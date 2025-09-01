package io.scriptor.eswin.esl;

import io.scriptor.eswin.esl.tree.*;
import io.scriptor.eswin.grammar.Context;
import io.scriptor.eswin.grammar.Grammar;
import io.scriptor.eswin.grammar.Unroll;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Embedded Scripting Language
 */
public class EslGrammar extends Grammar<EslExpression> {

    // expression      = call | store | member | name | constant
    // call            = member '(' ( expression ',' )* expression? ')'
    // store           = member '=' expression
    // member          = name ( '.' name )+
    // name            = ( letter | '_' ) ( letter | digit | '_' )*
    // constant        = constant_string | constant_char | constant_int | constant_float
    // constant_string = '"' (!'"')* '"'
    // constant_char   = ''' (!''') '''
    // constant_int    = '0' | ( no-zero-digit digit* )
    // constant_float  = ( constant_int '.' constant_int? ) | ( constant_int? '.' constant_int )

    @Override
    protected @NotNull EslExpression parseRoot(final @NotNull Context context) throws Unroll {
        return expression(context);
    }

    protected @NotNull EslExpression expression(final @NotNull Context context) throws Unroll {
        return parseUnionOf(context, this::call, this::store, this::member, this::name, this::constant);
    }

    protected @NotNull EslCallExpression call(final @NotNull Context context) throws Unroll {
        return wrap(ctx -> {
            final var callee = member(ctx);

            ctx.expect('(', true);

            final List<EslExpression> arguments = new ArrayList<>();

            arguments.addAll(parseZeroOrMoreOf(ctx, wrap(ctx1 -> {
                final var argument = expression(ctx1);
                ctx1.expect(',', true);
                return argument;
            })));

            parseZeroOrOneOf(ctx, this::expression).ifPresent(arguments::add);

            ctx.expect(')', true);

            return new EslCallExpression(callee, arguments.toArray(EslExpression[]::new));
        }).parse(context);
    }

    protected @NotNull EslStoreExpression store(final @NotNull Context context) throws Unroll {
        return wrap(ctx -> {
            final var dst = member(ctx);
            ctx.expect('=', true);
            final var src = expression(ctx);

            return new EslStoreExpression(dst, src);
        }).parse(context);
    }

    protected @NotNull EslExpression member(final @NotNull Context context) throws Unroll {
        return wrap(ctx -> {
            final var name = name(ctx);
            final var segments = parseOneOrMoreOf(ctx, wrap(ctx1 -> {
                ctx1.expect('.', true);
                return name(ctx1);
            }));

            EslExpression object = name;
            for (final var segment : segments)
                object = new EslMemberExpression(object, segment.value());

            return object;
        }).parse(context);
    }

    protected @NotNull EslNameExpression name(final @NotNull Context context) throws Unroll {
        return wrap(ctx -> {
            parseZeroOrMoreOf(ctx, this::whitespace);
            final var fst = parseUnionOf(ctx, this::letter, ctx1 -> ctx1.expect('_', false));
            final var snd = parseZeroOrMoreOf(
                    ctx,
                    ctx1 -> parseUnionOf(ctx1, this::letter, this::digit, ctx2 -> ctx2.expect('_', false)));

            final List<Integer> data = new ArrayList<>();
            data.add(fst);
            data.addAll(snd);

            final var codepoints = data.stream().mapToInt(Integer::intValue).toArray();
            final var value      = new String(codepoints, 0, codepoints.length);

            return new EslNameExpression(value);
        }).parse(context);
    }

    protected @NotNull EslConstant constant(final @NotNull Context context) throws Unroll {
        return parseUnionOf(context, this::constantString, this::constantChar, this::constantInt, this::constantFloat);
    }

    protected @NotNull EslConstantString constantString(final @NotNull Context context) throws Unroll {
        return wrap(ctx -> {
            ctx.expect('"', true);
            final var data = parseZeroOrMoreOf(ctx, ctx1 -> ctx1.expectNot('"'));
            ctx.expect('"', false);

            final var codepoints = data.stream().mapToInt(Integer::intValue).toArray();
            final var value      = new String(codepoints, 0, codepoints.length);

            return new EslConstantString(value);
        }).parse(context);
    }

    protected @NotNull EslConstantChar constantChar(final @NotNull Context context) throws Unroll {
        return wrap(ctx -> {
            ctx.expect('\'', true);
            final var value = ctx.expect('\'', false);
            ctx.expect('\'', false);

            return new EslConstantChar(value);
        }).parse(context);
    }

    protected @NotNull EslConstantInt constantInt(final @NotNull Context context) throws Unroll {
        return wrap(ctx -> {

            final var data = parseUnionOf(
                    ctx,
                    ctx1 -> List.of(ctx1.expect('0', true)),
                    wrap(ctx1 -> {
                        final List<Integer> digits = new ArrayList<>();

                        digits.addAll(parseZeroOrMoreOf(ctx1, this::nonZeroDigit));
                        digits.add(digit(ctx1));

                        return digits;
                    }));

            final var codepoints = data.stream().mapToInt(Integer::intValue).toArray();
            final var string     = new String(codepoints, 0, codepoints.length);

            final var value = Long.parseLong(string);

            return new EslConstantInt(value);
        }).parse(context);
    }

    protected @NotNull EslConstantFloat constantFloat(final @NotNull Context context) throws Unroll {
        return parseUnionOf(
                context,
                wrap(ctx -> {
                    parseZeroOrMoreOf(ctx, this::whitespace);

                    final var fst = constantInt(ctx);
                    ctx.expect('.', false);
                    final var snd = parseZeroOrOneOf(ctx, this::constantInt);

                    final var string = "%d.%d".formatted(fst.value(), snd.map(EslConstantInt::value).orElse(0L));
                    return new EslConstantFloat(Double.parseDouble(string));
                }),
                wrap(ctx -> {
                    parseZeroOrMoreOf(ctx, this::whitespace);

                    final var fst = parseZeroOrOneOf(ctx, this::constantInt);
                    ctx.expect('.', false);
                    final var snd = constantInt(ctx);

                    final var string = "%d.%d".formatted(fst.map(EslConstantInt::value).orElse(0L), snd.value());
                    return new EslConstantFloat(Double.parseDouble(string));
                }));
    }

    protected int letter(final @NotNull Context context) throws Unroll {
        final var mark = context.index();
        if (!Character.isLetter(context.get()))
            throw new Unroll(context, mark);
        return context.skip();
    }

    protected int digit(final @NotNull Context context) throws Unroll {
        final var mark = context.index();
        if (!Character.isDigit(context.get()))
            throw new Unroll(context, mark);
        return context.skip();
    }

    protected int nonZeroDigit(final @NotNull Context context) throws Unroll {
        final var mark = context.index();
        if (context.get() == '0' || !Character.isDigit(context.get()))
            throw new Unroll(context, mark);
        return context.skip();
    }

    protected int whitespace(final @NotNull Context context) throws Unroll {
        if (!Character.isWhitespace(context.get()))
            throw new Unroll(context, context.index());
        return context.skip();
    }
}
