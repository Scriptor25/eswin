package io.scriptor.eswin.component;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class ContextProvider {

    public interface ContextFrame extends AutoCloseable {
        void close();
    }

    private final Map<Class<? extends Context>, Stack<Context>> map = new HashMap<>();

    public <C extends Context> ContextFrame provide(final @NotNull Class<C> type, final @NotNull C context) {
        final var stack = map.computeIfAbsent(type, _ -> new Stack<>(Context[]::new));
        stack.push(context);
        return stack::pop;
    }

    public <C extends Context> boolean provides(final @NotNull Class<C> type) {
        return map.containsKey(type) && !map.get(type).isEmpty();
    }

    public <C extends Context> @NotNull C use(final @NotNull Class<C> type) {
        if (!map.containsKey(type) || map.get(type).isEmpty())
            throw new NoSuchElementException();
        return type.cast(map.get(type).peek());
    }
}
