package io.scriptor.eswin.grammar;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

public class GrammarContext {

    private final String filename;
    private final int[] buffer;
    private final int end;
    private int index;

    public GrammarContext(final @NotNull String filename, final int @NotNull [] buffer) {
        this(filename, buffer, 0, buffer.length);
    }

    public GrammarContext(final @NotNull String filename, final @NotNull String string) {
        this.filename = filename;
        this.buffer = string.codePoints().toArray();
        this.end = this.buffer.length;
        this.index = 0;
    }

    public GrammarContext(final @NotNull String filename, final @NotNull InputStream stream) throws IOException {
        final var wrapped = new PushbackInputStream(stream);
        final var charset = Encoding.detect(wrapped);
        final var bytes   = wrapped.readAllBytes();

        this.filename = filename;
        this.buffer = new String(bytes, charset).codePoints().toArray();
        this.end = this.buffer.length;
        this.index = 0;
    }

    public GrammarContext(
            final @NotNull String filename,
            final int @NotNull [] buffer,
            final int offset,
            final int length
    ) {
        this.filename = filename;
        this.buffer = buffer;
        this.end = offset + length;
        this.index = offset;
    }

    public GrammarContext(final @NotNull String filename, final @NotNull String string, final int offset) {
        this.filename = filename;
        this.buffer = string.codePoints().toArray();
        this.end = this.buffer.length;
        this.index = offset;
    }

    public GrammarContext(
            final @NotNull String filename,
            final @NotNull String string,
            final int offset,
            final int length
    ) {
        this.filename = filename;
        this.buffer = string.codePoints().toArray();
        this.end = offset + length;
        this.index = offset;
    }

    public GrammarContext(
            final @NotNull String filename,
            final @NotNull InputStream stream,
            final int offset,
            final int length
    ) throws IOException {
        final var wrapped = new PushbackInputStream(stream);
        final var charset = Encoding.detect(wrapped);
        final var bytes   = wrapped.readAllBytes();

        this.filename = filename;
        this.buffer = new String(bytes, charset).codePoints().toArray();
        this.end = offset + length;
        this.index = offset;
    }

    public int index() {
        return index;
    }

    public int @NotNull [] buffer() {
        return buffer;
    }

    public @NotNull String filename() {
        return filename;
    }

    public void index(final int index) {
        this.index = index;
    }

    public int skip() {
        if (index >= end)
            return -1;
        return buffer[index++];
    }

    public int get() {
        if (index >= end)
            return -1;
        return buffer[index];
    }

    public boolean skipIf(final int codepoint) {
        if (get() != codepoint)
            return false;
        skip();
        return true;
    }

    public int expect(final int codepoint) throws Unroll {
        if (get() != codepoint)
            throw new Unroll(filename, buffer, index);
        return skip();
    }

    public void expect(final @NotNull String string) throws Unroll {
        final var mark = index;
        for (final var codepoint : string.codePoints().toArray())
            if (skip() != codepoint)
                throw new Unroll(filename, buffer, mark);
    }

    public int expectNot(final int codepoint) throws Unroll {
        if (get() == codepoint)
            throw new Unroll(filename, buffer, index);
        return skip();
    }

    public int expectNot(final @NotNull String string) throws Unroll {
        final var mark = index;
        for (final var codepoint : string.codePoints().toArray()) {
            final var cp = skip();
            if (cp != codepoint) {
                this.index = mark;
                return skip();
            }
        }
        throw new Unroll(filename, buffer, mark);
    }

    public void whitespace() {
        while (Character.isWhitespace(get()))
            skip();
    }
}
