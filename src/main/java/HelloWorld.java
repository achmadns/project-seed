import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.sql.ResultSet;

public class HelloWorld {
    private static final Logger log = LoggerFactory.getLogger(HelloWorld.class);

    public static void main(String[] args) throws InterruptedException {
        final HikariDataSource ds = new HikariDataSource(config(args));
        log.info("Data source created.");
        final Sql2o sql = new Sql2o(ds);
        final Thread tester = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    try (Connection connection = sql.open()) {
                        connection.createQuery("SELECT * FROM mydb.PC;").executeAndFetch((ResultSet resultSet) -> null);
                        log.info("Query success!");
                    } catch (Exception e) {
                        log.error("Unable to obtain DB connection!");
                    }
                } catch (Exception e) {
                    log.error("Oops!", e);
                }
            }
        });
        tester.start();
        tester.join();
    }

    private static HikariConfig config(String[] args) {
        final HikariConfig config = new HikariConfig();
        config.setPoolName("mydbpool");
        config.setDriverClassName(org.mariadb.jdbc.Driver.class.getName());
        config.setJdbcUrl(args[0]);
        config.setUsername(args[1]);
        config.setPassword(args[2]);
        config.setConnectionTestQuery("SELECT 1");
        config.setMaxLifetime(60000); // 60 Sec
        config.setIdleTimeout(45000); // 45 Sec
        config.setMaximumPoolSize(50); // 50 Connections (including idle connections)
        return config;
    }
}
