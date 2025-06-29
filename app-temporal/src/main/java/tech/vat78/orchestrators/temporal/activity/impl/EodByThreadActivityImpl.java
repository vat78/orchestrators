package tech.vat78.orchestrators.temporal.activity.impl;

import io.temporal.spring.boot.ActivityImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tech.vat78.orchestrators.core.eod.EodService;
import tech.vat78.orchestrators.core.eod.dto.EodContext;
import tech.vat78.orchestrators.temporal.activity.EodByThreadActivity;
import tech.vat78.orchestrators.utils.TelemetryComponent;

@Component
@ActivityImpl(taskQueues = "${spring.temporal.queues.eod}")
@RequiredArgsConstructor
public class EodByThreadActivityImpl implements EodByThreadActivity {

    private final EodService eodService;
    private final TelemetryComponent telemetryComponent;

    @Override
    public void clientInterestPayment(EodContext context, int threadId) {
        telemetryComponent.logWithTraceExecution(() -> eodService.clientInterestPayment(context, threadId));
    }

    @Override
    public void debtInterestClaim(EodContext context, int threadId) {
        telemetryComponent.logWithTraceExecution(() -> eodService.debtInterestClaim(context, threadId));
    }

    @Override
    public void overdueStep(EodContext context, int threadId) {
        telemetryComponent.logWithTraceExecution(() -> eodService.overdueStep(context, threadId));
    }

    @Override
    public void clientInterestCalculation(EodContext context, int threadId) {
        telemetryComponent.logWithTraceExecution(() -> eodService.clientInterestCalculation(context, threadId));
    }

    @Override
    public void debtInterestCalculation(EodContext context, int threadId) {
        telemetryComponent.logWithTraceExecution(() -> eodService.debtInterestCalculation(context, threadId));
    }
}
