package io.scriptor.eswin.impl.view;

import io.scriptor.eswin.impl.model.Column;
import io.scriptor.eswin.impl.model.Table;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Comparator;

public final class TableView {

    private static final Color COLOR_NORMAL = new Color(0x474955);
    private static final Color COLOR_HOVER = new Color(0x696A78);
    private static final Color COLOR_SELECT = new Color(0x6D739F);
    private static final Color COLOR_TEXT = new Color(0xffffff);
    private static final Color COLOR_BORDER = new Color(0x212121);

    private static final int PADDING = 10;

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
        final var rowHeight  = fontHeight + PADDING;
        final var columns = table.getColumns()
                                 .sorted(Comparator.comparingInt(Column::getOrdinalPosition))
                                 .toList();

        {
            final var label      = table.getName();
            final var labelWidth = g2.getFontMetrics().stringWidth(label);

            if (width < labelWidth)
                width = labelWidth;

            height += rowHeight;
        }

        columns.forEach(column -> {
            final var label      = column.getName();
            final var labelWidth = g2.getFontMetrics().stringWidth(label);

            if (width < labelWidth)
                width = labelWidth;

            height += rowHeight;
        });

        width += 2 * PADDING;
        height += 2 * PADDING;

        g2.setClip(x, y, width, height);

        g2.setColor(this == selected
                    ? COLOR_SELECT
                    : this == hovered
                      ? COLOR_HOVER
                      : COLOR_NORMAL);
        g2.fillRect(x, y, width, height);

        {
            final var label = table.getName();

            g2.setColor(COLOR_TEXT);
            g2.drawString(label, x + PADDING, y + rowHeight);
        }

        for (int i = 0; i < columns.size(); ++i) {
            final var column = columns.get(i);
            final var label  = column.getName();
            final var offset = (i + 1) * rowHeight;

            g2.setColor(COLOR_TEXT);
            g2.drawString(label, x + PADDING, y + offset + rowHeight);
        }

        g2.setColor(COLOR_BORDER);
        g2.setStroke(new BasicStroke(2, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
        g2.drawRect(x, y, width - 1, height - 1);
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
