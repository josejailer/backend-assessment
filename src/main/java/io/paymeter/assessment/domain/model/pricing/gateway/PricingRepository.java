package io.paymeter.assessment.domain.model.pricing.gateway;

import io.paymeter.assessment.domain.model.Parking.Parking;
import reactor.core.publisher.Mono;

public interface PricingRepository {
    Mono<Parking> findByParkingId(String parkingId);
}
