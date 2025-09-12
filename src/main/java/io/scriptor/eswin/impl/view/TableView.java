package io.scriptor.eswin.impl.view;

import io.scriptor.eswin.impl.model.Table;
import io.scriptor.eswin.util.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;

public final class TableView {

    private final Table table;

    private int x;
    private int y;
    private int width;
    private int height;

    private int dx;
    private int dy;

    public TableView(final @NotNull Table table) {
        this.table = table;
    }

    public @NotNull Table getTable() {
        return table;
    }

    public void get(final @NotNull Connection connection) throws SQLException {
        try (final var s = connection.prepareStatement(
                """
                select * from eswin.tables
                         where schema_name = ? and table_name = ?
                """
        )) {
            s.setString(1, table.getSchema().getName());
            s.setString(2, table.getName());

            try (final var set = s.executeQuery()) {
                if (set.next()) {
                    x = set.getInt("position_x");
                    y = set.getInt("position_y");
                }
            }
        }
    }

    public void put(final @NotNull Connection connection) throws SQLException {
        try (final var s = connection.prepareStatement(
                """
                insert into eswin.tables (schema_name, table_name, position_x, position_y)
                values (?, ?, ?, ?)
                on conflict (schema_name, table_name) do update
                set position_x = excluded.position_x,
                    position_y = excluded.position_y
                """
        )) {
            s.setString(1, table.getSchema().getName());
            s.setString(2, table.getName());

            s.setInt(3, x);
            s.setInt(4, y);

            s.executeUpdate();
        }
    }

    public @NotNull Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public void paint(
            final @NotNull Graphics2D g2,
            final @Nullable TableView hovered,
            final @Nullable TableView selected
    ) {
        width = 0;
        height = 0;

        final var fontHeight = g2.getFontMetrics().getHeight();
        final var columns    = table.getColumns().toList();

        {
            final var label      = table.getName();
            final var labelWidth = g2.getFontMetrics().stringWidth(label);

            if (width < labelWidth)
                width = labelWidth;

            height += fontHeight;
        }

        columns.forEach(column -> {
            final var label      = column.getName();
            final var labelWidth = g2.getFontMetrics().stringWidth(label);

            if (width < labelWidth)
                width = labelWidth;

            height += fontHeight;
        });

        g2.setClip(x, y, width, height);

        g2.setColor(new Color(this == selected
                              ? 0xff0000
                              : this == hovered
                                ? 0xff00ff
                                : 0x0000ff));
        g2.fillRect(x, y, width, height);

        {
            final var label = table.getName();
            g2.setColor(new Color(0xffffff));
            g2.drawString(label, x, y + fontHeight);
        }

        for (int i = 0; i < columns.size(); ++i) {
            final var column = columns.get(i);
            final var label  = column.getName();
            final var offset = (i + 1) * fontHeight;

            g2.setColor(new Color(0xffffff));
            g2.drawString(label, x, y + offset + fontHeight);
        }
    }

    public void onDragBegin(final int x, final int y) {
        dx = x - this.x;
        dy = y - this.y;
    }

    public void onDragEnd(final int x, final int y) {
        this.x = x - dx;
        this.y = y - dy;
    }

    public void onDrag(final int x, final int y) {
        this.x = x - dx;
        this.y = y - dy;
    }
}
