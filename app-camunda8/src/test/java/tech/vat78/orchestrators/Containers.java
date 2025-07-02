package tech.vat78.orchestrators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;

import java.time.Duration;
import java.util.List;

public class Containers {

    private static final Logger log = LoggerFactory.getLogger(Containers.class);

    public static final int DB_PORT = 13140;
    public static final String DB_NAME= "orchestrators";
    public static final int ZEEBE_PORT = 13141;

    private static final Network network = Network.newNetwork();

    public static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13")
            .withNetworkAliases("postgres")
            .withNetwork(network)
            .withDatabaseName(DB_NAME)
            .withLogConsumer(f -> log.info("postgres-container] - {}", f.getUtf8StringWithoutLineEnding()))
            .waitingFor(
                    new LogMessageWaitStrategy()
                            .withRegEx(".*database system is ready to accept connections.*\\s")
                            .withTimes(1)
                            .withStartupTimeout(Duration.ofSeconds(180))
            );

    public static GenericContainer<?> zeebeContainer = new GenericContainer<>("camunda/zeebe:8.7.6")
            .withNetworkAliases("zeebe")
            .withNetwork(network)
            .dependsOn(postgres)
            .withLogConsumer(f -> log.info("zeebe-container] - {}", f.getUtf8StringWithoutLineEnding()))
            .withEnv("ZEEBE_BROKER_DATA_DISKUSAGECOMMANDWATERMARK", "0.998")
            .withEnv("ZEEBE_BROKER_DATA_DISKUSAGEREPLICATIONWATERMARK", "0.999")
            .withEnv("JAVA_TOOL_OPTIONS", "-Xms512m -Xmx512m")
            .waitingFor(
                    new LogMessageWaitStrategy()
                            .withRegEx(".*Broker is ready.*")
                            .withTimes(1)
                            .withStartupTimeout(Duration.ofSeconds(180))
            );

    public static void init() {
        postgres.setPortBindings(List.of(DB_PORT + ":5432"));
        zeebeContainer.setPortBindings(List.of(ZEEBE_PORT + ":26500"));
        postgres.start();
        zeebeContainer.start();
    }
}
