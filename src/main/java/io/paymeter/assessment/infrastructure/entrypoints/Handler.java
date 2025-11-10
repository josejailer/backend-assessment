package io.paymeter.assessment.infrastructure.entrypoints;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.paymeter.assessment.domain.model.common.enums.BusinessExceptionEnum;
import io.paymeter.assessment.infrastructure.entrypoints.parking.dto.ErrorResponse;
import io.paymeter.assessment.infrastructure.entrypoints.parking.dto.ResponseDTO;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;


@Component
@Log4j2
@RequiredArgsConstructor

@ControllerAdvice
public class Handler {
    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ResponseDTO> handleException(WebExchangeBindException e, ServerHttpRequest request) {
        var errorResponse= ErrorResponse.builder().build();
        e.getBindingResult()
                .getAllErrors().forEach(error -> {
                    errorResponse.setMessage(error.getDefaultMessage());
                    errorResponse.setCode(HttpStatus.BAD_REQUEST.value());
                    log.info("Ocurrio un error de negocio WebExchangeBindException error {}",error.getDefaultMessage());
                });
        return ResponseEntity.badRequest().body(ResponseDTO.buildError(errorResponse,request));
    }

   @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseDTO> handleException(ConstraintViolationException e, ServerHttpRequest request) {
       var errorResponse= ErrorResponse.builder().build();
        e.getConstraintViolations().forEach(error -> {
            errorResponse.setMessage(error.getMessage());
            errorResponse.setCode(HttpStatus.BAD_REQUEST.value());
            log.info("Ocurrio un error de negocio ConstraintViolationException error {}",error.getMessage());
        });
        return ResponseEntity.badRequest().body(ResponseDTO.buildError(errorResponse,request));
    }
   @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseDTO> handleException(IllegalArgumentException e,ServerHttpRequest request) {
       var errorResponse= ErrorResponse.builder().build();
       errorResponse.setMessage(e.getMessage());
       errorResponse.setCode(HttpStatus.BAD_REQUEST.value());
       log.info("Ocurrio un error de negocio IllegalArgumentException error {}",e.getMessage());
        return ResponseEntity.badRequest().body(ResponseDTO.buildError(errorResponse,request));
    }
}
