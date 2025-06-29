package tech.vat78.orchestrators;

import io.temporal.api.enums.v1.ScheduleOverlapPolicy;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.schedules.*;
import io.temporal.common.RetryOptions;
import io.temporal.worker.WorkerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import tech.vat78.orchestrators.temporal.workflow.EodWorkflow;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationStartupService implements ApplicationListener<ApplicationReadyEvent> {

    private final WorkerFactory workerFactory;
    private final ScheduleClient scheduleClient;

    @Value("${spring.temporal.queues.eod}")
    private String queueName;

    @Override
    public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
        log.info("Run actions after application startup");
        workerFactory.start();

        String scheduleName = queueName + "-schedule";
        enableEodSchedule(scheduleName);
    }

    private void enableEodSchedule(String scheduleId) {
        Schedule schedule = Schedule.newBuilder()
                .setAction(ScheduleActionStartWorkflow.newBuilder()
                        .setWorkflowType(EodWorkflow.class)
                        .setOptions(
                                WorkflowOptions.newBuilder()
                                        .setWorkflowId(queueName)
                                        .setTaskQueue(queueName)
                                        .setRetryOptions(RetryOptions.newBuilder()
                                                .setMaximumAttempts(1)
                                                .build())
                                        .build())
                        .build())
                .setSpec(ScheduleSpec.newBuilder().setCronExpressions(List.of("0 0 * * *")).build())
                .setPolicy(SchedulePolicy.newBuilder().setOverlap(ScheduleOverlapPolicy.SCHEDULE_OVERLAP_POLICY_BUFFER_ONE).build())
                .build();
        try {
            ScheduleHandle handle = scheduleClient.createSchedule(scheduleId, schedule, ScheduleOptions.newBuilder().build());
            log.info("The new schedule: {}", handle.describe().getInfo());

        } catch (Exception e) {
            log.warn("Can't create the schedule task in Temporal. Delete the old one manually, if you need to update it");
        }
    }
}
