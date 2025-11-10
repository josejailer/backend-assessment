package io.paymeter.assessment.domaint.usecase;

import io.paymeter.assessment.domain.model.Parking.Parking;
import io.paymeter.assessment.domain.model.pricing.Pricing;
import io.paymeter.assessment.domain.model.pricing.gateway.PricingRepository;
import io.paymeter.assessment.domain.usecase.PricingUseCse;
import io.paymeter.assessment.infrastructure.entrypoints.parking.dto.ParkingRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PricingUseCseTest {
    @InjectMocks
    private PricingUseCse pricingUseCse;

    @Mock
    private PricingRepository pricingRepository;

    private static final String P000123 = "P000123";
    private static final String P000456 = "P000456";
    private static final String UNKNOWN_ID = "P999999";

    private Parking config_24h_max;
    private Parking config_12h_max_plus_free;

    @BeforeEach
    void setup() {
        config_24h_max = mock(Parking.class);
        config_12h_max_plus_free = mock(Parking.class);
    }

    private void setupConfig24hMax() {
        when(config_24h_max.getParkingId()).thenReturn(P000123);
        when(config_24h_max.getHourlyRate()).thenReturn(new BigDecimal("2.00"));
        when(config_24h_max.getMaxRateAmount()).thenReturn(new BigDecimal("15.00"));
        when(config_24h_max.getMaxRatePeriodHours()).thenReturn(24);
        when(config_24h_max.getFreeInitialHours()).thenReturn(0);
    }

    private void setupConfig12hMaxPlusFree() {
        when(config_12h_max_plus_free.getParkingId()).thenReturn(P000456);
        when(config_12h_max_plus_free.getHourlyRate()).thenReturn(new BigDecimal("3.00"));
        when(config_12h_max_plus_free.getMaxRateAmount()).thenReturn(new BigDecimal("20.00"));
        when(config_12h_max_plus_free.getMaxRatePeriodHours()).thenReturn(12);
        when(config_12h_max_plus_free.getFreeInitialHours()).thenReturn(1);
    }


    @Test
    void freeParkingLessThanOneMinute() {
        LocalDateTime from = LocalDateTime.now().minusSeconds(59);
        LocalDateTime to = LocalDateTime.now();
        ParkingRequest request = mock(ParkingRequest.class);
        when(request.getParkingId()).thenReturn(P000123);
        when(request.getFrom()).thenReturn(from.toString());
        when(request.getTo()).thenReturn(to.toString());

        when(pricingRepository.findByParkingId(P000123)).thenReturn(Mono.just(config_24h_max));

        Mono<Pricing> result = pricingUseCse.calculatePricing(request);

        StepVerifier.create(result)
                .assertNext(pricing -> {
                    assertEquals("0EUR", pricing.getPrice());
                    assertTrue(pricing.getDuration() < 1);
                })
                .verifyComplete();
    }

    @Test
    void parkingIdNotFound() {
        ParkingRequest request = mock(ParkingRequest.class);
        when(request.getParkingId()).thenReturn(UNKNOWN_ID);

        when(pricingRepository.findByParkingId(UNKNOWN_ID)).thenReturn(Mono.empty());
        Mono<Pricing> result = pricingUseCse.calculatePricing(request);
        StepVerifier.create(result).verifyComplete();
    }

    @Test
    void simpleHourlyRateNoFreeHoursLessThanMax() {
        setupConfig24hMax();

        LocalDateTime from = LocalDateTime.now().minusHours(3);
        LocalDateTime to = LocalDateTime.now();
        ParkingRequest request = mock(ParkingRequest.class);
        when(request.getParkingId()).thenReturn(P000123);
        when(request.getFrom()).thenReturn(from.toString());
        when(request.getTo()).thenReturn(to.toString());

        when(pricingRepository.findByParkingId(P000123)).thenReturn(Mono.just(config_24h_max));

        StepVerifier.create(pricingUseCse.calculatePricing(request))
                .assertNext(pricing -> assertEquals("600EUR", pricing.getPrice()))
                .verifyComplete();
    }

    @Test
    void freeInitialHoursChargeableHoursIsZero() {
        when(config_12h_max_plus_free.getParkingId()).thenReturn(P000456);
        when(config_12h_max_plus_free.getFreeInitialHours()).thenReturn(1);

        LocalDateTime from = LocalDateTime.now().minusHours(1);
        LocalDateTime to = LocalDateTime.now();
        ParkingRequest request = mock(ParkingRequest.class);
        when(request.getParkingId()).thenReturn(P000456);
        when(request.getFrom()).thenReturn(from.toString());
        when(request.getTo()).thenReturn(to.toString());

        when(pricingRepository.findByParkingId(P000456)).thenReturn(Mono.just(config_12h_max_plus_free));

        StepVerifier.create(pricingUseCse.calculatePricing(request))
                .assertNext(pricing -> assertEquals("0EUR", pricing.getPrice()))
                .verifyComplete();
    }

    @Test
    void freeInitialHoursPartiallyCharged() {
        setupConfig12hMaxPlusFree();
        LocalDateTime from = LocalDateTime.now().minusHours(3).minusMinutes(1);
        LocalDateTime to = LocalDateTime.now();

        ParkingRequest request = mock(ParkingRequest.class);
        when(request.getParkingId()).thenReturn(P000456);
        when(request.getFrom()).thenReturn(from.toString());
        when(request.getTo()).thenReturn(to.toString());

        when(pricingRepository.findByParkingId(P000456)).thenReturn(Mono.just(config_12h_max_plus_free));

        StepVerifier.create(pricingUseCse.calculatePricing(request))
                .assertNext(pricing -> assertEquals("900EUR", pricing.getPrice()))
                .verifyComplete();
    }



    @Test
    void maxRateReachedPartialPeriodCap() {
        setupConfig12hMaxPlusFree();
        LocalDateTime from = LocalDateTime.now().minusHours(8).minusMinutes(1);
        LocalDateTime to = LocalDateTime.now();

        ParkingRequest request = mock(ParkingRequest.class);
        when(request.getParkingId()).thenReturn(P000456);
        when(request.getFrom()).thenReturn(from.toString());
        when(request.getTo()).thenReturn(to.toString());

        when(pricingRepository.findByParkingId(P000456)).thenReturn(Mono.just(config_12h_max_plus_free));

        StepVerifier.create(pricingUseCse.calculatePricing(request))
                .assertNext(pricing -> assertEquals("2000EUR", pricing.getPrice()))
                .verifyComplete();
    }

    @Test
    void maxRateReachedOneCompletePeriod() {
        setupConfig24hMax();

        LocalDateTime from = LocalDateTime.now().minusDays(1);
        LocalDateTime to = LocalDateTime.now();
        ParkingRequest request = mock(ParkingRequest.class);
        when(request.getParkingId()).thenReturn(P000123);
        when(request.getFrom()).thenReturn(from.toString());
        when(request.getTo()).thenReturn(to.toString());

        when(pricingRepository.findByParkingId(P000123)).thenReturn(Mono.just(config_24h_max));

        StepVerifier.create(pricingUseCse.calculatePricing(request))
                .assertNext(pricing -> assertEquals("1500EUR", pricing.getPrice()))
                .verifyComplete();
    }

    @Test
    void maxRateReachedMultiplePeriods() {
        setupConfig24hMax();
        LocalDateTime from = LocalDateTime.now().minusHours(28);
        LocalDateTime to = LocalDateTime.now();
        ParkingRequest request = mock(ParkingRequest.class);
        when(request.getParkingId()).thenReturn(P000123);
        when(request.getFrom()).thenReturn(from.toString());
        when(request.getTo()).thenReturn(to.toString());

        when(pricingRepository.findByParkingId(P000123)).thenReturn(Mono.just(config_24h_max));

        StepVerifier.create(pricingUseCse.calculatePricing(request))
                .assertNext(pricing -> assertEquals("2300EUR", pricing.getPrice()))
                .verifyComplete();
    }

    @Test
    void maxRateReachedMultiplePeriodsLargeDuration() {
        setupConfig24hMax();
        LocalDateTime from = LocalDateTime.now().minusHours(50);
        LocalDateTime to = LocalDateTime.now();
        ParkingRequest request = mock(ParkingRequest.class);
        when(request.getParkingId()).thenReturn(P000123);
        when(request.getFrom()).thenReturn(from.toString());
        when(request.getTo()).thenReturn(to.toString());

        when(pricingRepository.findByParkingId(P000123)).thenReturn(Mono.just(config_24h_max));

        StepVerifier.create(pricingUseCse.calculatePricing(request))
                .assertNext(pricing -> assertEquals("3400EUR", pricing.getPrice()))
                .verifyComplete();
    }

    @Test
    void defaultToTimeWhenToIsNull() {
        setupConfig24hMax();
        LocalDateTime twoHoursAgo = LocalDateTime.now().minusHours(2).truncatedTo(ChronoUnit.MINUTES);
        ParkingRequest request = mock(ParkingRequest.class);
        when(request.getParkingId()).thenReturn(P000123);
        when(request.getFrom()).thenReturn(twoHoursAgo.toString());
        when(request.getTo()).thenReturn(null);
        when(pricingRepository.findByParkingId(P000123)).thenReturn(Mono.just(config_24h_max));

        StepVerifier.create(pricingUseCse.calculatePricing(request))
                .assertNext(pricing -> {
                    assertEquals("400EUR", pricing.getPrice());
                    assertTrue(pricing.getDuration() >= 120);
                })
                .verifyComplete();
    }
}
