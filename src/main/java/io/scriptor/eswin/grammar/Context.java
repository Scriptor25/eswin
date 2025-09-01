package io.scriptor.eswin.grammar;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

public class Context {

    private final int[] buffer;
    private int index;
    private int end;

    public Context(final int @NotNull [] buffer) {
        this(buffer, 0, buffer.length);
    }

    public Context(final @NotNull String string) {
        this.buffer = string.codePoints().toArray();
        this.index = 0;
        this.end = this.buffer.length;
    }

    public Context(final @NotNull InputStream stream) throws IOException {
        final var wrapped = new PushbackInputStream(stream);
        final var charset = Encoding.detect(wrapped);
        final var bytes   = wrapped.readAllBytes();

        this.buffer = new String(bytes, charset).codePoints().toArray();
        this.index = 0;
        this.end = this.buffer.length;
    }

    public Context(final int @NotNull [] buffer, final int offset, final int length) {
        this.buffer = buffer;
        this.index = offset;
        this.end = offset + length;
    }

    public Context(final @NotNull String string, final int offset) {
        this.buffer = string.codePoints().toArray();
        this.index = offset;
        this.end = this.buffer.length;
    }

    public Context(final @NotNull String string, final int offset, final int length) {
        this.buffer = string.codePoints().toArray();
        this.index = offset;
        this.end = offset + length;
    }

    public Context(final @NotNull InputStream stream, final int offset, final int length) throws IOException {
        final var wrapped = new PushbackInputStream(stream);
        final var charset = Encoding.detect(wrapped);
        final var bytes   = wrapped.readAllBytes();

        this.buffer = new String(bytes, charset).codePoints().toArray();
        this.index = offset;
        this.end = offset + length;
    }

    public int index() {
        return index;
    }

    public int @NotNull [] buffer() {
        return buffer;
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

    public boolean skipif(final int codepoint, boolean ignoreWhitespace) {
        if (ignoreWhitespace)
            while (Character.isWhitespace(get()))
                skip();
        if (get() != codepoint)
            return false;
        skip();
        return true;
    }

    public int expect(final int codepoint, boolean ignoreWhitespace) throws Unroll {
        if (ignoreWhitespace)
            while (Character.isWhitespace(get()))
                skip();
        if (get() != codepoint) {
            throw new Unroll(buffer, index);
        }
        return skip();
    }

    public int expectNot(final int codepoint) throws Unroll {
        if (get() == codepoint) {
            throw new Unroll(buffer, index);
        }
        return skip();
    }
}
