package tech.vat78.orchestrators.camunda;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.vat78.orchestrators.core.eod.EodService;
import tech.vat78.orchestrators.core.eod.dto.EodContext;
import tech.vat78.orchestrators.utils.TelemetryComponent;

import java.util.Map;

import static tech.vat78.orchestrators.camunda.EodCamundaConstants.*;

@Service
@RequiredArgsConstructor
public class EodCamundaWorker {

    private static final String MDC_CAMUNDA_PROCESS = "camundaProcessId";
    
    private final EodService eodService;
    private final TelemetryComponent telemetryComponent;

    @JobWorker(type = EOD_START_WORKER)
    public Map<String, Object> startEod(final ActivatedJob job) throws Exception {
        return telemetryComponent.logWithTraceExecution(Map.of(MDC_CAMUNDA_PROCESS, job.getBpmnProcessId()), () -> {
            EodContext eodContext = eodService.startEod();
            return Map.of(CONTEXT_VARIABLE, eodContext);
        });
    }

    @JobWorker(type = EOD_SWITCH_DAY_WORKER)
    public Map<String, Object> switchDay(final ActivatedJob job,
                                        @Variable(name = CONTEXT_VARIABLE) EodContext eodContext) throws Exception {
        return telemetryComponent.logWithTraceExecution(Map.of(MDC_CAMUNDA_PROCESS, job.getBpmnProcessId()), () -> {
            eodService.switchOpenDay(eodContext);
            return Map.of();
        });
    }

    @JobWorker(type = EOD_INTEREST_PAY_WORKER)
    public Map<String, Object> payClientInterest(final ActivatedJob job,
                                                 @Variable(name = CONTEXT_VARIABLE) EodContext eodContext,
                                                 @Variable(name = THREAD_ID_VARIABLE) int threadId) throws Exception {
        return telemetryComponent.logWithTraceExecution(Map.of(MDC_CAMUNDA_PROCESS, job.getBpmnProcessId()), () -> {
            eodService.clientInterestPayment(eodContext, threadId);
            return Map.of();
        });
    }

    @JobWorker(type = EOD_CLIENT_INTEREST_WORKER)
    public Map<String, Object> calculateClientInterest(final ActivatedJob job,
                                                       @Variable(name = CONTEXT_VARIABLE) EodContext eodContext,
                                                       @Variable(name = THREAD_ID_VARIABLE) int threadId) throws Exception {
        return telemetryComponent.logWithTraceExecution(Map.of(MDC_CAMUNDA_PROCESS, job.getBpmnProcessId()), () -> {
            eodService.clientInterestCalculation(eodContext, threadId);
            return Map.of();
        });
    }
    
    @JobWorker(type = EOD_INTEREST_CLAIM_WORKER)
    public Map<String, Object> claimDebtInterest(final ActivatedJob job,
                                                 @Variable(name = CONTEXT_VARIABLE) EodContext eodContext,
                                                 @Variable(name = THREAD_ID_VARIABLE) int threadId) throws Exception {
        return telemetryComponent.logWithTraceExecution(Map.of(MDC_CAMUNDA_PROCESS, job.getBpmnProcessId()), () -> {
            eodService.debtInterestClaim(eodContext, threadId);
            return Map.of();
        });
    }

    @JobWorker(type = EOD_DEBT_INTEREST_WORKER)
    public Map<String, Object> calculateDebtInterest(final ActivatedJob job, 
                                                     @Variable(name = CONTEXT_VARIABLE) EodContext eodContext,
                                                     @Variable(name = THREAD_ID_VARIABLE) int threadId) throws Exception {
        return telemetryComponent.logWithTraceExecution(Map.of(MDC_CAMUNDA_PROCESS, job.getBpmnProcessId()), () -> {
            eodService.debtInterestCalculation(eodContext, threadId);
            return Map.of();
        });
    }

    @JobWorker(type = EOD_OVERDUE_WORKER)
    public Map<String, Object> overdueHandling(final ActivatedJob job, 
                                               @Variable(name = CONTEXT_VARIABLE) EodContext eodContext,
                                               @Variable(name = THREAD_ID_VARIABLE) int threadId) throws Exception {
        return telemetryComponent.logWithTraceExecution(Map.of(MDC_CAMUNDA_PROCESS, job.getBpmnProcessId()), () -> {
            eodService.overdueStep(eodContext, threadId);
            return Map.of();
        });
    }
}
