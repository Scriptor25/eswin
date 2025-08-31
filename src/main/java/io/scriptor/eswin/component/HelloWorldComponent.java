package io.scriptor.eswin.component;

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
    private String status;

    public HelloWorldComponent(
            final @Nullable ComponentBase container,
            final @NotNull AttributeSet attributes,
            final @NotNull String text
    ) {
        super(container, attributes, text);

        this.message = attributes.get("message");
        notify("message", this.message);
    }

    public @NotNull String message() {
        return message;
    }

    public @Nullable String status() {
        return status;
    }

    public void status(final @NotNull String status) {
        this.status = status;
        notify("status", this.status);
    }

    public void example(final @NotNull ActionEvent event) {
        final var button = get("press-me", ButtonComponent.class);
        final var text   = button.getText();

        if (!button.isEnabled())
            return;

        button.setEnabled(false);
        button.setText("You pressed me!");

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                button.setText(text);
                button.setEnabled(true);
            }
        }, 1000L);
    }

    public void first(final @NotNull ActionEvent event) {
        status("pressed first button");
    }

    public void second(final @NotNull ActionEvent event) {
        status("pressed second button");
    }

    public void third(final @NotNull ActionEvent event) {
        status("pressed third button");
    }
}
