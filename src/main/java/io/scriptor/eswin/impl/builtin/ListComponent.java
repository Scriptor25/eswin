package io.scriptor.eswin.impl.builtin;

import io.scriptor.eswin.component.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;
import java.util.Optional;

@Component("list")
public class ListComponent<T> extends ActionComponentBase<ListComponent<T>, ListComponent.Payload> {

    public record Payload(int begin, int end, boolean adjusting) {
    }

    private final JList<T> root;

    public ListComponent(
            final @NotNull ContextProvider provider,
            final @Nullable ComponentBase parent,
            final @NotNull AttributeSet attributes,
            final @NotNull String text
    ) {
        super(provider, parent, attributes, text);

        apply(root = new JList<>());

        if (attributes.has("model")) {
            final var model = switch (attributes.get("model")) {
                case "single" -> ListSelectionModel.SINGLE_SELECTION;
                case "single-interval" -> ListSelectionModel.SINGLE_INTERVAL_SELECTION;
                case "multiple-interval" -> ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
                default -> throw new IllegalStateException();
            };
            root.setSelectionMode(model);
        }

        if (attributes.has("layout")) {
            final var orientation = switch (attributes.get("layout")) {
                case "vertical" -> JList.VERTICAL;
                case "vertical-wrap" -> JList.VERTICAL_WRAP;
                case "horizontal-wrap" -> JList.HORIZONTAL_WRAP;
                default -> throw new IllegalStateException();
            };
            root.setLayoutOrientation(orientation);
        }
    }

    @Override
    public void addListener(final @NotNull ActionListener<ListComponent<T>, Payload> listener) {
        root.addListSelectionListener(event -> {
            final var payload = new Payload(event.getFirstIndex(), event.getLastIndex(), event.getValueIsAdjusting());
            listener.callback(new ActionEvent<>(this, payload));
        });
    }

    @Override
    public @NotNull JComponent getJRoot() {
        return root;
    }

    public void setListData(final @NotNull T[] data) {
        root.setListData(data);
    }

    public int getSelectedIndex() {
        return root.getSelectedIndex();
    }

    public int @NotNull [] getSelectedIndices() {
        return root.getSelectedIndices();
    }

    public @NotNull Optional<T> getSelectedValue() {
        return Optional.ofNullable(root.getSelectedValue());
    }

    public @NotNull List<T> getSelectedValues() {
        return root.getSelectedValuesList();
    }
}
