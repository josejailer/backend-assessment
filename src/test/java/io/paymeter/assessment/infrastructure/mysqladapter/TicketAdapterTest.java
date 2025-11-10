package io.paymeter.assessment.infrastructure.mysqladapter;

import io.paymeter.assessment.domain.model.Parking.Parking;
import io.paymeter.assessment.domain.model.common.enums.TechnicalExceptionEnum;
import io.paymeter.assessment.domain.model.common.exception.TechnicalException;
import io.paymeter.assessment.infrastructure.drivenadapters.mysqladapter.adapters.TicketAdapter;
import io.paymeter.assessment.infrastructure.drivenadapters.mysqladapter.entities.ParkingEntity;
import io.paymeter.assessment.infrastructure.drivenadapters.mysqladapter.entities.ParkingEntityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TicketAdapterTest {

    @Mock
    private ParkingEntityRepository parkingEntityRepository;

    @InjectMocks
    private TicketAdapter ticketAdapter;

    private static final String TEST_ID = "PARKING001";
    private ParkingEntity mockEntity;

    @Test
    void findByParkingIdSuccess() {
        mockEntity = mock(ParkingEntity.class);
        when(mockEntity.getParkingId()).thenReturn(TEST_ID);
        when(mockEntity.getHourlyRate()).thenReturn(new BigDecimal("2.50"));
        when(mockEntity.getMaxRateAmount()).thenReturn(new BigDecimal("25.00"));
        when(mockEntity.getMaxRatePeriodHours()).thenReturn(12);
        when(mockEntity.getFreeInitialHours()).thenReturn(1);
        when(parkingEntityRepository.findByParkingId(TEST_ID)).thenReturn(Mono.just(mockEntity));
        Mono<Parking> resultMono = ticketAdapter.findByParkingId(TEST_ID);
        StepVerifier.create(resultMono)
                .assertNext(parking -> {
                    assertEquals(TEST_ID, parking.getParkingId());
                    assertEquals(new BigDecimal("2.50"), parking.getHourlyRate());
                    assertEquals(12, parking.getMaxRatePeriodHours());
                    assertEquals(1, parking.getFreeInitialHours());
                    assertEquals(new BigDecimal("25.00"), parking.getMaxRateAmount());
                })
                .verifyComplete();
    }

    @Test
    void findByParkingIdNotFound() {
        when(parkingEntityRepository.findByParkingId(TEST_ID)).thenReturn(Mono.empty());
        Mono<Parking> resultMono = ticketAdapter.findByParkingId(TEST_ID);
        StepVerifier.create(resultMono)
                .verifyComplete();
    }

    @Test
    void findByParkingIdError() {
        RuntimeException dbException = new RuntimeException("Database connection failed");
        when(parkingEntityRepository.findByParkingId(TEST_ID)).thenReturn(Mono.error(dbException));
        Mono<Parking> resultMono = ticketAdapter.findByParkingId(TEST_ID);
        StepVerifier.create(resultMono)
                .verifyErrorMatches(throwable ->
                        throwable instanceof TechnicalException &&
                                ((TechnicalException) throwable).getTechnicalExceptionEnum()
                                        == TechnicalExceptionEnum.TECHNICAL_INTERNAL_SERVER
                );
    }
}
