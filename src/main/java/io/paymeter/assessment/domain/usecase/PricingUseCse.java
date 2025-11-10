package io.paymeter.assessment.domain.usecase;

import io.paymeter.assessment.domain.model.Parking.Parking;
import io.paymeter.assessment.domain.model.pricing.Money;
import io.paymeter.assessment.domain.model.pricing.Pricing;
import io.paymeter.assessment.domain.model.pricing.gateway.PricingRepository;
import io.paymeter.assessment.infrastructure.entrypoints.parking.dto.ParkingRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
@Log4j2
public class PricingUseCse {
    private final PricingRepository parkingRepository;

    public Mono<Pricing> calculatePricing(ParkingRequest parkingRequest) {
        return parkingRepository.findByParkingId(parkingRequest.getParkingId()).map(parking -> {

            LocalDateTime fromTime = LocalDateTime.parse(parkingRequest.getFrom());
            LocalDateTime toTime = parkingRequest.getTo() != null && !parkingRequest.getTo().isEmpty()
                    ? LocalDateTime.parse(parkingRequest.getTo())
                    : LocalDateTime.now();

            Duration duration = Duration.between(fromTime, toTime);
            long totalMinutes = duration.toMinutes();

            if (isFreeParking(totalMinutes)) {
                return buildResponse(parking, fromTime, toTime, totalMinutes, new Money(new BigDecimal(0)));
            }

            long billedHours = (long) Math.ceil(totalMinutes / 60.0);
            log.info("Total Horas:  {}", billedHours);

            BigDecimal finalPrice = calculatePriceLogic(parking, billedHours);
            return buildResponse(parking, fromTime, toTime, totalMinutes, new Money(finalPrice));


        });
    }

    private boolean isFreeParking(long minutes) {
        return minutes < 1;
    }

    private BigDecimal calculatePriceLogic(Parking config, long billedHours) {
        long chargeableHours = Math.max(0, billedHours - config.getFreeInitialHours());

        if (chargeableHours == 0) {
            return BigDecimal.ZERO;
        }

        int period = config.getMaxRatePeriodHours(); // 24h o 12h ETC

        long completePeriods = chargeableHours / period;
        log.info("Toral número de períodos completos: {}",completePeriods);

        long remainingHours = chargeableHours % period;

        BigDecimal periodCost = config.getMaxRateAmount();
        BigDecimal costOfCompletePeriods = periodCost.multiply(BigDecimal.valueOf(completePeriods));

        BigDecimal costOfRemainingTime = config.getHourlyRate()
                .multiply(BigDecimal.valueOf(remainingHours));

        if (remainingHours > 0) {
            costOfRemainingTime = costOfRemainingTime.min(periodCost);
        }

        return costOfCompletePeriods
                .add(costOfRemainingTime)
                .setScale(2, RoundingMode.HALF_UP);
    }


    private Pricing buildResponse(Parking parking, LocalDateTime from, LocalDateTime to, long minutes, Money money) {
        log.info("Precio total: {}, {}", money.getAmount(),money.getCurrencyCode());
        log.info("Formato precio: {}", "Cantidad entera + código de moneda (por ejemplo 2,35€ sería 235EUR");
        String priceInCents = money.getAmount().multiply(new BigDecimal("100"))
                .setScale(0, RoundingMode.UNNECESSARY).toString();
        return Pricing.builder()
                .price(priceInCents + money.getCurrencyCode())
                .duration(minutes)
                .from(from)
                .to(to)
                .parkingId(parking.getParkingId())
                .build();
    }

}
