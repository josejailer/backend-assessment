package io.paymeter.assessment.domain.model.common.exception;

import io.paymeter.assessment.domain.model.common.enums.BusinessExceptionEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BusinessException extends RuntimeException {

    private final BusinessExceptionEnum exception;
}