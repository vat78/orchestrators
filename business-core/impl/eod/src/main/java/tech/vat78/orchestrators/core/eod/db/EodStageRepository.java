package tech.vat78.orchestrators.core.eod.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface EodStageRepository extends JpaRepository<EodStageEntity, Long> {
    Optional<EodStageEntity> findByClosingDayAndTypeAndThreadId(LocalDate closingDay,
                                                                EodStageType type,
                                                                Integer threadId);
}
