package tech.vat78.orchestrators.core.day.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface OpenDayRepository extends JpaRepository<OpenDayEntity, LocalDate> {
    Optional<OpenDayEntity> findOpenDayEntityByStatus(DayStatus status);
}
