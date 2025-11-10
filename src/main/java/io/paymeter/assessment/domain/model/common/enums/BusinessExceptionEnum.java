package io.paymeter.assessment.domain.model.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BusinessExceptionEnum {

    PARKING_NOT_FOUND(404,  "No se encontró estacionamiento");
    private final int code;
    private final String message;
}
