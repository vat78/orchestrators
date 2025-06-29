package tech.vat78.orchestrators.core.eod.db;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "eod_stages",
        uniqueConstraints = { @UniqueConstraint( columnNames = {"type", "closing_day", "thread_id" })})
@Getter
@Setter
@Accessors(fluent = true)
@ToString
public class EodStageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Enumerated(EnumType.STRING)
    private EodStageType type;
    private LocalDate closingDay;
    @Nullable
    private Integer threadId;
    OffsetDateTime completedAt;
}
