package io.paymeter.assessment.infrastructure.entrypoints.parking;

import io.paymeter.assessment.domain.model.common.enums.BusinessExceptionEnum;
import io.paymeter.assessment.domain.model.common.exception.TechnicalException;
import io.paymeter.assessment.domain.usecase.PricingUseCse;
import io.paymeter.assessment.infrastructure.entrypoints.parking.dto.ErrorResponse;
import io.paymeter.assessment.infrastructure.entrypoints.parking.dto.ParkingRequest;
import io.paymeter.assessment.infrastructure.entrypoints.parking.dto.ResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import org.springframework.http.server.reactive.ServerHttpRequest;

@RestController
@RequestMapping("tickets")
@RequiredArgsConstructor
@Validated
public class TicketController {

    private final PricingUseCse parkingUseCse;
	@PostMapping("/calculate")
    public Mono<ResponseEntity<ResponseDTO>> calculate(@Valid @RequestBody ParkingRequest parkingRequest, ServerHttpRequest serverHttpRequest){
        var errorResponse= ErrorResponse.builder().build();
    return parkingUseCse.calculatePricing(parkingRequest)
            .map(parking ->
            ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(parking,serverHttpRequest)))
            .switchIfEmpty(Mono.defer(() -> {
                errorResponse.setMessage(BusinessExceptionEnum.PARKING_NOT_FOUND.getMessage());
                errorResponse.setCode(BusinessExceptionEnum.PARKING_NOT_FOUND.getCode());
                return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ResponseDTO.buildError(errorResponse, serverHttpRequest)));
            })).onErrorResume(TechnicalException.class, e -> {
                errorResponse.setMessage(e.getTechnicalExceptionEnum().getMessage());
                errorResponse.setCode(e.getTechnicalExceptionEnum().getCode());
                return Mono.just(ResponseEntity.internalServerError()
                        .body(ResponseDTO.buildError(errorResponse, serverHttpRequest)));
            });
    }

}
