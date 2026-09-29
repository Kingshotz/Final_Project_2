package models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static constants.Constants.EUR_MAX_PERIOD;
import static constants.Constants.EUR_SYMBOL;
import static constants.Constants.GEL_MAX_PERIOD;
import static constants.Constants.GEL_SYMBOL;
import static constants.Constants.USD_MAX_PERIOD;
import static constants.Constants.USD_SYMBOL;

@Getter
@AllArgsConstructor
public enum LoanCurrency {
    GEL(GEL_SYMBOL, GEL_MAX_PERIOD),
    USD(USD_SYMBOL, USD_MAX_PERIOD),
    EUR(EUR_SYMBOL, EUR_MAX_PERIOD);

    private final String symbol;
    private final int maxPeriod;

    public static int commonMaxPeriod() {
        int max = Integer.MAX_VALUE;
        for (LoanCurrency currency : values()) {
            max = Math.min(max, currency.maxPeriod);
        }
        return max;
    }
}
