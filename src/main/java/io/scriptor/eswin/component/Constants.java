package io.scriptor.eswin.component;

import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
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

    @MagicConstant(valuesFromClass = BoxLayout.class)
    public static int getBoxLayout(final @NotNull String value) {
        return switch (value) {
            case "x" -> BoxLayout.X_AXIS;
            case "y" -> BoxLayout.Y_AXIS;
            case "line" -> BoxLayout.LINE_AXIS;
            case "page" -> BoxLayout.PAGE_AXIS;
            default -> throw new NoSuchElementException("no box layout constant '%s'".formatted(value));
        };
    }
}
