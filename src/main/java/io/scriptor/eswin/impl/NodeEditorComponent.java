package io.scriptor.eswin.impl;

import io.scriptor.eswin.component.AttributeSet;
import io.scriptor.eswin.component.Component;
import io.scriptor.eswin.component.ComponentBase;
import io.scriptor.eswin.component.ContextProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component(value = "node-editor")
public class NodeEditorComponent extends ComponentBase {

    @FunctionalInterface
    public interface NodeConsumer {

        void run(
                final @NotNull Node node,
                final @NotNull JComponent component,
                final int cx,
                final int cy
        );
    }

    private final JComponent root;
    private final List<Node> nodes = new ArrayList<>();

    private final SourceContext ctxSource;

    public NodeEditorComponent(
            final @NotNull ContextProvider provider,
            final @Nullable ComponentBase parent,
            final @NotNull AttributeSet attributes,
            final @NotNull String text
    ) {
        super(provider, parent, attributes, text);

        ctxSource = provider.use(SourceContext.class);

        apply(root = new JComponent() {

            @Override
            public void paint(final @NotNull Graphics g) {
                final var g2 = (Graphics2D) g;

                final var label = "Table Editor";
                final var sw    = g2.getFontMetrics().stringWidth(label);
                final var sh    = g2.getFontMetrics().getHeight();
                g2.drawString(label, (getWidth() - sw) / 2, (getHeight() + sh) / 2);

                for (final var node : nodes)
                    node.paint(g2);
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(800, 600);
            }
        });

        root.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(final @NotNull MouseEvent event) {
                interact(event.getX(), event.getY(), Node::onClick);
            }

            @Override
            public void mousePressed(final @NotNull MouseEvent event) {
                interact(event.getX(), event.getY(), Node::onPress);
            }

            @Override
            public void mouseReleased(final @NotNull MouseEvent event) {
                active().ifPresent(node -> node.onRelease(root));
            }

            @Override
            public void mouseEntered(final @NotNull MouseEvent mouseEvent) {
            }

            @Override
            public void mouseExited(final @NotNull MouseEvent mouseEvent) {
            }
        });

        root.addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged(final @NotNull MouseEvent event) {
                for (final var node : nodes)
                    node.onDrag(root, event.getX(), event.getY());
            }

            @Override
            public void mouseMoved(final @NotNull MouseEvent event) {
                final var cursor = hovered(event.getX(), event.getY())
                        .map(hovered -> {
                            for (final var table : nodes)
                                if (table.onMove(table != hovered, event.getX(), event.getY()))
                                    root.repaint(table.x, table.y, table.width, table.height);

                            return hovered.getCursor();
                        })
                        .orElseGet(() -> {
                            for (final var table : nodes)
                                if (table.onMove(true, event.getX(), event.getY()))
                                    root.repaint(table.x, table.y, table.width, table.height);

                            return Cursor.getDefaultCursor();
                        });

                root.setCursor(cursor);
            }
        });
    }

    public @NotNull Optional<Node> hovered(final int x, final int y) {
        for (int i = nodes.size() - 1; i >= 0; --i)
            if (nodes.get(i).containsMargin(x, y))
                return Optional.of(nodes.get(i));
        return Optional.empty();
    }

    public @NotNull Optional<Node> active() {
        for (int i = nodes.size() - 1; i >= 0; --i)
            if (nodes.get(i).active())
                return Optional.of(nodes.get(i));
        return Optional.empty();
    }

    public void interact(final int x, final int y, final @NotNull NodeConsumer consumer) {
        hovered(x, y).ifPresent(node -> {
            consumer.run(node, root, x, y);
            nodes.remove(node);
            nodes.add(node);
        });
    }

    @Override
    public @NotNull JComponent getJRoot() {
        return root;
    }

    @Override
    protected void onAttached() {
        nodes.clear();
        ctxSource.tables().map(table -> {
            final var out = new Node();

            out.label = table.name();
            for (final var column : table.columns()) {
                final var attribute = new Attribute();
                attribute.label = "%s (%s)".formatted(column.name(), column.getType());
                out.attributes.add(attribute);
            }

            return out;
        }).forEach(nodes::add);
    }
}
