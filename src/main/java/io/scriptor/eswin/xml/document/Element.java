package io.scriptor.eswin.xml.document;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record Element(String name, Attribute[] attributes, ElementBase[] children) implements ElementBase {

    @Override
    public @NotNull String toString() {
        return stringify(0);
    }

    @Override
    public @NotNull String stringify(final int depth) {
        final var builder = new StringBuilder();
        builder.append('<').append(name);
        for (final var attribute : attributes) {
            builder.append(' ').append(attribute.stringify(0));
        }
        builder.append('>');
        for (final var element : children) {
            builder.append('\n').append(Printable.spacing(depth + 1)).append(element.stringify(depth + 1));
        }
        if (children.length > 0)
            builder.append('\n').append(Printable.spacing(depth));
        builder.append("</").append(name).append('>');
        return builder.toString();
    }

    public String text() {
        return Arrays.stream(children)
                     .filter(Text.class::isInstance)
                     .map(Text.class::cast)
                     .map(Text::value)
                     .collect(Collectors.joining());
    }

    public Stream<Element> elements() {
        return Arrays.stream(children)
                     .filter(Element.class::isInstance)
                     .map(Element.class::cast);
    }
}
