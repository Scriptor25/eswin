package io.scriptor.eswin.util;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.IntFunction;

public class Stack<T> implements Collection<T> {

    private int size;
    private T[] elements;

    public Stack(final @NotNull IntFunction<T[]> supplier) {
        size = 0;
        elements = supplier.apply(10);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(final Object o) {
        for (int i = 0; i < size; ++i)
            if (Objects.equals(o, elements[i]))
                return true;
        return false;
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        return new Iterator<>() {

            int index = 0;

            @Override
            public boolean hasNext() {
                return index < size;
            }

            @Override
            public T next() {
                if (index >= size)
                    throw new NoSuchElementException("index %d out of bounds [0;%d)".formatted(index, size));
                return elements[index++];
            }
        };
    }

    @Override
    public Object @NotNull [] toArray() {
        return Arrays.copyOfRange(elements, 0, size);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U> U @NotNull [] toArray(U @NotNull [] u) {
        if (u.length < size)
            u = Arrays.copyOf(u, size);
        for (int i = 0; i < size; ++i)
            u[i] = (U) elements[i];
        if (u.length > size)
            u[size] = null;
        return u;
    }

    @Override
    public boolean add(final T t) {
        if (elements.length == size)
            elements = Arrays.copyOf(elements, elements.length * 2 + 1);
        elements[size++] = t;
        return true;
    }

    @Override
    public boolean remove(final Object o) {
        for (int i = 0; i < size; ++i) {
            if (Objects.equals(o, elements[i])) {
                for (int j = i; j < size - 1; ++j)
                    elements[j] = elements[j + 1];
                elements[--size] = null;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsAll(final @NotNull Collection<?> collection) {
        for (final var o : collection)
            if (!contains(o))
                return false;
        return true;
    }

    @Override
    public boolean addAll(final @NotNull Collection<? extends T> collection) {
        for (final var o : collection)
            add(o);
        return !collection.isEmpty();
    }

    @Override
    public boolean removeAll(final @NotNull Collection<?> collection) {
        var modified = false;
        for (final var o : collection)
            modified |= remove(o);
        return modified;
    }

    @Override
    public boolean retainAll(final @NotNull Collection<?> collection) {
        final var delete     = new Object[size];
        int       deleteSize = 0;
        for (int i = 0; i < size; ++i) {
            final var e = elements[i];
            if (!collection.contains(e))
                delete[deleteSize++] = e;
        }
        for (int i = 0; i < deleteSize; ++i)
            remove(delete[i]);
        return deleteSize != 0;
    }

    @Override
    public void clear() {
        size = 0;
        Arrays.fill(elements, null);
    }

    public void push(final T t) {
        add(t);
    }

    public T pop() {
        if (size == 0)
            throw new NoSuchElementException();

        final var e = elements[--size];
        elements[size] = null;
        return e;
    }

    public T peek() {
        if (size == 0)
            return null;

        return elements[size - 1];
    }
}
