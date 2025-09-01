package io.scriptor.eswin.grammar;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

public class Context {

    private final int[] buffer;
    private int index = 0;

    public Context(final int @NotNull [] buffer) {
        this.buffer = buffer;
    }

    public Context(final @NotNull String string) {
        this.buffer = string.codePoints().toArray();
    }

    public Context(final @NotNull InputStream stream) throws IOException {
        final var wrapped = new PushbackInputStream(stream);
        final var charset = Encoding.detect(wrapped);
        final var bytes   = wrapped.readAllBytes();

        this.buffer = new String(bytes, charset).codePoints().toArray();
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
        if (index >= buffer.length)
            return -1;
        return buffer[index++];
    }

    public int get() {
        if (index >= buffer.length)
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
