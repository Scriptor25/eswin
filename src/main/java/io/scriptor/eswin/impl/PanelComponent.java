package io.scriptor.eswin.impl;

import io.scriptor.eswin.component.AttributeSet;
import io.scriptor.eswin.component.Component;
import io.scriptor.eswin.component.ComponentBase;
import io.scriptor.eswin.component.Constants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

@Component("panel")
public class PanelComponent extends ComponentBase {

    private final JPanel panel;

    public PanelComponent(
            final @Nullable ComponentBase container,
            final @NotNull AttributeSet attributes,
            final @NotNull String text
    ) {
        super(container, attributes, text);

        apply(panel = new JPanel());

        if (Constants.DEBUG)
            panel.setBackground(new Color((float) Math.random(), (float) Math.random(), (float) Math.random()));

        if (attributes.has("layout")) {
            switch (attributes.get("layout")) {
                case "flow" -> {
                    final var layout = new FlowLayout();

                    if (attributes.has("align"))
                        layout.setAlignment(Constants.getSwing(attributes.get("align")));
                    if (attributes.has("hgap"))
                        layout.setHgap(attributes.getInt("hgap"));
                    if (attributes.has("vgap"))
                        layout.setVgap(attributes.getInt("vgap"));

                    layout.setAlignOnBaseline(attributes.has("baseline"));

                    panel.setLayout(layout);
                }
                case "box" -> {
                    final int axis;
                    if (attributes.has("axis"))
                        axis = Constants.getBoxLayout(attributes.get("axis"));
                    else
                        axis = BoxLayout.PAGE_AXIS;

                    final var layout = new BoxLayout(panel, axis);

                    panel.setLayout(layout);
                }
                case "grid" -> {
                    final var layout = new GridLayout(1, 1, 0, 0);

                    if (attributes.has("rows"))
                        layout.setRows(attributes.getInt("rows"));
                    if (attributes.has("cols"))
                        layout.setColumns(attributes.getInt("cols"));
                    if (attributes.has("hgap"))
                        layout.setHgap(attributes.getInt("hgap"));
                    if (attributes.has("vgap"))
                        layout.setVgap(attributes.getInt("vgap"));

                    panel.setLayout(layout);
                }
                default -> throw new IllegalStateException();
            }
        }
    }

    @Override
    public boolean hasJRoot() {
        return true;
    }

    @Override
    public @NotNull JPanel getJRoot() {
        return panel;
    }

    @Override
    public void putChild(final @NotNull String id, final @NotNull ComponentBase child) {
        super.putChild(id, child);

        if (child.hasJRoot())
            panel.add(child.getJRoot());
    }
}
