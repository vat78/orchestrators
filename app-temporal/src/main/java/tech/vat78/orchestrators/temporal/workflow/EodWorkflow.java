package tech.vat78.orchestrators.temporal.workflow;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface EodWorkflow {

    @WorkflowMethod
    void eodRun();

}
