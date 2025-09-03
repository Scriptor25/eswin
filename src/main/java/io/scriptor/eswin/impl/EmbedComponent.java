package io.scriptor.eswin.impl;

import io.scriptor.eswin.component.AttributeSet;
import io.scriptor.eswin.component.Component;
import io.scriptor.eswin.component.ComponentBase;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Component("embed")
public class EmbedComponent extends ComponentBase {

    private final JFXPanel panel;

    public EmbedComponent(
            final @Nullable ComponentBase container,
            final @NotNull AttributeSet attributes,
            final @NotNull String text
    ) {
        super(container, attributes, text);

        apply(panel = new JFXPanel());

        final Consumer<WebEngine> consumer;
        if (attributes.has("src")) {
            final var src = attributes.get("src");

            consumer = engine -> engine.load(src);
        } else {
            final var type = attributes.get("type", "text/html");

            consumer = engine -> engine.loadContent(text, type);
        }

        Platform.runLater(() -> {
            final var view   = new WebView();
            final var engine = view.getEngine();

            consumer.accept(engine);

            final var scene = new Scene(view);
            panel.setScene(scene);
        });
    }

    @Override
    public @NotNull Stream<JComponent> getJRoot() {
        return Stream.of(panel);
    }
}
