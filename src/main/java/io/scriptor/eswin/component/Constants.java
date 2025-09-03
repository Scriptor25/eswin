package io.scriptor.eswin.component;

import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.NoSuchElementException;

public class Constants {

    public static final boolean DEBUG = false;

    @MagicConstant(valuesFromClass = SwingConstants.class)
    public static int getSwing(final @NotNull String value) {
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

    public static float getAlignment(final @NotNull String value) {
        return switch (value) {
            case "top" -> JComponent.TOP_ALIGNMENT;
            case "center" -> JComponent.CENTER_ALIGNMENT;
            case "bottom" -> JComponent.BOTTOM_ALIGNMENT;
            case "left" -> JComponent.LEFT_ALIGNMENT;
            case "right" -> JComponent.RIGHT_ALIGNMENT;
            default -> throw new NoSuchElementException("no alignment constant '%s'".formatted(value));
        };
    }

    public static int getAnchor(final @NotNull String value) {
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

    public static int getFill(final @NotNull String value) {
        return switch (value) {
            case "none" -> GridBagConstraints.NONE;
            case "horizontal" -> GridBagConstraints.HORIZONTAL;
            case "vertical" -> GridBagConstraints.VERTICAL;
            case "both" -> GridBagConstraints.BOTH;
            default -> throw new NoSuchElementException(value);
        };
    }

    public static int getSize(final @NotNull String value) {
        return switch (value) {
            case "relative" -> GridBagConstraints.RELATIVE;
            case "remainder" -> GridBagConstraints.REMAINDER;
            default -> Integer.parseUnsignedInt(value, 10);
        };
    }

    public static boolean getInsets(
            final @NotNull AttributeSet attributes,
            final @NotNull String name,
            final @NotNull Insets insets
    ) {
        if (attributes.has(name)) {
            final var value = attributes.get(name);
            final var values = Arrays
                    .stream(value.trim().split("\\s+"))
                    .mapToInt(Integer::parseInt)
                    .toArray();

            switch (values.length) {
                case 1 -> insets.top = insets.left = insets.bottom = insets.right = values[0];
                case 2 -> {
                    insets.top = insets.bottom = values[0];
                    insets.left = insets.right = values[1];
                }
                case 3 -> {
                    insets.top = values[0];
                    insets.left = insets.right = values[1];
                    insets.bottom = values[2];
                }
                case 4 -> {
                    insets.top = values[0];
                    insets.left = values[1];
                    insets.bottom = values[2];
                    insets.right = values[3];
                }
                default -> throw new IllegalStateException();
            }

            return true;
        }

        var has = false;
        if (attributes.has("%s-top".formatted(name))) {
            final var value = attributes.get("%s-top".formatted(name));
            insets.top = Integer.parseInt(value);
            has = true;
        }
        if (attributes.has("%s-left".formatted(name))) {
            final var value = attributes.get("%s-left".formatted(name));
            insets.left = Integer.parseInt(value);
            has = true;
        }
        if (attributes.has("%s-bottom".formatted(name))) {
            final var value = attributes.get("%s-bottom".formatted(name));
            insets.bottom = Integer.parseInt(value);
            has = true;
        }
        if (attributes.has("%s-right".formatted(name))) {
            final var value = attributes.get("%s-right".formatted(name));
            insets.right = Integer.parseInt(value);
            has = true;
        }

        return has;
    }
}
