package com.github.achmadns.lab;

import java.io.Serializable;
import net.openhft.chronicle.set.ChronicleSet;
import net.openhft.chronicle.set.ChronicleSetBuilder;

public class Events implements Serializable {
    private final ChronicleSet<String> data = ChronicleSetBuilder.of(String.class).create();

    public Events add(String event) {
        data.add(event);
        return this;
    }

    public ChronicleSet<String> data() {
        return data;
    }
}
