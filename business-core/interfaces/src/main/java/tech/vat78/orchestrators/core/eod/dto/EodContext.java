package tech.vat78.orchestrators.core.eod.dto;

import java.time.LocalDate;

public record EodContext(
        LocalDate closingDay,
        int threadsCount
) {
}
