package io.paymeter.assessment.infrastructure.entrypoints.parking.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;


@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@RequiredArgsConstructor
public class ParkingRequest {
    private final String REGEX="^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])T([01]\\d|2[0-3]):([0-5]\\d):([0-5]\\d)\\.\\d{3}$";

    @NotBlank(message = "El parametro parkingId es obligatorio")
    @NotNull(message = "El parametro parkingId es obligatorio")
    private String parkingId;

    @NotBlank(message = "El parametro from es obligatorio")
    @NotNull(message = "El parametro from es obligatorio")

    @Pattern(regexp = REGEX, message = "Formato inválido del parametro from, debe ser yyyy-MM-dd'T'HH:mm:ss.SSS")
    private String from;

    @Pattern(regexp = REGEX, message = "Formato inválido del parametro to, debe ser yyyy-MM-dd'T'HH:mm:ss.SSS")
    private String to;


}
