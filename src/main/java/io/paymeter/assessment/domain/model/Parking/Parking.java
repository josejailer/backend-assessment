package io.paymeter.assessment.domain.model.Parking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@RequiredArgsConstructor
public class Parking {
    private String parkingId;
    private BigDecimal hourlyRate;
    private BigDecimal maxRateAmount;
    private int maxRatePeriodHours; // 24 para P000123, 12 para P000456
    private int freeInitialHours;   // 0 para P000123, 1 para P000456


}
