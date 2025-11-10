package io.paymeter.assessment.pricing;

import io.paymeter.assessment.domain.model.pricing.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

    @Test
    void testCurrencyIsAlwaysEuro() {
        var money = new Money(new BigDecimal(123));
        assertEquals("EUR", money.getCurrencyCode());
    }
}
