package io.paymeter.assessment.domain.model.pricing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@RequiredArgsConstructor
public class Pricing {
    private String parkingId;
    private LocalDateTime from;
    private LocalDateTime to;
    private long duration;
    private String price;
}
