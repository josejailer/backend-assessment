package io.paymeter.assessment.infrastructure.drivenadapters.mysqladapter.entities;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ParkingEntityRepository extends ReactiveCrudRepository<ParkingEntity, Long> {
    Mono<ParkingEntity> findByParkingId(String parkingId);

}
