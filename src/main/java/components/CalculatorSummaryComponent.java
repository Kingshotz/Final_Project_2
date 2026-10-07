package components;

import com.microsoft.playwright.Locator;

import static utils.TextParser.parseNumber;

public class CalculatorSummaryComponent {
    public final Locator root;
    public final Locator monthlyPayment;
    public final Locator values;
    public final Locator amount;
    public final Locator period;
    public final Locator interestRate;
    public final Locator effectiveRate;

    public CalculatorSummaryComponent(Locator calculator) {
        root = calculator;
        monthlyPayment = root.locator(".tbcx-pw-calculated-info__number--new");
        values = root.locator(".tbcx-pw-calculated-info__rows-item-info");
        amount = values.nth(0);
        period = values.nth(1);
        interestRate = values.nth(2);
        effectiveRate = values.nth(3);
    }

    public double readMonthlyPayment() {
        return parseNumber(monthlyPayment.textContent());
    }

    public double readAmount() {
        return parseNumber(amount.innerText());
    }

    public double readPeriod() {
        return parseNumber(period.innerText());
    }

    public double readInterestRate() {
        return parseNumber(interestRate.innerText());
    }

    public double readEffectiveRate() {
        return parseNumber(effectiveRate.innerText());
    }
}
