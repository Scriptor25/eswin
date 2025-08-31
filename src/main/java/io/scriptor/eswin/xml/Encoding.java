package io.scriptor.eswin.xml;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.PushbackInputStream;
import java.nio.charset.Charset;
import java.util.*;

public class Encoding {

    private Encoding() {
    }

    private final static Map<List<Integer>, Set<Integer>> STATES;
    private final static Map<List<Integer>, String> TAILS;

    static {
        STATES = new HashMap<>();
        STATES.put(List.of(), Set.of(0xEF, 0xFE, 0x00, 0xFF, 0x2B, 0xF7, 0xDD, 0x0E, 0xFB, 0x84));

        STATES.put(List.of(0xEF), Set.of(0xBB));
        STATES.put(List.of(0xEF, 0xBB), Set.of(0xBF));

        STATES.put(List.of(0xFE), Set.of(0xFF));

        STATES.put(List.of(0x00), Set.of(0x00));
        STATES.put(List.of(0x00, 0x00), Set.of(0xFE));
        STATES.put(List.of(0x00, 0x00, 0xFE), Set.of(0xFF));

        STATES.put(List.of(0xFF), Set.of(0xFE));
        STATES.put(List.of(0xFF, 0xFE), Set.of(0x00));
        STATES.put(List.of(0xFF, 0xFE, 0x00), Set.of(0x00));

        STATES.put(List.of(0x2B), Set.of(0x2F));
        STATES.put(List.of(0x2B, 0x2F), Set.of(0x76));

        STATES.put(List.of(0xF7), Set.of(0x64));
        STATES.put(List.of(0xF7, 0x64), Set.of(0x4C));

        STATES.put(List.of(0xDD), Set.of(0x73));
        STATES.put(List.of(0xDD, 0x73), Set.of(0x66));
        STATES.put(List.of(0xDD, 0x73, 0x66), Set.of(0x73));

        STATES.put(List.of(0x0E), Set.of(0xFE));
        STATES.put(List.of(0x0E, 0xFE), Set.of(0xEE));

        STATES.put(List.of(0xFB), Set.of(0xEE));
        STATES.put(List.of(0xFB, 0xEE), Set.of(0x28));

        STATES.put(List.of(0x84), Set.of(0x31));
        STATES.put(List.of(0x84, 0x31), Set.of(0x95));
        STATES.put(List.of(0x84, 0x31, 0x95), Set.of(0x33));

        TAILS = new HashMap<>();
        TAILS.put(List.of(0xEF, 0xBB, 0xBF), "UTF-8");
        TAILS.put(List.of(0xFE, 0xFF), "UTF-16BE");
        TAILS.put(List.of(0xFF, 0xFE), "UTF-16LE");
        TAILS.put(List.of(0x00, 0x00, 0xFE, 0xFF), "UTF-32BE");
        TAILS.put(List.of(0xFF, 0xFE, 0x00, 0x00), "UTF-32LE");
        TAILS.put(List.of(0x2B, 0x2F, 0x76), "UTF-7");
        TAILS.put(List.of(0xF7, 0x64, 0x4C), "UTF-1");
        TAILS.put(List.of(0xDD, 0x73, 0x66, 0x73), "UTF-EBCDIC");
        TAILS.put(List.of(0x0E, 0xFE, 0xEE), "SCSU");
        TAILS.put(List.of(0xFB, 0xEE, 0x28), "BOCU-1");
        TAILS.put(List.of(0x84, 0x31, 0x95, 0x33), "GB18030");
    }

    public static Charset detect(final @NotNull PushbackInputStream stream) throws IOException {
        final List<Integer> state = new ArrayList<>();
        while (STATES.containsKey(state)) {
            final var data = stream.read();
            if (!STATES.get(state).contains(data)) {
                stream.unread(data);
                break;
            }
            state.add(data);
        }
        if (TAILS.containsKey(state))
            return Charset.forName(TAILS.get(state));
        return Charset.defaultCharset();
    }
}
