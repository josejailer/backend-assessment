package io.paymeter.assessment.domain.model.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TechnicalExceptionEnum {

    TECHNICAL_INTERNAL_SERVER( 500,"Ha ocurrido un error del servidor.");
    private final int code;
    private final String message;
}
