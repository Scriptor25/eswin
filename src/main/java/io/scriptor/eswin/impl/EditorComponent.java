package io.scriptor.eswin.impl;

import io.scriptor.eswin.component.Component;
import io.scriptor.eswin.component.ComponentBase;
import io.scriptor.eswin.component.ComponentInfo;
import io.scriptor.eswin.impl.view.TableView;
import io.scriptor.eswin.util.Log;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component(value = "editor")
public class EditorComponent extends ComponentBase {

    private final JComponent root;

    private final SourceContext ctxSource;

    private final List<TableView> tables = new ArrayList<>();
    private TableView hovered;
    private TableView selected;

    public EditorComponent(final @NotNull ComponentInfo info) {
        super(info);

        ctxSource = getProvider().use(SourceContext.class);

        apply(root = new JComponent() {

            @Override
            public void paint(final @NotNull Graphics g) {
                final var g2 = (Graphics2D) g;

                final var label = "Table Editor";
                final var sw    = g2.getFontMetrics().stringWidth(label);
                final var sh    = g2.getFontMetrics().getHeight();
                g2.drawString(label, (getWidth() - sw) / 2, (getHeight() + sh) / 2);

                tables.forEach(view -> view.paint(g2, hovered, selected));
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
                selected = hovered;

                if (selected != null) {
                    selected.onDragBegin(event.getX(), event.getY());

                    tables.remove(selected);
                    tables.add(selected);

                    root.repaint(selected.getBounds());
                }
            }

            @Override
            public void mouseReleased(final @NotNull MouseEvent event) {

                final var view = selected;
                selected = null;

                if (view != null) {
                    view.onDragEnd(event.getX(), event.getY());

                    root.repaint(view.getBounds());
                }
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
                if (selected != null) {
                    final var bounds = selected.getBounds();
                    selected.onDrag(event.getX(), event.getY());

                    Rectangle.union(bounds, selected.getBounds(), bounds);
                    root.repaint(bounds);
                }
            }

            @Override
            public void mouseMoved(final @NotNull MouseEvent event) {
                final var ex = event.getX();
                final var ey = event.getY();

                final var previous = hovered;
                hovered = null;

                tables.forEach(view -> {
                    if (view.getBounds().contains(ex, ey)) {
                        hovered = view;
                    }
                });

                if (previous != null && previous != hovered) {
                    root.repaint(previous.getBounds());
                }

                if (hovered != null) {
                    root.repaint(hovered.getBounds());
                }
            }
        });
    }

    @Override
    public boolean hasJRoot() {
        return true;
    }

    @Override
    public @NotNull JComponent getJRoot() {
        return root;
    }

    @Override
    protected void onAttached() {
        super.onAttached();

        tables.clear();
        ctxSource.getTables()
                 .map(TableView::new)
                 .forEach(view -> {
                     tables.add(view);

                     try {
                         view.get(ctxSource.getConnection());
                     } catch (final SQLException e) {
                         Log.warn("while get table view '%s': %s", view, e);
                     }
                 });
    }

    @Override
    protected void onDetached() {
        super.onDetached();

        tables.forEach(view -> {
            try {
                view.put(ctxSource.getConnection());
            } catch (final SQLException e) {
                Log.warn("while put table view '%s': %s", view, e);
            }
        });
        tables.clear();
    }
}
