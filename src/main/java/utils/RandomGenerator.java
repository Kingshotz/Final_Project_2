package utils;

import models.LoanCurrency;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

import static constants.Constants.EXCHANGE_AMOUNT_DECIMALS;
import static constants.Constants.EXCHANGE_MAX_AMOUNT;
import static constants.Constants.EXCHANGE_MIN_AMOUNT;
import static constants.Constants.MORTGAGE_MAX_AMOUNT;
import static constants.Constants.MORTGAGE_MIN_AMOUNT;
import static constants.Constants.MORTGAGE_MIN_PERIOD;

public class RandomGenerator {
    public static final Random rand;
    static {
        rand = new Random();
    }

    public static String randomExchangeAmount() {
        return BigDecimal.valueOf(EXCHANGE_MIN_AMOUNT + rand.nextDouble(EXCHANGE_MAX_AMOUNT - EXCHANGE_MIN_AMOUNT))
                .setScale(EXCHANGE_AMOUNT_DECIMALS, RoundingMode.HALF_UP)
                .stripTrailingZeros()
                .toPlainString();
    }

    public static String randomMortgageAmount() {
        return String.valueOf(MORTGAGE_MIN_AMOUNT + rand.nextInt(MORTGAGE_MAX_AMOUNT - MORTGAGE_MIN_AMOUNT + 1));
    }

    public static String randomMortgagePeriod() {
        return String.valueOf(MORTGAGE_MIN_PERIOD + rand.nextInt(LoanCurrency.commonMaxPeriod() - MORTGAGE_MIN_PERIOD + 1));
    }
}
