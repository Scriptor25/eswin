package io.scriptor.eswin.impl;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public final class Node {

    private static final int MARGIN = 10;
    private static final int PADDING = 10;

    public enum Region {
        NONE,
        CENTER,
        TOP,
        LEFT,
        BOTTOM,
        RIGHT,
        TOP_LEFT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT,
        TOP_RIGHT,
    }

    public enum Mode {
        NONE,
        MOVE,
        RESIZE,
    }

    public Color colorTable = new Color(0xB3B3B3);
    public Color colorHover = new Color(0x737373);
    public Color colorLabel = new Color(0x2B2B2B);
    public Color colorBorder = new Color(0x191919);
    public String label = "";
    public List<Attribute> attributes = new ArrayList<>();

    public int x = 0;
    public int y = 0;
    public int width = 0;
    public int height = 0;

    public int labelWidth = 0;
    public int labelHeight = 0;
    public int minWidth = 0;
    public int minHeight = 0;

    public Region region = Region.NONE;
    public Mode mode = Mode.NONE;

    public int dx, dy;

    public int top() {
        return y;
    }

    public int left() {
        return x;
    }

    public int bottom() {
        return y + height;
    }

    public int right() {
        return x + width;
    }

    public boolean active() {
        return mode != Mode.NONE;
    }

    public @NotNull Cursor getCursor() {
        return switch (region) {
            case NONE -> Cursor.getDefaultCursor();
            case CENTER -> Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
            case TOP -> Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
            case LEFT -> Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR);
            case BOTTOM -> Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR);
            case RIGHT -> Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
            case TOP_LEFT -> Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR);
            case BOTTOM_LEFT -> Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR);
            case BOTTOM_RIGHT -> Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR);
            case TOP_RIGHT -> Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR);
        };
    }

    public void paint(final @NotNull Graphics2D g) {
        labelWidth = g.getFontMetrics().stringWidth(label);
        labelHeight = g.getFontMetrics().getHeight();

        minWidth = labelWidth + PADDING * 2;
        minHeight = labelHeight + PADDING * 2;

        if (!attributes.isEmpty()) {
            minHeight += PADDING;
            for (final var attribute : attributes) {
                final var entryWidth = g.getFontMetrics().stringWidth(attribute.label) + PADDING * 2;
                minWidth = Math.max(minWidth, entryWidth);
                minHeight += labelHeight + PADDING;

                attribute.width = entryWidth;
                attribute.height = labelHeight;
            }
            minHeight += PADDING;
        }

        width = Math.max(width, minWidth);
        height = Math.max(height, minHeight);

        g.setClip(x, y, width, height);
        g.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        if (mode == Mode.NONE) {
            g.setColor(colorTable);
            g.fillRect(x, y, width, height);

            int ly = y;

            ly += PADDING;
            g.setColor(colorLabel);
            g.drawString(label, x + (width - labelWidth) / 2, ly + labelHeight);
            ly += labelHeight;
            ly += PADDING;

            g.setColor(colorBorder);
            g.drawLine(left(), ly, right(), ly);

            ly += PADDING;
            for (final var attribute : attributes) {
                if (attribute.hovered) {
                    g.setColor(colorHover);
                    g.fillRect(x, ly, width, labelHeight + PADDING);
                }
                g.setColor(colorLabel);
                g.drawString(attribute.label, x + PADDING, ly + labelHeight);
                ly += labelHeight + PADDING;
            }
        }

        g.setColor(colorBorder);
        g.drawRect(x, y, width, height);
    }

    public boolean containsX(final int x) {
        return left() <= x && x <= right();
    }

    public boolean containsY(final int y) {
        return top() <= y && y <= bottom();
    }

    public boolean containsMarginX(final int x) {
        return left() - MARGIN / 2 <= x && x <= right() + MARGIN / 2;
    }

    public boolean containsMarginY(final int y) {
        return top() - MARGIN / 2 <= y && y <= bottom() + MARGIN / 2;
    }

    public boolean contains(final int x, final int y) {
        return containsX(x) && containsY(y);
    }

    public boolean containsMargin(final int x, final int y) {
        return containsMarginX(x) && containsMarginY(y);
    }

    public boolean containsTop(final int x, final int y) {
        return containsX(x) && Math.abs(top() - y) <= MARGIN;
    }

    public boolean containsLeft(final int x, final int y) {
        return containsY(y) && Math.abs(left() - x) <= MARGIN;
    }

    public boolean containsBottom(final int x, final int y) {
        return containsX(x) && Math.abs(bottom() - y) <= MARGIN;
    }

    public boolean containsRight(final int x, final int y) {
        return containsY(y) && Math.abs(right() - x) <= MARGIN;
    }

    public boolean containsTopLeft(final int x, final int y) {
        return Math.abs(left() - x) <= MARGIN && Math.abs(top() - y) <= MARGIN;
    }

    public boolean containsBottomLeft(final int x, final int y) {
        return Math.abs(left() - x) <= MARGIN && Math.abs(bottom() - y) <= MARGIN;
    }

    public boolean containsBottomRight(final int x, final int y) {
        return Math.abs(right() - x) <= MARGIN && Math.abs(bottom() - y) <= MARGIN;
    }

    public boolean containsTopRight(final int x, final int y) {
        return Math.abs(right() - x) <= MARGIN && Math.abs(top() - y) <= MARGIN;
    }

    public void onClick(final @NotNull JComponent component, final int cx, final int cy) {
    }

    public void onPress(final @NotNull JComponent component, final int cx, final int cy) {
        switch (region) {
            case NONE -> mode = Mode.NONE;
            case CENTER -> {
                mode = Mode.MOVE;
                dx = x - cx;
                dy = y - cy;
            }
            case TOP -> {
                mode = Mode.RESIZE;
                dy = bottom();
            }
            case LEFT -> {
                mode = Mode.RESIZE;
                dx = right();
            }
            case BOTTOM -> {
                mode = Mode.RESIZE;
                dy = top();
            }
            case RIGHT -> {
                mode = Mode.RESIZE;
                dx = left();
            }
            case TOP_LEFT -> {
                mode = Mode.RESIZE;
                dx = right();
                dy = bottom();
            }
            case BOTTOM_LEFT -> {
                mode = Mode.RESIZE;
                dx = right();
                dy = top();
            }
            case BOTTOM_RIGHT -> {
                mode = Mode.RESIZE;
                dx = left();
                dy = top();
            }
            case TOP_RIGHT -> {
                mode = Mode.RESIZE;
                dx = left();
                dy = bottom();
            }
        }
    }

    public void onRelease(final @NotNull JComponent component) {
        if (mode == Mode.NONE)
            return;

        mode = Mode.NONE;
        component.repaint(this.x, this.y, this.width, this.height);
    }

    public boolean onMove(final boolean covered, final int cx, final int cy) {
        if (covered) {
            region = Region.NONE;

            var repaint = false;

            for (final var attribute : attributes) {
                if (attribute.hovered) {
                    attribute.hovered = false;
                    repaint = true;
                }
            }

            return repaint;
        }

        if (containsTopLeft(cx, cy)) {
            region = Region.TOP_LEFT;
        } else if (containsBottomLeft(cx, cy)) {
            region = Region.BOTTOM_LEFT;
        } else if (containsBottomRight(cx, cy)) {
            region = Region.BOTTOM_RIGHT;
        } else if (containsTopRight(cx, cy)) {
            region = Region.TOP_RIGHT;
        } else if (containsTop(cx, cy)) {
            region = Region.TOP;
        } else if (containsLeft(cx, cy)) {
            region = Region.LEFT;
        } else if (containsBottom(cx, cy)) {
            region = Region.BOTTOM;
        } else if (containsRight(cx, cy)) {
            region = Region.RIGHT;
        } else if (contains(cx, cy)) {
            region = Region.CENTER;
        } else {
            region = Region.NONE;
        }

        var repaint = false;

        int ly = y + PADDING + labelHeight + PADDING + PADDING;
        for (final var attribute : attributes) {
            final var lt      = ly;
            final var ll      = x;
            final var lb      = (ly + labelHeight + PADDING);
            final var lr      = (x + width);
            final var hovered = ll <= cx && cx <= lr && lt <= cy && cy <= lb;
            if (hovered != attribute.hovered) {
                attribute.hovered = hovered;
                repaint = true;
            }
            ly += labelHeight + PADDING;
        }

        return repaint;
    }

    public void onDrag(final @NotNull JComponent component, final int cx, final int cy) {
        switch (mode) {
            case MOVE -> {
                final var px = this.x;
                final var py = this.y;

                this.x = this.dx + cx;
                this.y = this.dy + cy;

                component.repaint(px, py, this.width, this.height);
                component.repaint(this.x, this.y, this.width, this.height);
            }
            case RESIZE -> {
                final var px = this.x;
                final var py = this.y;
                final var pw = this.width;
                final var ph = this.height;

                if (region == Region.TOP_LEFT || region == Region.TOP_RIGHT || region == Region.TOP) {
                    this.height = Math.max(this.dy - cy, minHeight);
                    this.y = this.dy - this.height;
                }
                if (region == Region.TOP_LEFT || region == Region.BOTTOM_LEFT || region == Region.LEFT) {
                    this.width = Math.max(this.dx - cx, minWidth);
                    this.x = this.dx - this.width;
                }
                if (region == Region.BOTTOM_LEFT || region == Region.BOTTOM_RIGHT || region == Region.BOTTOM) {
                    this.height = Math.max(cy - this.dy, minHeight);
                }
                if (region == Region.TOP_RIGHT || region == Region.BOTTOM_RIGHT || region == Region.RIGHT) {
                    this.width = Math.max(cx - this.dx, minWidth);
                }

                component.repaint(px, py, pw, ph);
                component.repaint(this.x, this.y, this.width, this.height);
            }
        }
    }
}
