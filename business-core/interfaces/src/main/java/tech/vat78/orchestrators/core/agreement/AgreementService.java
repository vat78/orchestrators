package tech.vat78.orchestrators.core.agreement;

import java.time.LocalDate;
import java.util.List;

public interface AgreementService {
    List<Long> findAllAgreementsByStatementDay(LocalDate statementDay, int threadId);
    List<Long> findAllAgreements(int threadId);
    void setOverdue(LocalDate statementDay, int threadId);
}
