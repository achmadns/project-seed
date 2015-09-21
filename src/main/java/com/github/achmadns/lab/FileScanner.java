package com.github.achmadns.lab;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import net.openhft.chronicle.map.ChronicleMap;
import net.openhft.chronicle.map.ChronicleMapBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileScanner {

    private final String path;
    private static final Logger log = LoggerFactory.getLogger(FileScanner.class);
    private final Pattern pattern = Pattern.compile("(id = )(\\d+)");
    private final ChronicleMap<String, Events> cache = ChronicleMapBuilder.of(String.class, Events.class).create();

    public FileScanner(String path) {
        this.path = path;
    }

    public FileScanner scan() throws IOException {
        try (final Stream<String> lines = Files.lines(Paths.get("simple.txt"))) {
            final StringBuilder buffer = new StringBuilder();
            lines.forEach(line -> {
                if (line.startsWith("Event")) {
                    scan(buffer.append(line).toString());
                    buffer.delete(0, buffer.length());
                    return;
                }
                buffer.append(line).append("\n");
            });
        }
        return this;
    }

    private void scan(String event) {
        final String eventId = check(event);
        if (eventId == null) return;
        final Events events = cache.get(eventId);
        if (events == null) {
            cache.put(eventId, new Events().add(event));
            return;
        }
        events.data().add(event);
    }

    public String check(String event) {
        final Matcher matcher = pattern.matcher(event);
        return matcher.find() ? matcher.group(2) : null;
    }

    public ChronicleMap<String, Events> cache() {
        return cache;
    }

    public String path() {
        return path;
    }
}
