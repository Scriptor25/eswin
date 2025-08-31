package io.scriptor.eswin.component;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

@Component("panel")
public class PanelComponent extends ComponentBase {

    private final JPanel root;

    public PanelComponent(
            final @Nullable ComponentBase container,
            final @NotNull AttributeSet attributes,
            final @NotNull String text
    ) {
        super(container, attributes, text);

        root = new JPanel();

        if (Constants.DEBUG)
            root.setBackground(new Color((float) Math.random(), (float) Math.random(), (float) Math.random()));

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

                    root.setLayout(layout);
                }
                case "box" -> {
                    final int axis;
                    if (attributes.has("axis"))
                        axis = Constants.getBoxLayout(attributes.get("axis"));
                    else
                        axis = BoxLayout.Y_AXIS;

                    final var layout = new BoxLayout(root, axis);

                    root.setLayout(layout);
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

                    root.setLayout(layout);
                }
                default -> throw new IllegalStateException();
            }
        }
    }

    @Override
    public @NotNull JPanel getJRoot() {
        return root;
    }

    @Override
    public void put(final @NotNull String id, final @NotNull ComponentBase component) {
        super.put(id, component);

        root.add(component.getJRoot());
    }
}
