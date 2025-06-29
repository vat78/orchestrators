package tech.vat78.orchestrators.core.agreement;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(value = Transactional.TxType.MANDATORY)
@RequiredArgsConstructor
public class AgreementServiceImpl implements AgreementService {

    @Override
    public List<Long> findAllAgreementsByStatementDay(LocalDate statementDay, int threadId) {
        return List.of();
    }

    @Override
    public List<Long> findAllAgreements(int threadId) {
        return List.of();
    }

    @Override
    public void setOverdue(LocalDate statementDay, int threadId) {

    }
}
