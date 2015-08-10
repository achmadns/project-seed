import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.localserver.LocalTestServer;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static com.mashape.unirest.http.Unirest.get;
import static org.assertj.core.api.StrictAssertions.assertThat;

public class LocalTest {
    private final LocalTestServer server = new LocalTestServer(null, null);

    @BeforeClass
    public void setup() throws Exception {
        server.start();
        server.register("/", new HttpRequestHandler() {
            public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
                response.setEntity(new StringEntity("ok"));
            }
        });
    }

    @AfterClass
    public void clean() throws Exception {
        server.stop();
        Unirest.shutdown();
    }

    @Test
    public void test() throws UnirestException {
        assertThat(get("http:/" + server.getServiceAddress() + "/").asString().getBody()).isEqualTo("ok");
    }
}
