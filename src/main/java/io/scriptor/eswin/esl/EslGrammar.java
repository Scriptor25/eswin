package io.scriptor.eswin.esl;

import io.scriptor.eswin.esl.runtime.*;
import io.scriptor.eswin.esl.tree.*;
import io.scriptor.eswin.grammar.GrammarContext;
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
    protected @NotNull EslExpression parseRoot(final @NotNull GrammarContext context) throws Unroll {
        return expression(context);
    }

    protected @NotNull EslExpression expression(final @NotNull GrammarContext context) throws Unroll {
        return parseUnionOf(context, this::call, this::store, this::member, this::name, this::constant);
    }

    protected @NotNull EslCallExpression call(final @NotNull GrammarContext context) throws Unroll {
        return wrap(ctx -> {
            final var callee = member(ctx);

            ctx.whitespace();
            ctx.expect('(');

            final List<EslExpression> arguments = new ArrayList<>();

            arguments.addAll(parseZeroOrMoreOf(ctx, wrap(ctx1 -> {
                final var argument = expression(ctx1);

                ctx1.whitespace();
                ctx1.expect(',');

                return argument;
            })));

            parseZeroOrOneOf(ctx, this::expression).ifPresent(arguments::add);

            ctx.whitespace();
            ctx.expect(')');

            return new EslCallExpression(callee, arguments.toArray(EslExpression[]::new));
        }).parse(context);
    }

    protected @NotNull EslStoreExpression store(final @NotNull GrammarContext context) throws Unroll {
        return wrap(ctx -> {
            final var dst = member(ctx);

            ctx.whitespace();
            ctx.expect('=');

            final var src = expression(ctx);

            return new EslStoreExpression(dst, src);
        }).parse(context);
    }

    protected @NotNull EslExpression member(final @NotNull GrammarContext context) throws Unroll {
        return wrap(ctx -> {
            final var name = name(ctx);
            final var segments = parseOneOrMoreOf(ctx, wrap(ctx1 -> {
                ctx1.whitespace();
                ctx1.expect('.');
                return name(ctx1);
            }));

            EslExpression object = name;
            for (final var segment : segments)
                object = new EslMemberExpression(object, segment.value());

            return object;
        }).parse(context);
    }

    protected @NotNull EslNameExpression name(final @NotNull GrammarContext context) throws Unroll {
        context.whitespace();
        return wrap(ctx -> {
            final var fst = parseUnionOf(ctx, this::letter, ctx1 -> ctx1.expect('_'));
            final var snd = parseZeroOrMoreOf(
                    ctx,
                    ctx1 -> parseUnionOf(ctx1, this::letter, this::digit, ctx2 -> ctx2.expect('_')));

            final List<Integer> data = new ArrayList<>();
            data.add(fst);
            data.addAll(snd);

            final var codepoints = data.stream().mapToInt(Integer::intValue).toArray();
            final var value      = new String(codepoints, 0, codepoints.length);

            return new EslNameExpression(value);
        }).parse(context);
    }

    protected @NotNull EslConstant constant(final @NotNull GrammarContext context) throws Unroll {
        return parseUnionOf(context, this::constantString, this::constantChar, this::constantInt, this::constantFloat);
    }

    protected @NotNull EslConstantString constantString(final @NotNull GrammarContext context) throws Unroll {
        context.whitespace();
        return wrap(ctx -> {
            ctx.expect('"');
            final var data = parseZeroOrMoreOf(ctx, ctx1 -> ctx1.expectNot('"'));
            ctx.expect('"');

            final var codepoints = data.stream().mapToInt(Integer::intValue).toArray();
            final var value      = new String(codepoints, 0, codepoints.length);

            return new EslConstantString(value);
        }).parse(context);
    }

    protected @NotNull EslConstantChar constantChar(final @NotNull GrammarContext context) throws Unroll {
        context.whitespace();
        return wrap(ctx -> {
            ctx.expect('\'');
            final var value = ctx.expect('\'');
            ctx.expect('\'');

            return new EslConstantChar(value);
        }).parse(context);
    }

    protected @NotNull EslConstantInt constantInt(final @NotNull GrammarContext context) throws Unroll {
        context.whitespace();
        return wrap(ctx -> {

            final var data = parseUnionOf(
                    ctx,
                    ctx1 -> List.of(ctx1.expect('0')),
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

    protected @NotNull EslConstantFloat constantFloat(final @NotNull GrammarContext context) throws Unroll {
        context.whitespace();
        return parseUnionOf(
                context,
                wrap(ctx -> {
                    final var fst = constantInt(ctx);
                    ctx.expect('.');
                    final var snd = parseZeroOrOneOf(ctx, this::constantInt);

                    final var string = "%d.%d".formatted(fst.val(), snd.map(EslConstantInt::val).orElse(0L));
                    return new EslConstantFloat(Double.parseDouble(string));
                }),
                wrap(ctx -> {
                    final var fst = parseZeroOrOneOf(ctx, this::constantInt);
                    ctx.expect('.');
                    final var snd = constantInt(ctx);

                    final var string = "%d.%d".formatted(fst.map(EslConstantInt::val).orElse(0L), snd.val());
                    return new EslConstantFloat(Double.parseDouble(string));
                }));
    }

    protected int letter(final @NotNull GrammarContext context) throws Unroll {
        final var mark = context.index();
        if (!Character.isLetter(context.get()))
            throw new Unroll(context, mark);
        return context.skip();
    }

    protected int digit(final @NotNull GrammarContext context) throws Unroll {
        final var mark = context.index();
        if (!Character.isDigit(context.get()))
            throw new Unroll(context, mark);
        return context.skip();
    }

    protected int nonZeroDigit(final @NotNull GrammarContext context) throws Unroll {
        final var mark = context.index();
        if (context.get() == '0' || !Character.isDigit(context.get()))
            throw new Unroll(context, mark);
        return context.skip();
    }
}
