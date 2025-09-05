package io.scriptor.eswin.impl;

import io.scriptor.eswin.component.AttributeSet;
import io.scriptor.eswin.component.Component;
import io.scriptor.eswin.component.ComponentBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.event.ActionEvent;
import java.util.Timer;
import java.util.TimerTask;

@Component(
        value = "hello-world",
        layout = "layout/hello.world.xml"
)
public class HelloWorldComponent extends ComponentBase {

    private final String message;

    private String text = "Press me!";
    private String status = "";

    public HelloWorldComponent(
            final @Nullable ComponentBase parent,
            final @NotNull AttributeSet attributes,
            final @NotNull String text
    ) {
        super(parent, attributes, text);

        this.message = attributes.get("message");

        notify("message", this.message);
        notify("text", this.text);
        notify("status", this.status);
    }

    public @NotNull String message() {
        return message;
    }

    public @NotNull String text() {
        return text;
    }

    public void text(final @NotNull String text) {
        this.text = text;
        notify("text", this.text);
    }

    public @NotNull String status() {
        return status;
    }

    public void status(final @NotNull String status) {
        this.status = status;
        notify("status", this.status);
    }

    public void example(final @NotNull ActionEvent event) {
        final var button = getChild("press-me", ButtonComponent.class);

        if (!button.isEnabled())
            return;

        button.setEnabled(false);
        text("You pressed me!");

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                text("Press me!");
                button.setEnabled(true);
            }
        }, 1000L);
    }
}
