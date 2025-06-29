package tech.vat78.orchestrators.core.eod;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tech.vat78.orchestrators.core.account.AccountService;
import tech.vat78.orchestrators.core.agreement.AgreementService;
import tech.vat78.orchestrators.core.client.ClientService;
import tech.vat78.orchestrators.core.day.OpenDayService;
import tech.vat78.orchestrators.core.eod.db.EodStageEntity;
import tech.vat78.orchestrators.core.eod.db.EodStageRepository;
import tech.vat78.orchestrators.core.eod.db.EodStageType;
import tech.vat78.orchestrators.core.eod.dto.EodContext;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import static java.time.ZoneOffset.UTC;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EodServiceImpl implements EodService {

    private final OpenDayService openDayService;
    private final AccountService accountService;
    private final AgreementService agreementService;
    private final EodStageRepository eodStageRepository;

    @Override
    public EodContext startEod() {
        LocalDate closingDay = openDayService.getOpenDay();
        safeRun(closingDay, EodStageType.START, () -> openDayService.startDayClosing(closingDay));
        return new EodContext(closingDay, ClientService.THREADS_COUNT);
    }

    @Override
    public void clientInterestPayment(EodContext context, int threadId) {
        safeRun(context.closingDay(), EodStageType.CLIENT_INTEREST_PAYMENT, threadId,
                () -> accountService.clientInterestPayment(context.closingDay(), threadId));
    }

    @Override
    public void debtInterestClaim(EodContext context, int threadId) {
        safeRun(context.closingDay(), EodStageType.DEBT_INTEREST_CLAIM, threadId,
                () -> accountService.debtInterestClaim(context.closingDay(), threadId));
    }

    @Override
    public void overdueStep(EodContext context, int threadId) {
        safeRun(context.closingDay(), EodStageType.OVERDUE, threadId,
                () -> agreementService.setOverdue(context.closingDay(), threadId));
    }

    @Override
    public void switchOpenDay(EodContext context) {
        safeRun(context.closingDay(), EodStageType.SWITCH_DAY, () -> openDayService.closeDay(context.closingDay()));
    }

    @Override
    public void clientInterestCalculation(EodContext context, int threadId) {
        safeRun(context.closingDay(), EodStageType.CLIENT_INTEREST_CALCULATION, threadId,
                () -> accountService.calculateClientInterest(threadId));
    }

    @Override
    public void debtInterestCalculation(EodContext context, int threadId) {
        safeRun(context.closingDay(), EodStageType.DEBT_INTEREST_CALCULATION, threadId,
                () -> accountService.calculateDebtInterest(threadId));
    }

    private void safeRun(LocalDate closingDay, EodStageType type, Runnable action) {
        safeRun(closingDay, type, null, action);
    }

    private void safeRun(LocalDate closingDay, EodStageType type, Integer threadId, Runnable action) {
        if (eodStageRepository.findByClosingDayAndTypeAndThreadId(closingDay, type, threadId).isPresent()) {
            log.warn("Stage {} for thread {} and closing day {} is already completed", type, threadId, closingDay);
            return;
        }
        action.run();
        var stageEntity = new EodStageEntity()
                .closingDay(closingDay)
                .type(type)
                .threadId(threadId)
                .completedAt(OffsetDateTime.now(UTC));
        eodStageRepository.save(stageEntity);
    }
}
