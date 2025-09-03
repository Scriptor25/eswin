package io.scriptor.eswin;

import com.formdev.flatlaf.FlatDarculaLaf;
import io.scriptor.eswin.component.Component;
import io.scriptor.eswin.impl.*;
import io.scriptor.eswin.registry.ComponentData;
import io.scriptor.eswin.registry.Registry;
import io.scriptor.eswin.xml.XmlDocument;
import io.scriptor.eswin.xml.XmlGrammar;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(final @NotNull String @NotNull [] args) {

        FlatDarculaLaf.setup();

        final var environment   = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final var device        = environment.getDefaultScreenDevice();
        final var configuration = device.getDefaultConfiguration();
        final var frame         = new JFrame("ESWIN", configuration);

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

        final var context = new Registry();

        // TODO: load all classes, filter classes for @Component annotation, load layout for components, registers classes in context
        final var classes = List.of(PanelComponent.class,
                                    ButtonComponent.class,
                                    LabelComponent.class,
                                    EmbedComponent.class,
                                    FragmentComponent.class,
                                    HelloWorldComponent.class,
                                    AppComponent.class,
                                    SourcePanelComponent.class,
                                    TextFieldComponent.class,
                                    ExampleComponent.class,
                                    DatabasePanelComponent.class,
                                    SystemDatabasePanelComponent.class,
                                    ActionPanelComponent.class,
                                    RadioButtonComponent.class);

        final var grammar = new XmlGrammar();

        for (final var type : classes) {
            final var component = type.getAnnotation(Component.class);

            final XmlDocument layout;
            if (component.layout().isEmpty()) {
                layout = null;
            } else {
                try (final var stream = ClassLoader.getSystemResourceAsStream(component.layout())) {
                    if (stream == null)
                        throw new FileNotFoundException();

                    layout = grammar.parse(component.layout(), stream);

                } catch (final IOException e) {
                    e.printStackTrace(System.err);
                    return;
                }
            }

            context.put(component.value(), new ComponentData(type, layout));
        }

        final var app = context.instantiate("example");
        app.getJRoot().forEach(frame::add);

        frame.pack();
        frame.setVisible(true);
    }

    public static void example() {
        System.out.println("Lorem ipsum");
    }
}
