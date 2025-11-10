package io.paymeter.assessment.infrastructure.drivenadapters.mysqladapter.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("parking")
public class ParkingEntity {
    @Id
    @Column("parking_id")
    private String parkingId;

    @Column("hourly_rate")
    private BigDecimal hourlyRate;

    @Column("max_rate_amount")
    private BigDecimal maxRateAmount;

    @Column("max_rate_period_hours")
    private int maxRatePeriodHours;

    @Column("free_initial_hours")
    private int freeInitialHours;

}
