package io.scriptor.eswin.component.attribute;

import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.function.Function;

public final class AttributeUtil {

    private AttributeUtil() {
    }

    @MagicConstant(valuesFromClass = SwingConstants.class)
    public static int parseSwing(final @NotNull String value) {
        return switch (value) {
            case "center" -> SwingConstants.CENTER;
            case "top" -> SwingConstants.TOP;
            case "left" -> SwingConstants.LEFT;
            case "bottom" -> SwingConstants.BOTTOM;
            case "right" -> SwingConstants.RIGHT;
            case "north" -> SwingConstants.NORTH;
            case "north-east" -> SwingConstants.NORTH_EAST;
            case "east" -> SwingConstants.EAST;
            case "south-east" -> SwingConstants.SOUTH_EAST;
            case "south" -> SwingConstants.SOUTH;
            case "south-west" -> SwingConstants.SOUTH_WEST;
            case "west" -> SwingConstants.WEST;
            case "north-west" -> SwingConstants.NORTH_WEST;
            case "horizontal" -> SwingConstants.HORIZONTAL;
            case "vertical" -> SwingConstants.VERTICAL;
            case "leading" -> SwingConstants.LEADING;
            case "trailing" -> SwingConstants.TRAILING;
            case "next" -> SwingConstants.NEXT;
            case "previous" -> SwingConstants.PREVIOUS;
            default -> throw new NoSuchElementException("no swing constant '%s'".formatted(value));
        };
    }

    public static float parseAlignment(final @NotNull String value) {
        return switch (value) {
            case "top" -> JComponent.TOP_ALIGNMENT;
            case "center" -> JComponent.CENTER_ALIGNMENT;
            case "bottom" -> JComponent.BOTTOM_ALIGNMENT;
            case "left" -> JComponent.LEFT_ALIGNMENT;
            case "right" -> JComponent.RIGHT_ALIGNMENT;
            default -> throw new NoSuchElementException("no alignment constant '%s'".formatted(value));
        };
    }

    public static int parseAnchor(final @NotNull String value) {
        return switch (value) {
            case "center" -> GridBagConstraints.CENTER;
            case "north" -> GridBagConstraints.NORTH;
            case "north-east" -> GridBagConstraints.NORTHEAST;
            case "east" -> GridBagConstraints.EAST;
            case "south-east" -> GridBagConstraints.SOUTHEAST;
            case "south" -> GridBagConstraints.SOUTH;
            case "south-west" -> GridBagConstraints.SOUTHWEST;
            case "west" -> GridBagConstraints.WEST;
            case "north-west" -> GridBagConstraints.NORTHWEST;
            case "page-start" -> GridBagConstraints.PAGE_START;
            case "page-end" -> GridBagConstraints.PAGE_END;
            case "line-start" -> GridBagConstraints.LINE_START;
            case "line-end" -> GridBagConstraints.LINE_END;
            case "first-line-start" -> GridBagConstraints.FIRST_LINE_START;
            case "first-line-end" -> GridBagConstraints.FIRST_LINE_END;
            case "last-line-start" -> GridBagConstraints.LAST_LINE_START;
            case "last-line-end" -> GridBagConstraints.LAST_LINE_END;
            case "baseline" -> GridBagConstraints.BASELINE;
            case "baseline-leading" -> GridBagConstraints.BASELINE_LEADING;
            case "baseline-trailing" -> GridBagConstraints.BASELINE_TRAILING;
            case "above-baseline" -> GridBagConstraints.ABOVE_BASELINE;
            case "above-baseline-leading" -> GridBagConstraints.ABOVE_BASELINE_LEADING;
            case "above-baseline-trailing" -> GridBagConstraints.ABOVE_BASELINE_TRAILING;
            case "below-baseline" -> GridBagConstraints.BELOW_BASELINE;
            case "below-baseline-leading" -> GridBagConstraints.BELOW_BASELINE_LEADING;
            case "below-baseline-trailing" -> GridBagConstraints.BELOW_BASELINE_TRAILING;
            default -> throw new NoSuchElementException(value);
        };
    }

    public static int parseFill(final @NotNull String value) {
        return switch (value) {
            case "none" -> GridBagConstraints.NONE;
            case "horizontal" -> GridBagConstraints.HORIZONTAL;
            case "vertical" -> GridBagConstraints.VERTICAL;
            case "both" -> GridBagConstraints.BOTH;
            default -> throw new NoSuchElementException(value);
        };
    }

    public static int parseSize(final @NotNull String value) {
        return switch (value) {
            case "relative" -> GridBagConstraints.RELATIVE;
            case "remainder" -> GridBagConstraints.REMAINDER;
            default -> Integer.parseUnsignedInt(value);
        };
    }

    public static int parseOffset(final @NotNull String value) {
        return value.equals("relative")
               ? GridBagConstraints.RELATIVE
               : Integer.parseUnsignedInt(value);
    }

    public static <T> boolean getComponents(
            final @NotNull AttributeSet attributes,
            final @NotNull String name,
            final @NotNull String name0,
            final @NotNull String name1,
            final @NotNull T[] output,
            final @NotNull Function<String, T> mapper
    ) {
        return getComponents(attributes, name, name0, name1, output, 0, mapper);
    }

    public static <T> boolean getComponents(
            final @NotNull AttributeSet attributes,
            final @NotNull String name,
            final @NotNull String name0,
            final @NotNull String name1,
            final @NotNull T[] output,
            final int offset,
            final @NotNull Function<String, T> mapper
    ) {
        if (attributes.has(name)) {
            final var value = attributes.get(name);
            final var values = Arrays
                    .stream(value.trim().split("\\s+"))
                    .map(mapper)
                    .toList();

            switch (values.size()) {
                case 1 -> output[offset] = output[offset + 1] = values.get(0);
                case 2 -> {
                    output[offset] = values.get(0);
                    output[offset + 1] = values.get(1);
                }
                default -> throw new IllegalStateException();
            }

            return true;
        }

        var has = false;
        if (attributes.has(name0)) {
            final var value = attributes.get(name0);
            output[offset] = mapper.apply(value);
            has = true;
        }
        if (attributes.has(name1)) {
            final var value = attributes.get(name1);
            output[offset + 1] = mapper.apply(value);
            has = true;
        }

        return has;
    }

    public static <T> boolean getComponents(
            final @NotNull AttributeSet attributes,
            final @NotNull String name,
            final @NotNull String name0,
            final @NotNull String name1,
            final @NotNull String name2,
            final @NotNull String name3,
            final @NotNull T[] output,
            final @NotNull Function<String, T> mapper
    ) {
        if (attributes.has(name)) {
            final var value = attributes.get(name);
            final var values = Arrays
                    .stream(value.trim().split("\\s+"))
                    .map(mapper)
                    .toList();

            switch (values.size()) {
                case 1 -> output[0] = output[1] = output[2] = output[3] = values.get(0);
                case 2 -> {
                    output[0] = output[2] = values.get(0);
                    output[1] = output[3] = values.get(1);
                }
                case 3 -> {
                    output[0] = values.get(0);
                    output[1] = output[3] = values.get(1);
                    output[2] = values.get(2);
                }
                case 4 -> {
                    output[0] = values.get(0);
                    output[1] = values.get(1);
                    output[2] = values.get(2);
                    output[3] = values.get(3);
                }
                default -> throw new IllegalStateException();
            }

            return true;
        }

        var has = false;
        if (attributes.has(name0)) {
            final var value = attributes.get(name0);
            output[0] = mapper.apply(value);
            has = true;
        }
        if (attributes.has(name1)) {
            final var value = attributes.get(name1);
            output[1] = mapper.apply(value);
            has = true;
        }
        if (attributes.has(name2)) {
            final var value = attributes.get(name2);
            output[2] = mapper.apply(value);
            has = true;
        }
        if (attributes.has(name3)) {
            final var value = attributes.get(name3);
            output[3] = mapper.apply(value);
            has = true;
        }

        return has;
    }

    public static <T> boolean getComponents(
            final @NotNull AttributeSet attributes,
            final @NotNull String name,
            final @NotNull String name01,
            final @NotNull String name23,
            final @NotNull String name0,
            final @NotNull String name1,
            final @NotNull String name2,
            final @NotNull String name3,
            final @NotNull T[] output,
            final @NotNull Function<String, T> mapper01,
            final @NotNull Function<String, T> mapper23
    ) {
        if (attributes.has(name)) {
            final var value    = attributes.get(name);
            final var segments = Arrays.stream(value.trim().split("\\s+")).toList();

            switch (segments.size()) {
                case 2 -> {
                    output[0] = output[1] = mapper01.apply(segments.get(0));
                    output[2] = output[3] = mapper23.apply(segments.get(1));
                }
                case 4 -> {
                    output[0] = mapper01.apply(segments.get(0));
                    output[1] = mapper01.apply(segments.get(1));
                    output[2] = mapper23.apply(segments.get(2));
                    output[3] = mapper23.apply(segments.get(3));
                }
                default -> throw new IllegalStateException();
            }

            return true;
        }

        var has = false;
        has |= getComponents(attributes, name01, name0, name1, output, 0, mapper01);
        has |= getComponents(attributes, name23, name2, name3, output, 2, mapper23);

        return has;
    }

    public static boolean getInsets(
            final @NotNull AttributeSet attributes,
            final @NotNull String name,
            final @NotNull Insets insets
    ) {
        final var output = new Integer[] { 0, 0, 0, 0 };
        if (!getComponents(attributes,
                           name,
                           name + "-top",
                           name + "-left",
                           name + "-bottom",
                           name + "-right",
                           output,
                           Integer::parseInt
        ))
            return false;

        insets.set(output[0], output[1], output[2], output[3]);
        return true;
    }
}
