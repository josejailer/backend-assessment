package io.paymeter.assessment.infrastructure.entrypoints.parking.dto;

import lombok.*;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@RequiredArgsConstructor
public class ErrorResponse {
    private String message;
    private int code;

}
