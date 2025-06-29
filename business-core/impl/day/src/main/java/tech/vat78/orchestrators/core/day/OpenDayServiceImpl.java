package tech.vat78.orchestrators.core.day;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.vat78.orchestrators.core.day.db.DayStatus;
import tech.vat78.orchestrators.core.day.db.OpenDayEntity;
import tech.vat78.orchestrators.core.day.db.OpenDayRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OpenDayServiceImpl implements OpenDayService {

    private final OpenDayRepository openDayRepository;

    @Override
    public LocalDate getOpenDay() {
        OpenDayEntity entity = openDayRepository.findOpenDayEntityByStatus(DayStatus.OPEN)
                .orElseGet(this::initiateDate);
        return entity.isClosing() ? entity.day().plusDays(1L) : entity.day();
    }

    @Override
    public void startDayClosing(LocalDate day) {
        var entity = openDayRepository.findById(day)
                .orElseThrow();
        if (entity.status() != DayStatus.OPEN) {
            throw new IllegalStateException("Try to start closing the wrong day " + day);
        }
        entity.isClosing(true);
        openDayRepository.save(entity);
    }

    @Override
    public void closeDay(LocalDate day) {
        var entity = openDayRepository.findById(day)
                .orElseThrow();
        if (entity.status() != DayStatus.OPEN) {
            throw new IllegalStateException("Try to close the wrong day " + day);
        }
        entity.isClosing(false);
        entity.status(DayStatus.CLOSED);

        var nextDay = new OpenDayEntity()
                .day(day.plusDays(1L))
                .status(DayStatus.OPEN)
                .isClosing(false);
        openDayRepository.saveAll(List.of(entity, nextDay));
    }

    private OpenDayEntity initiateDate() {
        var today = new OpenDayEntity()
                .day(LocalDate.now())
                .status(DayStatus.OPEN)
                .isClosing(false);
        return openDayRepository.save(today);
    }
}
