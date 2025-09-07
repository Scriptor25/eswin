package io.scriptor.eswin.xml;

import io.scriptor.eswin.grammar.GrammarContext;
import io.scriptor.eswin.grammar.Grammar;
import io.scriptor.eswin.grammar.Unroll;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class XmlGrammar extends Grammar<XmlDocument> {

    /*
     * document    = instruction* element
     * instruction = '<?' identifier attribute* '?>'
     * element     = '<' identifier attribute* '>' ( element | text | cdata )* '</' identifier '>'
     * attribute   = identifier '=' string
     * string      = '"' (!'"')* '"'
     * text        = ( whitespace | identifier | symbol )+
     * symbol      = '&' identifier ';'
     * identifier  = ( digit | letter | emoji | ':' | '-' | '.' | '_' )+
     * cdata       = '<![CDATA[' (!']') ']]>'
     */

    protected static boolean isIdentifier(final int character) {
        return Character.isLetter(character)
               || Character.isDigit(character)
               || Character.isEmoji(character)
               || character == '.'
               || character == ':'
               || character == '-'
               || character == '_';
    }

    protected static final Map<String, Integer> ENTITY_LOOKUP;

    static {
        ENTITY_LOOKUP = new HashMap<>();
        ENTITY_LOOKUP.put("lt", (int) '<');
        ENTITY_LOOKUP.put("gt", (int) '>');
        ENTITY_LOOKUP.put("amp", (int) '&');
        ENTITY_LOOKUP.put("apos", (int) '\'');
        ENTITY_LOOKUP.put("quot", (int) '"');
    }

    @Override
    protected XmlDocument parseRoot(final @NotNull GrammarContext context) throws Unroll {
        return wrap(ctx -> {
            final var instructions = parseZeroOrMoreOf(ctx, this::instruction);
            parseZeroOrMoreOf(ctx, this::comment);
            final var root = element(ctx);

            return new XmlDocument(instructions.toArray(XmlInstruction[]::new), root);
        }).parse(context);
    }

    protected XmlInstruction instruction(final @NotNull GrammarContext context) throws Unroll {
        context.whitespace();
        return wrap(ctx -> {
            ctx.expect("<?");

            final var name       = identifier(ctx);
            final var attributes = parseZeroOrMoreOf(ctx, this::attribute);

            ctx.whitespace();
            ctx.expect("?>");

            return new XmlInstruction(name, attributes.toArray(XmlAttribute[]::new));
        }).parse(context);
    }

    protected XmlElement element(final @NotNull GrammarContext context) throws Unroll {
        context.whitespace();
        return wrap(ctx -> {
            final var mark = ctx.index();

            ctx.expect('<');

            final var begin      = identifier(ctx);
            final var attributes = parseZeroOrMoreOf(ctx, this::attribute);

            ctx.whitespace();
            if (ctx.skipIf('/')) {
                ctx.expect('>');
                return new XmlElement(begin, attributes.toArray(XmlAttribute[]::new), new XmlBase[0]);
            }

            ctx.expect('>');

            final Collection<XmlBase> children = parseZeroOrMoreOf(
                    ctx,
                    context1 -> parseUnionOf(context1, this::comment, this::cdata, this::element, this::text));

            ctx.whitespace();
            ctx.expect("</");

            final var end = identifier(ctx);

            ctx.whitespace();
            ctx.expect('>');

            if (!end.equals(begin))
                throw new Unroll(ctx, mark);

            return new XmlElement(begin, attributes.toArray(XmlAttribute[]::new), children.toArray(XmlBase[]::new));
        }).parse(context);
    }

    protected XmlComment comment(final @NotNull GrammarContext context) throws Unroll {
        context.whitespace();
        return wrap(ctx -> {
            ctx.expect("<!--");

            final var elements   = parseZeroOrMoreOf(ctx, ctx1 -> ctx1.expectNot("-->"));
            final var codepoints = elements.stream().mapToInt(Integer::intValue).toArray();
            final var value      = new String(codepoints, 0, codepoints.length);

            ctx.expect("-->");

            return new XmlComment(value);
        }).parse(context);
    }

    protected XmlText cdata(final @NotNull GrammarContext context) throws Unroll {
        context.whitespace();
        return wrap(ctx -> {
            ctx.expect("<![CDATA[");

            final var elements = parseZeroOrMoreOf(ctx, ctx1 -> ctx1.expectNot("]]>"));
            ctx.expect("]]>");

            final var codepoints = elements.stream().mapToInt(Integer::intValue).toArray();
            final var value      = new String(codepoints, 0, codepoints.length);

            return new XmlText(value);
        }).parse(context);
    }

    protected XmlText text(final @NotNull GrammarContext context) throws Unroll {
        return wrap(ctx -> {
            final var mark = ctx.index();

            final var elements = parseOneOrMoreOf(
                    ctx,
                    ctx1 -> parseUnionOf(ctx1, this::entity, ctx2 -> ctx2.expectNot('<')));

            final var codepoints = elements.stream().mapToInt(Integer::intValue).toArray();
            final var value      = new String(codepoints, 0, codepoints.length);

            if (value.isBlank())
                throw new Unroll(ctx, mark);

            return new XmlText(value.trim());
        }).parse(context);
    }

    protected Integer entity(final @NotNull GrammarContext context) throws Unroll {
        return wrap(ctx -> {
            final var mark = ctx.index();

            ctx.expect('&');
            final var name = identifier(ctx);
            ctx.expect(';');

            if (!ENTITY_LOOKUP.containsKey(name))
                throw new Unroll(ctx, mark);

            return ENTITY_LOOKUP.get(name);
        }).parse(context);
    }

    protected XmlAttribute attribute(final @NotNull GrammarContext context) throws Unroll {
        context.whitespace();
        return wrap(ctx -> {
            final var name = identifier(ctx);

            ctx.whitespace();
            ctx.expect('=');
            ctx.whitespace();

            final var value = string(ctx);

            return new XmlAttribute(name, value);
        }).parse(context);
    }

    protected String identifier(final @NotNull GrammarContext context) throws Unroll {
        return wrap(ctx -> {
            final var elements = parseOneOrMoreOf(ctx, ctx1 -> {
                if (!isIdentifier(ctx1.get()))
                    throw new Unroll(ctx1, ctx1.index());
                return ctx1.skip();
            });

            final var codepoints = elements.stream().mapToInt(Integer::intValue).toArray();
            return new String(codepoints, 0, codepoints.length);
        }).parse(context);
    }

    protected String string(final @NotNull GrammarContext context) throws Unroll {
        return wrap(ctx -> {
            ctx.expect('"');
            final var elements = parseZeroOrMoreOf(
                    ctx,
                    ctx1 -> parseUnionOf(ctx1, this::entity, ctx2 -> ctx2.expectNot('"')));
            ctx.expect('"');

            final var codepoints = elements.stream().mapToInt(Integer::intValue).toArray();
            return new String(codepoints, 0, codepoints.length);
        }).parse(context);
    }
}
