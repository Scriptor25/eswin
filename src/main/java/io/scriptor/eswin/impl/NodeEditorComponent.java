package io.scriptor.eswin.impl;

import io.scriptor.eswin.component.AttributeSet;
import io.scriptor.eswin.component.Component;
import io.scriptor.eswin.component.ComponentBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

@Component(value = "node-editor")
public class NodeEditorComponent extends ComponentBase {

    public static class Node {

        public @NotNull Color colorBackground = new Color(0xB3B3B3);
        public @NotNull Color colorForeground = new Color(0x2B2B2B);
        public @NotNull String label = "Node";

        public int x = 0;
        public int y = 0;
        public int width = 100;
        public int height = 200;

        public boolean drag = false;
        public int dx = 0;
        public int dy = 0;

        public void paint(final @NotNull Graphics g) {
            g.setColor(colorBackground);
            g.fillRect(x, y, width, height);

            final var width  = g.getFontMetrics().stringWidth(label);
            final var height = g.getFontMetrics().getHeight();

            g.setColor(colorForeground);
            g.drawString(label, x, y + height);
        }

        public boolean contains(final int x, final int y) {
            return this.x <= x && x <= (this.x + this.width) && this.y <= y && y <= (this.y + this.height);
        }
    }

    private final JComponent root;
    private final Node node;

    public NodeEditorComponent(
            final @Nullable ComponentBase parent,
            final @NotNull AttributeSet attributes,
            final @NotNull String text
    ) {
        super(parent, attributes, text);

        node = new Node();

        apply(root = new JComponent() {

            @Override
            public void paint(final @NotNull Graphics g) {
                final var string = "Node Editor";
                final var width  = g.getFontMetrics().stringWidth(string);
                final var height = g.getFontMetrics().getHeight();
                g.drawString(string,
                             (getWidth() - width) / 2,
                             (getHeight() + height) / 2);

                node.paint(g);
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(800, 600);
            }
        });

        root.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(final @NotNull MouseEvent event) {
            }

            @Override
            public void mousePressed(final @NotNull MouseEvent event) {
                final var ex = event.getX();
                final var ey = event.getY();

                if (node.contains(ex, ey)) {
                    node.drag = true;
                    node.dx = node.x - ex;
                    node.dy = node.y - ey;
                }

                update(ex, ey);
            }

            @Override
            public void mouseReleased(final @NotNull MouseEvent event) {
                node.drag = false;
                update(event.getX(), event.getY());
            }

            @Override
            public void mouseEntered(final @NotNull MouseEvent event) {
            }

            @Override
            public void mouseExited(final @NotNull MouseEvent event) {
                node.drag = false;
                update(event.getX(), event.getY());
            }
        });

        root.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(final @NotNull MouseEvent event) {
                final var ex = event.getX();
                final var ey = event.getY();

                if (node.drag) {
                    root.repaint(node.x, node.y, node.width, node.height);

                    node.x = node.dx + ex;
                    node.y = node.dy + ey;

                    root.repaint(node.x, node.y, node.width, node.height);
                }

                update(ex, ey);
            }

            @Override
            public void mouseMoved(final @NotNull MouseEvent event) {
                update(event.getX(), event.getY());
            }
        });
    }

    private void update(final int x, final int y) {
        if (node.drag) {
            root.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else if (node.contains(x, y)) {
            root.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        } else {
            root.setCursor(Cursor.getDefaultCursor());
        }
    }

    @Override
    public @NotNull JComponent getJRoot() {
        return root;
    }
}
