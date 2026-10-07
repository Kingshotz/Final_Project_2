package utils;

import static constants.Constants.MONTHS_IN_YEAR;
import static constants.Constants.PERCENT;

public class LoanCalculator {
    public static double monthlyPayment(double amount, double annualRatePercent, int months) {
        double monthlyRate = annualRatePercent / PERCENT / MONTHS_IN_YEAR;
        return amount * monthlyRate / (1 - Math.pow(1 + monthlyRate, -months));
    }
}
