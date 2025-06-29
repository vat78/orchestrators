package tech.vat78.orchestrators.core.account;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Transactional(value = Transactional.TxType.MANDATORY)
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    @Override
    public void clientInterestPayment(LocalDate statementDay, int threadId) {

    }

    @Override
    public void debtInterestClaim(LocalDate statementDay, int threadId) {

    }

    @Override
    public void calculateClientInterest(int threadId) {

    }

    @Override
    public void calculateDebtInterest(int threadId) {

    }
}
