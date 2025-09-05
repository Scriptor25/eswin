package io.scriptor.eswin.util;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;

public class ClassScanner {

    private static void findClasses(
            final @NotNull ClassLoader loader,
            final @NotNull Collection<Class<?>> classes,
            final @NotNull String packageName
    ) throws IOException, ClassNotFoundException {
        try (final var packageStream = loader.getResourceAsStream(packageName.replace('.', '/'))) {
            if (packageStream == null)
                return;

            findClasses(loader, classes, packageName, packageStream);
        }
    }

    private static void findClasses(
            final @NotNull ClassLoader loader,
            final @NotNull Collection<Class<?>> classes,
            final @NotNull String packageName,
            final @NotNull InputStream packageStream
    ) throws IOException, ClassNotFoundException {
        final var reader = new BufferedReader(new InputStreamReader(packageStream));
        for (String line; (line = reader.readLine()) != null; ) {
            if (line.endsWith(".class")) {
                final var name      = line.substring(0, line.lastIndexOf(".class"));
                final var className = packageName.isEmpty() ? name : (packageName + '.' + name);
                final var cls       = loader.loadClass(className);
                classes.add(cls);
                continue;
            }
            findClasses(loader, classes, packageName.isEmpty() ? line : (packageName + '.' + line));
        }
    }

    private final Collection<Class<?>> classes;

    public ClassScanner() throws IOException, ClassNotFoundException {
        classes = new ArrayList<>();

        final var loader = ClassLoader.getSystemClassLoader();
        findClasses(loader, classes, "");
    }

    public @NotNull Stream<Class<?>> stream() {
        return classes.stream();
    }

    public @NotNull Stream<Class<?>> withAnnotation(final @NotNull Class<? extends Annotation> annotation) {
        return stream().filter(cls -> cls.isAnnotationPresent(annotation));
    }
}
