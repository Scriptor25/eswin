package io.scriptor.eswin.impl.builtin;

import io.scriptor.eswin.component.Component;
import io.scriptor.eswin.component.ComponentBase;
import io.scriptor.eswin.component.ComponentInfo;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.function.Consumer;

@Component("embed")
public class EmbedComponent extends ComponentBase {

    private final JFXPanel root;

    public EmbedComponent(final @NotNull ComponentInfo info) {
        super(info);

        apply(root = new JFXPanel());

        final Consumer<WebEngine> consumer;
        if (getAttributes().has("src")) {
            final var src = getAttributes().get("src");

            consumer = engine -> engine.load(src);
        } else {
            final var type = getAttributes().get("type", "text/html");

            consumer = engine -> engine.loadContent(info.getText(), type);
        }

        Platform.runLater(() -> {
            final var view   = new WebView();
            final var engine = view.getEngine();

            consumer.accept(engine);

            final var scene = new Scene(view);
            root.setScene(scene);
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
}
