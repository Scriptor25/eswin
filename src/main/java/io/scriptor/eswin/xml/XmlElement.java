package io.scriptor.eswin.xml;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record XmlElement(String name, XmlAttribute[] attributes, XmlBase[] children) implements XmlBase {

    @Override
    public @NotNull String toString() {
        return stringify(0);
    }

    @Override
    public @NotNull String stringify(final int depth) {
        final var builder = new StringBuilder();
        builder.append('<').append(name);
        for (final var attribute : attributes)
            builder.append(' ').append(attribute.stringify(0));
        if (children.length == 0) {
            builder.append("/>");
            return builder.toString();
        }
        builder.append('>');
        for (final var element : children)
            builder.append('\n')
                   .append(XmlPrintable.spacing(depth + 1))
                   .append(element.stringify(depth + 1));
        builder.append('\n')
               .append(XmlPrintable.spacing(depth))
               .append("</")
               .append(name)
               .append('>');
        return builder.toString();
    }

    public String text() {
        return Arrays.stream(children)
                     .filter(XmlText.class::isInstance)
                     .map(XmlText.class::cast)
                     .map(XmlText::value)
                     .collect(Collectors.joining());
    }

    public Stream<XmlElement> elements() {
        return Arrays.stream(children)
                     .filter(XmlElement.class::isInstance)
                     .map(XmlElement.class::cast);
    }
}
