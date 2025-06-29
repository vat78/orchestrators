package tech.vat78.orchestrators.core.eod;

import tech.vat78.orchestrators.core.eod.dto.EodContext;

public interface EodService {
    EodContext startEod();
    void clientInterestPayment(EodContext context, int threadId);
    void debtInterestClaim(EodContext context, int threadId);
    void overdueStep(EodContext context, int threadId);
    void switchOpenDay(EodContext context);
    void clientInterestCalculation(EodContext context, int threadId);
    void debtInterestCalculation(EodContext context, int threadId);
}
