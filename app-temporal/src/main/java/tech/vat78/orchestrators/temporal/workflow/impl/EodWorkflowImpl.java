package tech.vat78.orchestrators.temporal.workflow.impl;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Async;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Workflow;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import tech.vat78.orchestrators.core.eod.dto.EodContext;
import tech.vat78.orchestrators.temporal.activity.EodByThreadActivity;
import tech.vat78.orchestrators.temporal.activity.EodDayActivity;
import tech.vat78.orchestrators.temporal.workflow.EodWorkflow;

import java.time.Duration;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;

@WorkflowImpl(taskQueues = "${spring.temporal.queues.eod}")
@RequiredArgsConstructor
public class EodWorkflowImpl implements EodWorkflow {

    private final Logger log = Workflow.getLogger(EodWorkflowImpl.class);

    private final static RetryOptions retryOptions = RetryOptions.newBuilder()
            .setMaximumAttempts(1)
            .build();

    private final static ActivityOptions activityOptions = ActivityOptions.newBuilder()
            .setRetryOptions(retryOptions)
            .setStartToCloseTimeout(Duration.ofHours(1))
            .build();

    private final EodDayActivity eodDayActivity =
            Workflow.newActivityStub(EodDayActivity.class, activityOptions);

    private final EodByThreadActivity eodByThreadActivity =
            Workflow.newActivityStub(EodByThreadActivity.class, activityOptions);

    @Override
    public void eodRun() {
        try {

            EodContext context = eodDayActivity.startEod();
            log.info("EOD has started for {}", context);
            runStepByThreadsInParallel(context, eodByThreadActivity::clientInterestPayment);
            runStepByThreadsInParallel(context, eodByThreadActivity::debtInterestClaim);
            runStepByThreadsInParallel(context, eodByThreadActivity::overdueStep);
            eodDayActivity.switchOpenDay(context);
            runStepByThreadsInParallel(context, eodByThreadActivity::clientInterestCalculation);
            runStepByThreadsInParallel(context, eodByThreadActivity::debtInterestCalculation);
            log.info("EOD has finished for {}", context);

        } catch (Exception exception) {
            log.error("Unexpected error", exception);
        }
    }

    private void runStepByThreadsInParallel(EodContext context, BiConsumer<EodContext, Integer> stepFunction) {
        List<Promise<Void>> promises = IntStream.range(0, context.threadsCount())
                .mapToObj(thread -> Async.procedure(
                        () -> stepFunction.accept(context, thread)))
                .toList();
        Promise.allOf(promises).get();
    }
}
