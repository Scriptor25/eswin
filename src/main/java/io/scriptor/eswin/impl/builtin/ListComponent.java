package io.scriptor.eswin.impl.builtin;

import io.scriptor.eswin.component.*;
import io.scriptor.eswin.component.action.ActionComponentBase;
import io.scriptor.eswin.component.action.ActionEvent;
import io.scriptor.eswin.component.action.ActionListener;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;
import java.util.Optional;

@Component("list")
public class ListComponent<T> extends ActionComponentBase<ListComponent<T>, ListComponent.Payload> {

    public record Payload(int begin, int end, boolean adjusting) {
    }

    private final JList<T> root;

    public ListComponent(final @NotNull ComponentInfo info) {
        super(info);

        apply(root = new JList<>());

        if (getAttributes().has("model")) {
            final var model = switch (getAttributes().get("model")) {
                case "single" -> ListSelectionModel.SINGLE_SELECTION;
                case "single-interval" -> ListSelectionModel.SINGLE_INTERVAL_SELECTION;
                case "multiple-interval" -> ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
                default -> throw new IllegalStateException();
            };
            root.setSelectionMode(model);
        }

        if (getAttributes().has("layout")) {
            final var orientation = switch (getAttributes().get("layout")) {
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
