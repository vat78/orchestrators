package tech.vat78.orchestrators.camunda.eod;

import io.camunda.zeebe.client.ZeebeClient;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import tech.vat78.orchestrators.Containers;
import tech.vat78.orchestrators.core.client.ClientService;
import tech.vat78.orchestrators.core.day.OpenDayService;
import tech.vat78.orchestrators.core.eod.db.EodStageEntity;
import tech.vat78.orchestrators.core.eod.db.EodStageRepository;
import tech.vat78.orchestrators.core.eod.db.EodStageType;

import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class EodTest {

    @BeforeAll
    static void init() {
        Containers.init();
    }

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> "jdbc:postgresql://localhost:" + Containers.DB_PORT + "/" + Containers.DB_NAME);
        registry.add("spring.datasource.username", Containers.postgres::getUsername);
        registry.add("spring.datasource.password", Containers.postgres::getPassword);
        registry.add("camunda.client.zeebe.grpc-address", () -> "http://localhost:" + Containers.ZEEBE_PORT);
    }

    @Autowired
    private ZeebeClient zeebeClient;
    @Autowired
    private EodStageRepository eodStageRepository;
    @Autowired
    private OpenDayService openDayService;

    @Test
    public void testEodStagesCompletion() {
        LocalDate testingDay = openDayService.getOpenDay();

        zeebeClient.newBroadcastSignalCommand()
                .signalName("test_eod_start_signal")
                .send()
                .join();

        Awaitility.await().atMost(Duration.ofSeconds(30)).untilAsserted(() ->
                assertAll(assertMultiThreadStageCompletion(testingDay, EodStageType.DEBT_INTEREST_CALCULATION)));

        List<Executable> assertions = new ArrayList<>();
        assertions.add(assertStageCompletion(testingDay, EodStageType.START, null));
        assertions.add(assertStageCompletion(testingDay, EodStageType.SWITCH_DAY, null));
        assertions.addAll(assertMultiThreadStageCompletion(testingDay, EodStageType.CLIENT_INTEREST_CALCULATION));
        assertions.addAll(assertMultiThreadStageCompletion(testingDay, EodStageType.CLIENT_INTEREST_PAYMENT));
        assertions.addAll(assertMultiThreadStageCompletion(testingDay, EodStageType.DEBT_INTEREST_CALCULATION));
        assertions.addAll(assertMultiThreadStageCompletion(testingDay, EodStageType.DEBT_INTEREST_CLAIM));
        assertions.addAll(assertMultiThreadStageCompletion(testingDay, EodStageType.OVERDUE));

        assertAll(assertions);
    }

    @Test
    public void testEodFailingOnAlreadyCompletedStage() {
        LocalDate testingDay = openDayService.getOpenDay();
        var exitingStageRecord = new EodStageEntity()
                .closingDay(testingDay)
                .type(EodStageType.OVERDUE)
                .threadId(33)
                .completedAt(OffsetDateTime.now());
        eodStageRepository.save(exitingStageRecord);

        zeebeClient.newBroadcastSignalCommand()
                .signalName("test_eod_start_signal")
                .send()
                .join();

        Awaitility.await().atMost(Duration.ofSeconds(30)).untilAsserted(() ->
                assertAll(assertMultiThreadStageCompletion(testingDay, EodStageType.DEBT_INTEREST_CALCULATION)));

        var actualStageRecord = eodStageRepository.findByClosingDayAndTypeAndThreadId(exitingStageRecord.closingDay(), exitingStageRecord.type(), exitingStageRecord.threadId());
        assertEquals(exitingStageRecord.completedAt().toInstant(), actualStageRecord.get().completedAt().toInstant(), "Completion timestamp should not be changed");
    }

    private Executable assertStageCompletion(LocalDate day, EodStageType type, Integer threadId) {
        return () -> assertTrue(eodStageRepository.findByClosingDayAndTypeAndThreadId(day, type, threadId).isPresent(), type.name() + " for thread:" + threadId);
    }

    private List<Executable> assertMultiThreadStageCompletion(LocalDate day, EodStageType type) {
        return IntStream.range(0, ClientService.THREADS_COUNT)
                .mapToObj(i -> assertStageCompletion(day, type, i))
                .toList();
    }
}
