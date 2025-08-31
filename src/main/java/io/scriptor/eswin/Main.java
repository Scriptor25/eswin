package io.scriptor.eswin;

import io.scriptor.eswin.component.*;
import io.scriptor.eswin.component.Component;
import io.scriptor.eswin.context.ComponentData;
import io.scriptor.eswin.context.Context;
import io.scriptor.eswin.xml.Parser;
import io.scriptor.eswin.xml.document.Document;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(final @NotNull String @NotNull [] args) {
        final var environment   = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final var device        = environment.getDefaultScreenDevice();
        final var configuration = device.getDefaultConfiguration();
        final var frame         = new JFrame("ESWIN", configuration);

        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        try (final var stream = ClassLoader.getSystemResourceAsStream("app.png")) {
            if (stream == null)
                throw new FileNotFoundException();

            final var image = ImageIO.read(stream);
            frame.setIconImage(image);

        } catch (final IOException e) {
            e.printStackTrace(System.err);
            return;
        }

        final var context = new Context();

        // TODO: load all classes, filter classes for @Component annotation, load layout for components, registers classes in context
        final var classes = List.of(PanelComponent.class,
                                    ButtonComponent.class,
                                    LabelComponent.class,
                                    HelloWorldComponent.class,
                                    AppComponent.class);

        for (final var type : classes) {
            final var component = type.getAnnotation(Component.class);

            final Document layout;
            if (component.layout().isEmpty()) {
                layout = null;
            } else {
                try (final var stream = ClassLoader.getSystemResourceAsStream(component.layout())) {
                    if (stream == null)
                        throw new FileNotFoundException();

                    final var parser = new Parser(stream);
                    layout = parser.parse();

                } catch (final IOException e) {
                    e.printStackTrace(System.err);
                    return;
                }
            }

            context.put(component.value(), new ComponentData(type, layout));
        }

        final var app = context.instantiate("app");
        frame.add(app.getJRoot());

        frame.setVisible(true);
    }
}
