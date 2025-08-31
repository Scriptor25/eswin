package io.scriptor.eswin.xml;

import io.scriptor.eswin.xml.document.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.*;

public class Parser {

    /*
     * document   = prologues? root
     * prologues     = '<' '?' identifier attribute* '?' '>'
     * root    = '<' identifier attribute* '>' ( root | text )* '<' '/' identifier '>'
     * attribute  = identifier '=' string
     * string     = '"' (!'"')* '"'
     * text       = ( whitespace | identifier | symbol )+
     * symbol     = '&' identifier ';'
     * identifier = ( digit | letter | emoji | ':' | '-' | '.' | '_' )+
     */

    public static boolean isIdentifier(final int character) {
        return Character.isLetter(character)
               || Character.isDigit(character)
               || Character.isEmoji(character)
               || character == '.'
               || character == ':'
               || character == '-'
               || character == '_';
    }

    public static final Map<String, Integer> CODEPOINT_LOOKUP;

    static {
        CODEPOINT_LOOKUP = new HashMap<>();
        CODEPOINT_LOOKUP.put("lt", (int) '<');
        CODEPOINT_LOOKUP.put("gt", (int) '>');
        CODEPOINT_LOOKUP.put("amp", (int) '&');
        CODEPOINT_LOOKUP.put("apos", (int) '\'');
        CODEPOINT_LOOKUP.put("quot", (int) '"');
    }

    public Rule<Document> parseDocument;
    public Rule<Prologue> parsePrologue;
    public Rule<Element> parseElement;
    public Rule<Text> parseText;
    public Rule<Integer> parseSymbol;
    public Rule<Attribute> parseAttribute;
    public Rule<String> parseIdentifier;
    public Rule<String> parseString;

    private final int[] data;
    private int index;

    public Parser(final @NotNull InputStream stream) throws IOException {
        final var wrapped = new PushbackInputStream(stream);
        final var charset = Encoding.detect(wrapped);
        final var bytes   = wrapped.readAllBytes();

        this.data = new String(bytes, charset).codePoints().toArray();
        this.index = 0;

        this.parseDocument = wrap(() -> {
            final var prologues = parseZeroOrOneOf(parsePrologue);
            final var root      = parseElement.parse();

            return new Document(prologues, root);
        });

        this.parsePrologue = wrap(() -> {
            expect('<', true);
            expect('?', false);
            final var name       = parseIdentifier.parse();
            final var attributes = parseZeroOrMoreOf(parseAttribute);
            expect('?', true);
            expect('>', false);

            return new Prologue(name, attributes.toArray(Attribute[]::new));
        });

        this.parseElement = wrap(() -> {
            final var mark = index;

            expect('<', true);
            final var begin      = parseIdentifier.parse();
            final var attributes = parseZeroOrMoreOf(parseAttribute);
            expect('>', true);
            final var children = parseZeroOrMoreOf(() -> this.<ElementBase>parseUnionOf(parseElement, parseText));
            expect('<', true);
            expect('/', false);
            final var end = parseIdentifier.parse();
            expect('>', true);

            if (!end.equals(begin))
                throw new Unroll(data, mark);

            return new Element(begin, attributes.toArray(Attribute[]::new), children.toArray(ElementBase[]::new));
        });

        this.parseText = wrap(() -> {
            final var mark = index;

            final var elements = parseOneOrMoreOf(() -> parseUnionOf(parseSymbol, () -> expectNot('<')));

            final var codepoints = elements.stream().mapToInt(Integer::intValue).toArray();
            final var value      = new String(codepoints, 0, codepoints.length);

            if (value.isBlank())
                throw new Unroll(data, mark);

            return new Text(value.trim());
        });

        this.parseSymbol = wrap(() -> {
            final var mark = index;

            expect('&', false);
            final var name = parseIdentifier.parse();
            expect(';', false);

            if (!CODEPOINT_LOOKUP.containsKey(name))
                throw new Unroll(data, mark);

            return CODEPOINT_LOOKUP.get(name);
        });

        this.parseAttribute = wrap(() -> {
            parseZeroOrMoreOf(this::parseWhitespace);
            final var name = parseIdentifier.parse();
            expect('=', true);
            final var value = parseString.parse();

            return new Attribute(name, value);
        });

        this.parseIdentifier = wrap(() -> {
            final var elements = parseOneOrMoreOf(() -> {
                if (!isIdentifier(get()))
                    throw new Unroll(data, index);
                return skip();
            });
            final var codepoints = elements.stream().mapToInt(Integer::intValue).toArray();
            return new String(codepoints, 0, codepoints.length);
        });

        this.parseString = wrap(() -> {
            expect('"', true);
            final var elements = parseZeroOrMoreOf(() -> parseUnionOf(parseSymbol, () -> expectNot('"')));
            expect('"', false);

            final var codepoints = elements.stream().mapToInt(Integer::intValue).toArray();
            return new String(codepoints, 0, codepoints.length);
        });
    }

    public Document parse() {
        try {
            return parseDocument.parse();
        } catch (final Unroll e) {
            throw new RuntimeException(e);
        }
    }

    public int skip() {
        if (index >= data.length)
            return -1;
        return data[index++];
    }

    public int get() {
        if (index >= data.length)
            return -1;
        return data[index];
    }

    public void expect(final int codepoint, boolean ignoreWhitespace) throws Unroll {
        if (ignoreWhitespace)
            while (Character.isWhitespace(get()))
                skip();
        if (get() != codepoint) {
            throw new Unroll(data, index);
        }
        skip();
    }

    public int expectNot(final int codepoint) throws Unroll {
        if (get() == codepoint) {
            throw new Unroll(data, index);
        }
        return skip();
    }

    public <R> @NotNull Rule<R> wrap(final @NotNull Rule<R> rule) {
        return () -> {
            final var mark = index;
            try {
                return rule.parse();
            } catch (final Unroll cause) {
                throw new Unroll(data, mark, cause);
            }
        };
    }

    public <R> @NotNull Collection<R> parseZeroOrOneOf(final @NotNull Rule<R> rule) {
        final List<R> data = new ArrayList<>();
        try {
            data.add(rule.parse());
        } catch (final Unroll unroll) {
            index = unroll.mark;
        }
        return data;
    }

    public <R> @NotNull Collection<R> parseOneOrMoreOf(final @NotNull Rule<R> rule) throws Unroll {
        final List<R> data = new ArrayList<>();
        data.add(rule.parse());
        while (true)
            try {
                data.add(rule.parse());
            } catch (final Unroll unroll) {
                index = unroll.mark;
                break;
            }
        return data;
    }

    public <R> @NotNull Collection<R> parseZeroOrMoreOf(final @NotNull Rule<R> rule) {
        final List<R> data = new ArrayList<>();
        while (true)
            try {
                data.add(rule.parse());
            } catch (final Unroll unroll) {
                index = unroll.mark;
                break;
            }
        return data;
    }

    @SafeVarargs
    public final <R> R parseUnionOf(final @NotNull Rule<? extends R>... rules) throws Unroll {
        final var mark = index;
        for (final var rule : rules) {
            try {
                return rule.parse();
            } catch (final Unroll unroll) {
                index = unroll.mark;
            }
        }
        throw new Unroll(data, mark);
    }

    public int parseWhitespace() throws Unroll {
        if (!Character.isWhitespace(get()))
            throw new Unroll(data, index);
        return skip();
    }
}
