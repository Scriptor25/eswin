package io.scriptor.eswin;

import com.formdev.flatlaf.FlatDarculaLaf;
import io.scriptor.eswin.component.Component;
import io.scriptor.eswin.component.ComponentBase;
import io.scriptor.eswin.registry.ComponentData;
import io.scriptor.eswin.registry.Registry;
import io.scriptor.eswin.util.ClassScanner;
import io.scriptor.eswin.util.Log;
import io.scriptor.eswin.xml.XmlDocument;
import io.scriptor.eswin.xml.XmlGrammar;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

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
            Log.warn(e.toString());
            return;
        }

        final ClassScanner scanner;
        try {
            scanner = new ClassScanner("io.scriptor.eswin");
        } catch (final URISyntaxException | IOException | ClassNotFoundException e) {
            Log.warn(e.toString());
            return;
        }

        final var registry = new Registry();
        final var grammar  = new XmlGrammar();

        scanner.withAnnotation(Component.class)
               .map(cls -> (Class<? extends ComponentBase>) cls)
               .forEach(cls -> {
                   final var component = cls.getAnnotation(Component.class);

                   final XmlDocument layout;
                   if (component.layout().isEmpty()) {
                       layout = null;
                   } else {
                       try (final var stream = ClassLoader.getSystemResourceAsStream(component.layout())) {
                           if (stream == null)
                               throw new FileNotFoundException();

                           layout = grammar.parse(component.layout(), stream);

                       } catch (final IOException e) {
                           Log.warn(e.toString());
                           return;
                       }
                   }

                   registry.put(component.value(), new ComponentData(cls, layout));
               });

        final var app = registry.instantiate(args.length == 1 ? args[0] : "app");
        app.render(frame, false);

        frame.pack();
        frame.setVisible(true);
    }

    public static void example() {
        Log.info("Lorem ipsum");
    }
}
