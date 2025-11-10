package io.paymeter.assessment.infrastructure.drivenadapters.mysqladapter.adapters;

import io.paymeter.assessment.domain.model.Parking.Parking;
import io.paymeter.assessment.domain.model.common.enums.TechnicalExceptionEnum;
import io.paymeter.assessment.domain.model.common.exception.TechnicalException;
import io.paymeter.assessment.domain.model.pricing.gateway.PricingRepository;
import io.paymeter.assessment.infrastructure.drivenadapters.mysqladapter.entities.ParkingEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketAdapter implements PricingRepository {
    @Autowired
    private final ParkingEntityRepository parkingEntityRepository;

    @Override
    public Mono<Parking> findByParkingId(String parkingId) {
        return parkingEntityRepository.findByParkingId(parkingId).map(parkingEntity ->
                        Parking.builder()
                                .parkingId(parkingEntity.getParkingId())
                                .maxRatePeriodHours(parkingEntity.getMaxRatePeriodHours())
                                .freeInitialHours(parkingEntity.getFreeInitialHours())
                                .hourlyRate(parkingEntity.getHourlyRate())
                                .maxRateAmount(parkingEntity.getMaxRateAmount())
                                .build())
                .onErrorMap(Exception.class, exception -> {
                    log.error("Error al findByParkingId {}", exception.getMessage());
                    return new TechnicalException(exception, TechnicalExceptionEnum.TECHNICAL_INTERNAL_SERVER);
                });
    }
}
