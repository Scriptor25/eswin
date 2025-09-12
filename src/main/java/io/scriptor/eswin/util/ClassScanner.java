package io.scriptor.eswin.util;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public class ClassScanner {

    private static void findClasses(
            final @NotNull ClassLoader loader,
            final @NotNull Set<Class<?>> classes,
            final @NotNull String packageName
    ) throws IOException, URISyntaxException {
        Log.info("open package '%s'", packageName);

        final var resources = loader.getResources(packageName.replace('.', '/'));
        while (resources.hasMoreElements()) {
            final var resource = resources.nextElement();
            switch (resource.getProtocol()) {
                case "file" -> {
                    final var directory = new File(resource.toURI());
                    findClasses(loader, classes, packageName, directory);
                }
                case "jar" -> {
                    final var filepath = resource.getPath().substring(5, resource.getPath().indexOf('!'));
                    try (final var jar = new JarFile(URLDecoder.decode(filepath, StandardCharsets.UTF_8))) {
                        findClasses(loader, classes, jar);
                    }
                }
                default -> Log.warn(" * unhandled protocol: '%s'", resource.getProtocol());
            }
        }
    }

    private static void findClasses(
            final @NotNull ClassLoader loader,
            final @NotNull Set<Class<?>> classes,
            final @NotNull String packageName,
            final @NotNull File directory
    ) {
        Log.info("open directory '%s'", directory);

        final var files = directory.listFiles();
        if (files == null) {
            Log.warn(" * no files in directory '%s'", directory);
            return;
        }

        for (final var file : files) {
            if (file.isDirectory()) {
                findClasses(loader, classes, packageName + '.' + file.getName(), file);
                continue;
            }
            if (file.getName().endsWith(".class")) {
                addClass(loader, classes, packageName + '.' + file.getName());
            }
        }
    }

    private static void findClasses(
            final @NotNull ClassLoader loader,
            final @NotNull Set<Class<?>> classes,
            final @NotNull JarFile jar
    ) {
        Log.info("open jar '%s'", jar.getName());

        final var entries = jar.entries();
        while (entries.hasMoreElements()) {
            final var entry = entries.nextElement();
            if (entry.getName().endsWith(".class")) {
                addClass(loader, classes, entry.getName());
            }
        }
    }

    private static void addClass(
            final @NotNull ClassLoader loader,
            final @NotNull Set<Class<?>> classes,
            final @NotNull String filename
    ) {
        final var name = filename.substring(0, filename.lastIndexOf(".class")).replace('/', '.');
        try {
            final var cls = loader.loadClass(name);
            classes.add(cls);

            Log.info(" - %s", cls);
        } catch (final ClassNotFoundException e) {
            Log.warn(" * while loading class '%s': %s", name, e);
        }
    }

    private final Set<Class<?>> classes = new HashSet<>();

    public ClassScanner(final @NotNull String... packageNames)
            throws IOException, ClassNotFoundException, URISyntaxException {

        final var loader = getClass().getClassLoader();
        for (final var packageName : packageNames) {
            findClasses(loader, classes, packageName);
        }
    }

    public @NotNull Stream<Class<?>> stream() {
        return classes.stream();
    }

    public @NotNull Stream<Class<?>> withAnnotation(final @NotNull Class<? extends Annotation> annotation) {
        return stream().filter(cls -> cls.isAnnotationPresent(annotation));
    }
}
