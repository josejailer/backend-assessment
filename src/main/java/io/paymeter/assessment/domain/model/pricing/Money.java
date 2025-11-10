package io.paymeter.assessment.domain.model.pricing;


import lombok.Data;

import java.math.BigDecimal;
import java.util.Currency;
@Data
public class Money {
    private static final Currency DEFAULT_CURRENCY = Currency.getInstance("EUR");

    private final BigDecimal amount;
    private final Currency currency;

    public Money(BigDecimal amount) {
        this.amount = amount;
        this.currency = DEFAULT_CURRENCY;
    }

    public String getCurrencyCode() {
        return currency.getCurrencyCode();
    }
}
