package tech.vat78.orchestrators.temporal.activity.impl;

import io.temporal.spring.boot.ActivityImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tech.vat78.orchestrators.core.eod.EodService;
import tech.vat78.orchestrators.core.eod.dto.EodContext;
import tech.vat78.orchestrators.temporal.activity.EodDayActivity;
import tech.vat78.orchestrators.utils.TelemetryComponent;

@Component
@ActivityImpl(taskQueues = "${spring.temporal.queues.eod}")
@RequiredArgsConstructor
public class EodDayActivityImpl implements EodDayActivity {

    private final EodService eodService;
    private final TelemetryComponent telemetryComponent;

    @Override
    public EodContext startEod() throws Exception {
        return telemetryComponent.logWithTraceExecution(eodService::startEod);
    }

    @Override
    public void switchOpenDay(EodContext context) {
        telemetryComponent.logWithTraceExecution(() -> eodService.switchOpenDay(context));
    }
}
