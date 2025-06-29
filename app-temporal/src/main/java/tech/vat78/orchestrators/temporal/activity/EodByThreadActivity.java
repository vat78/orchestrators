package tech.vat78.orchestrators.temporal.activity;

import io.temporal.activity.ActivityInterface;
import tech.vat78.orchestrators.core.eod.dto.EodContext;

@ActivityInterface
public interface EodByThreadActivity {
    void clientInterestPayment(EodContext context, int threadId);
    void debtInterestClaim(EodContext context, int threadId);
    void overdueStep(EodContext context, int threadId);
    void clientInterestCalculation(EodContext context, int threadId);
    void debtInterestCalculation(EodContext context, int threadId);
}
