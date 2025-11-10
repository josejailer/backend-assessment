package io.paymeter.assessment.infrastructure.entrypoints.parking;


import io.paymeter.assessment.domain.model.common.enums.BusinessExceptionEnum;
import io.paymeter.assessment.domain.model.common.enums.TechnicalExceptionEnum;
import io.paymeter.assessment.domain.model.common.exception.TechnicalException;
import io.paymeter.assessment.domain.model.pricing.Pricing;
import io.paymeter.assessment.domain.usecase.PricingUseCse;
import io.paymeter.assessment.infrastructure.entrypoints.parking.dto.ParkingRequest;
import io.paymeter.assessment.infrastructure.entrypoints.parking.dto.ResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketControllerTest {
    private WebTestClient webTestClient;
    @Mock
    private PricingUseCse parkingUseCse;

    private ParkingRequest mockRequest;

    @BeforeEach
    void setUp() {
        TicketController ticketController = new TicketController(parkingUseCse);
        this.webTestClient = WebTestClient.bindToController(ticketController).build();

        mockRequest = mock(ParkingRequest.class);
        when(mockRequest.getParkingId()).thenReturn("P000456");
        when(mockRequest.getFrom()).thenReturn("2025-08-21T07:49:50.123");

    }

    @Test
    void calculateShouldSuccess() {
        Pricing mockPricing = Pricing.builder()
                .price("1500EUR")
                .parkingId("P000456")
                .build();

        when(parkingUseCse.calculatePricing(any(ParkingRequest.class)))
                .thenReturn(Mono.just(mockPricing));

        webTestClient.post().uri("/tickets/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(mockRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ResponseDTO.class)
                .consumeWith(response -> {
                    ResponseDTO responseDTO = response.getResponseBody();
                    assert responseDTO != null;
                    assert responseDTO.getData() != null;
                });
    }

    @Test
    void calculateParkingNotFound() {
        when(parkingUseCse.calculatePricing(any(ParkingRequest.class)))
                .thenReturn(Mono.empty());

        webTestClient.post().uri("/tickets/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(mockRequest)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ResponseDTO.class)
                .consumeWith(response -> {

                    ResponseDTO responseDTO = response.getResponseBody();
                    assert responseDTO != null;
                    assert responseDTO.getErrors() != null;
                    assert response.getStatus().value()==BusinessExceptionEnum.PARKING_NOT_FOUND.getCode();
                });
    }

    @Test
    void calculateTechnicalException() {
        TechnicalException technicalException = new TechnicalException(TechnicalExceptionEnum.TECHNICAL_INTERNAL_SERVER);
        when(parkingUseCse.calculatePricing(any(ParkingRequest.class)))
                .thenReturn(Mono.error(technicalException));

        webTestClient.post().uri("/tickets/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(mockRequest)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(ResponseDTO.class)
                .consumeWith(response -> {
                    ResponseDTO responseDTO = response.getResponseBody();
                    assert responseDTO != null;
                    assert responseDTO.getErrors() != null;
                    assert response.getStatus().value()==TechnicalExceptionEnum.TECHNICAL_INTERNAL_SERVER.getCode();
                });
    }

}