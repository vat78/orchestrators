package tech.vat78.orchestrators;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.DeploymentEvent;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationStartupService implements ApplicationListener<ApplicationReadyEvent> {

    private static final Path EOD_SCHEMA_FILE = Path.of("bpmn", "eod.bpmn");

    private final ZeebeClient zeebeClient;

    @Override
    public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
        log.info("Run actions after application startup");
        publishSchema();
    }

    private void publishSchema() {
        DeploymentEvent deploymentEvent = zeebeClient.newDeployResourceCommand()
                .addResourceString(getProcessingSchema(EOD_SCHEMA_FILE), UTF_8, EOD_SCHEMA_FILE.getFileName().toString())
                .send()
                .join();
        log.info("EoD schema published with result: {}", deploymentEvent.getProcesses());
    }

    @SneakyThrows({IOException.class, NullPointerException.class})
    private static String getProcessingSchema(Path schemaFile) {
        try (InputStream schemaStream = ApplicationStartupService.class.getClassLoader().getResourceAsStream(schemaFile.toString())) {
            return new String(schemaStream.readAllBytes());
        }
    }

}
