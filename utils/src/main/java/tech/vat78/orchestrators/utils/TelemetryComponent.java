package tech.vat78.orchestrators.utils;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

@Slf4j
@Component
public class TelemetryComponent {

    private static final String MDC_MY_TRACE_FIELD = "my-trace";

    private final Counter errorCounter;

    public TelemetryComponent(MeterRegistry meterRegistry) {
        this.errorCounter = Counter.builder("application.unexpected_errors").register(meterRegistry);
    }

    public void logWithTraceExecution(Runnable action) {
        String traceId = UUID.randomUUID().toString();
        logWithTraceExecution(Map.of(MDC_MY_TRACE_FIELD, traceId), action);
    }

    public void logWithTraceExecution(Map<String, String> mdcParameters, Runnable action) {
        var mdcCloseableList = addMdcParameters(mdcParameters);
        try {
            action.run();
        } catch (Exception ex) {
            logAndIncrementMetric(ex);
            throw ex;
        } finally {
            mdcCloseableList.forEach(MDC.MDCCloseable::close);
        }
    }

    public <T> T logWithTraceExecution(Callable<T> callable) throws Exception {
        String traceId = UUID.randomUUID().toString();
        return logWithTraceExecution(Map.of(MDC_MY_TRACE_FIELD, traceId), callable);
    }

    public <T> T logWithTraceExecution( Map<String, String> mdcParameters, Callable<T> callable) throws Exception {
        var mdcCloseableList = addMdcParameters(mdcParameters);
        try {
            return callable.call();
        } catch (Exception ex) {
            logAndIncrementMetric(ex);
            throw ex;
        } finally {
            mdcCloseableList.forEach(MDC.MDCCloseable::close);
        }
    }

    private List<MDC.MDCCloseable> addMdcParameters(Map<String, String> mdcParameters) {
        return mdcParameters.entrySet().stream()
                .map((entry) -> MDC.putCloseable(entry.getKey(), entry.getValue()))
                .toList();
    }

    private void logAndIncrementMetric(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        errorCounter.increment();
    }
}
