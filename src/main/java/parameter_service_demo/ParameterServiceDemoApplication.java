package parameter_service_demo;

import org.h2.tools.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.sql.SQLException;

@SpringBootApplication
public class ParameterServiceDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ParameterServiceDemoApplication.class, args);
    }

    /**
     * Start H2 TCP server so we can connect to in-memory database from outside the current JVM.
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server h2Server() throws SQLException {
        return Server.createTcpServer("-tcpPort", "9090", "-tcpAllowOthers");
    }
}
