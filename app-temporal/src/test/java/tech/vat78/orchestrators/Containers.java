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

    public static final int DB_PORT = 13130;
    public static final String DB_NAME= "orchestrators";
    public static final int TEMPORAL_PORT = 13131;
    public static final String TEMPORAL_NAMESPACE= "orchestrators";

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

    public static GenericContainer<?> temporalContainer = new GenericContainer<>("temporalio/auto-setup:1.26.2")
            .withNetworkAliases("temporal")
            .withNetwork(network)
            .dependsOn(postgres)
            .withLogConsumer(f -> log.info("temporal-container] - {}", f.getUtf8StringWithoutLineEnding()))
            .withEnv("DB", "postgres12")
            .withEnv("DB_PORT", "5432")
            .withEnv("POSTGRES_USER", postgres.getUsername())
            .withEnv("POSTGRES_PWD", postgres.getPassword())
            .withEnv("DEFAULT_NAMESPACE", TEMPORAL_NAMESPACE)
            .withEnv("POSTGRES_SEEDS", "postgres");

    public static void init() {
        postgres.setPortBindings(List.of(DB_PORT + ":5432"));
        temporalContainer.setPortBindings(List.of(TEMPORAL_PORT + ":7233"));
        postgres.start();
        temporalContainer.start();
    }
}
