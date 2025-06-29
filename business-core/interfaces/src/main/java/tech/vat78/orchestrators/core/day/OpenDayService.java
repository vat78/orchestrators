package tech.vat78.orchestrators.core.day;

import java.time.LocalDate;

public interface OpenDayService {
    LocalDate getOpenDay();
    void startDayClosing(LocalDate day);
    void closeDay(LocalDate day);
}
