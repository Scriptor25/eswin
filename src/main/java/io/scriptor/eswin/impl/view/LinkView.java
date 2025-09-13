package io.scriptor.eswin.impl.view;

import io.scriptor.eswin.impl.model.ForeignKey;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.geom.Area;
import java.util.List;

public final class LinkView {

    private final ForeignKey key;
    private final Rectangle bounds = new Rectangle();

    public LinkView(final @NotNull ForeignKey key) {
        this.key = key;
    }

    public boolean uses(final @NotNull TableView view) {
        return view.getTable() == key.getPKTable() || view.getTable() == key.getFKTable();
    }

    public @NotNull Rectangle getBounds() {
        return bounds;
    }

    private static Shape makeClip(final @NotNull Rectangle union, final @NotNull Rectangle... rects) {
        final var clip = new Area(union);
        for (final var rect : rects) {
            final var intersection = union.intersection(rect);
            if (!intersection.isEmpty()) {
                clip.subtract(new Area(intersection));
            }
        }
        return clip;
    }

    public void paint(final @NotNull Graphics2D g2, final @NotNull List<TableView> tables) {
        final var source = tables.stream()
                                 .filter(view -> view.getTable() == key.getPKTable())
                                 .findAny()
                                 .orElseThrow();

        final var target = tables.stream()
                                 .filter(view -> view.getTable() == key.getFKTable())
                                 .findAny()
                                 .orElseThrow();

        final var sourceBounds = source.getBounds();
        final var targetBounds = target.getBounds();

        final var scx = (int) sourceBounds.getCenterX();
        final var scy = (int) sourceBounds.getCenterY();
        final var tcx = (int) targetBounds.getCenterX();
        final var tcy = (int) targetBounds.getCenterY();

        final var minX = Math.min(scx, tcx);
        final var maxX = Math.max(scx, tcx);
        final var minY = Math.min(scy, tcy);
        final var maxY = Math.max(scy, tcy);

        final var union = new Rectangle(minX, minY, maxX - minX, maxY - minY);
        bounds.setRect(union);

        final var clip = makeClip(union, sourceBounds, targetBounds);
        g2.setClip(clip);

        g2.setColor(Color.RED);
        g2.drawLine(scx, scy, tcx, tcy);
    }
}
