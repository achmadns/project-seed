import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static spark.Spark.*;

public class HelloWorld {
    private static final Logger log = LoggerFactory.getLogger(HelloWorld.class);
    public static void main(String[] args) {
        log.info("Hello World from logback!");
        before("/hello", (reques, response) -> log.info("Before /hello"));
        get("/hello", (request, response) -> "Hello World!");
        post("/hello/:name", (request, response) -> "Hello " + request.params(":name") + "!");
        after("/hello", ((request, response) -> log.info("After /hello")));
    }
}
