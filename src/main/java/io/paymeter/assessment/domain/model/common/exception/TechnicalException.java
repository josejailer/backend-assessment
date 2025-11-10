package io.paymeter.assessment.domain.model.common.exception;

import io.paymeter.assessment.domain.model.common.enums.TechnicalExceptionEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TechnicalException extends RuntimeException {

    private final TechnicalExceptionEnum technicalExceptionEnum;

    public TechnicalException(Throwable cause, TechnicalExceptionEnum technicalException) {
        super(cause);
        this.technicalExceptionEnum = technicalException;
    }
}

