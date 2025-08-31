package io.scriptor.eswin;

import io.scriptor.eswin.xml.Parser;
import io.scriptor.eswin.xml.document.Document;
import io.scriptor.eswin.xml.document.Element;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

public class Main {

    @MagicConstant(valuesFromClass = SwingConstants.class)
    public static int getAlign(
            final @NotNull Element element,
            final @NotNull String name,
            final @MagicConstant(valuesFromClass = SwingConstants.class) int defaultValue
    ) {
        if (!element.hasAttribute(name)) {
            return defaultValue;
        }

        return switch (element.getAttribute(name)) {
            case "center" -> SwingConstants.CENTER;
            case "top" -> SwingConstants.TOP;
            case "left" -> SwingConstants.LEFT;
            case "bottom" -> SwingConstants.BOTTOM;
            case "right" -> SwingConstants.RIGHT;
            case "leading" -> SwingConstants.LEADING;
            case "trailing" -> SwingConstants.TRAILING;
            default -> throw new IllegalStateException();
        };
    }

    @MagicConstant(valuesFromClass = BoxLayout.class)
    public static int getAxis(final @NotNull Element element) {
        if (!element.hasAttribute("axis")) {
            return BoxLayout.Y_AXIS;
        }

        return switch (element.getAttribute("axis")) {
            case "x" -> BoxLayout.X_AXIS;
            case "y" -> BoxLayout.Y_AXIS;
            case "line" -> BoxLayout.LINE_AXIS;
            case "page" -> BoxLayout.PAGE_AXIS;
            default -> throw new IllegalStateException();
        };
    }

    public static @NotNull Optional<LayoutManager> getLayout(
            final @NotNull Element element,
            final @NotNull Container container
    ) {
        if (!element.hasAttribute("layout")) {
            return Optional.empty();
        }

        return Optional.of(switch (element.getAttribute("layout")) {
            case "flow" -> {
                final var align = getAlign(element, "align", SwingConstants.LEFT);

                final var hgap = element.getIntAttribute("hgap", 0);
                final var vgap = element.getIntAttribute("vgap", 0);

                final var baseline = element.hasAttribute("baseline");

                final var layout = new FlowLayout(align, hgap, vgap);
                layout.setAlignOnBaseline(baseline);

                yield layout;
            }
            case "box" -> {
                final var axis = getAxis(element);

                yield new BoxLayout(container, axis);
            }
            case "grid" -> {
                final var rows = element.getIntAttribute("rows", 0);
                final var cols = element.getIntAttribute("cols", 0);

                final var hgap = element.getIntAttribute("hgap", 0);
                final var vgap = element.getIntAttribute("vgap", 0);

                yield new GridLayout(rows, cols, hgap, vgap);
            }
            default -> throw new IllegalStateException();
        });
    }

    public static ActionListener getAction(
            final @NotNull Context context,
            final @NotNull Element element,
            final @NotNull JComponent component
    ) {
        if (!element.hasAttribute("click"))
            return _ -> {
            };

        final var path         = element.getAttribute("click");
        final var end          = path.lastIndexOf('.');
        final var className    = path.substring(0, end);
        final var functionName = path.substring(end + 1);

        try {
            final var loadedClass = ClassLoader.getSystemClassLoader().loadClass(className);
            final var loadedFunction = loadedClass.getMethod(functionName,
                                                             Context.class,
                                                             JComponent.class,
                                                             ActionEvent.class);
            return event -> {
                try {
                    loadedFunction.invoke(null, context, component, event);
                } catch (final IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            };
        } catch (final ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public static @NotNull JComponent toComponent(final @NotNull Context context, final @NotNull Element element) {
        final JComponent component = switch (element.name()) {
            case "panel" -> {
                final var panel = new JPanel();

                getLayout(element, panel).ifPresent(panel::setLayout);

                element.elements()
                       .map(e -> toComponent(context, e))
                       .forEach(panel::add);

                yield panel;
            }
            case "label" -> {
                final var halign = getAlign(element, "halign", SwingConstants.LEFT);
                final var valign = getAlign(element, "valign", SwingConstants.CENTER);

                final var label = new JLabel(element.text());
                label.setHorizontalAlignment(halign);
                label.setVerticalAlignment(valign);

                yield label;
            }
            case "button" -> {
                final var button = new JButton(element.text());

                final var action = getAction(context, element, button);
                button.addActionListener(action);

                yield button;
            }
            default -> throw new IllegalStateException();
        };

        if (element.hasAttribute("id"))
            context.put(element.getAttribute("id"), component);

        if (element.hasAttribute("title"))
            component.setToolTipText(element.getAttribute("title"));

        return component;
    }

    public static void main(final @NotNull String @NotNull [] args) {
        final var environment   = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final var device        = environment.getDefaultScreenDevice();
        final var configuration = device.getDefaultConfiguration();
        final var frame         = new JFrame("ESWIN", configuration);

        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        final Document document;
        try (final var stream = ClassLoader.getSystemResourceAsStream("layout/main.xml")) {
            if (stream == null)
                throw new FileNotFoundException();

            final var parser = new Parser(stream);
            document = parser.parse();

        } catch (final IOException e) {
            e.printStackTrace(System.err);
            return;
        }

        final var context = new Context();
        final var root    = document.root();

        frame.add(toComponent(context, root));

        frame.setVisible(true);
    }

    public static void example(
            final @NotNull Context context,
            final @NotNull JComponent component,
            final @NotNull ActionEvent event
    ) {
        final var button = context.get("press-me", JButton.class);
        final var text   = button.getText();

        if (!button.isEnabled())
            return;

        button.setEnabled(false);
        button.setText("Hello World! You pressed me!");

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                button.setText(text);
                button.setEnabled(true);
            }
        }, 1000L);
    }

    public static void first(
            final @NotNull Context context,
            final @NotNull JComponent component,
            final @NotNull ActionEvent event
    ) {
        final var label = context.get("status", JLabel.class);
        label.setText("pressed first button");
    }

    public static void second(
            final @NotNull Context context,
            final @NotNull JComponent component,
            final @NotNull ActionEvent event
    ) {
        final var label = context.get("status", JLabel.class);
        label.setText("pressed second button");
    }

    public static void third(
            final @NotNull Context context,
            final @NotNull JComponent component,
            final @NotNull ActionEvent event
    ) {
        final var label = context.get("status", JLabel.class);
        label.setText("pressed third button");
    }
}
