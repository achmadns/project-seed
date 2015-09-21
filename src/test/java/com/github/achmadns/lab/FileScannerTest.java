package com.github.achmadns.lab;

import java.util.regex.Matcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;


import static java.util.regex.Pattern.compile;
import static org.assertj.core.api.Assertions.assertThat;


public class FileScannerTest {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Test
    public void scan() throws Exception {
        log.info("Scan started");
        final FileScanner scanner = new FileScanner("simple.txt").scan();
        log.info("Scan ended");
        assertThat(2).isEqualTo(scanner.cache().size());
        assertThat(2).isEqualTo(scanner.cache().get("1235").data().size());
    }

    @Test
    public void number_should_exist() {
        final Matcher matcher = compile("(id = )(\\d+)").matcher("asdfasdf asdfasdf id = 1234, asdfasdf");
        assertThat(matcher.find()).isTrue();
        assertThat(matcher.group(2)).isEqualTo("1234");
    }

    @Test
    public void number_should_inexist() {
        final Matcher matcher = compile("(id = )(\\d+)").matcher("asdfasdf asdfasdf id = xxxx, asdfasdf");
        assertThat(matcher.find()).isFalse();
    }
}