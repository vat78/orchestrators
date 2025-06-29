package tech.vat78.orchestrators.core.account;

import java.time.LocalDate;

public interface AccountService {
    void clientInterestPayment(LocalDate statementDay, int threadId);
    void debtInterestClaim(LocalDate statementDay, int threadId);
    void calculateClientInterest(int threadId);
    void calculateDebtInterest(int threadId);
}
