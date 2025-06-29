package tech.vat78.orchestrators.core.day.db;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Entity
@Table(name = "open_days")
@Getter
@Setter
@Accessors(fluent = true)
@ToString
public class OpenDayEntity {
    @Id
    private LocalDate day;
    @Enumerated(EnumType.STRING)
    private DayStatus status;
    boolean isClosing;
}
