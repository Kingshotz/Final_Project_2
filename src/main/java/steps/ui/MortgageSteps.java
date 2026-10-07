package steps.ui;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.TimeoutError;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import models.LoanCurrency;
import models.LocaleData;
import models.MortgageLoan;
import org.testng.Assert;
import pages.MortgagePage;

import static constants.Constants.ACTIVE_CLASS;
import static constants.Constants.ACTIVE_CURRENCY_CLASS;
import static constants.Constants.CALCULATION_TIMEOUT;
import static constants.Constants.ERRORED_CLASS;
import static constants.Constants.MONTHS_IN_YEAR;
import static constants.Constants.MORTGAGE_PATH;
import static constants.Constants.MORTGAGE_URL;
import static constants.Constants.PAYMENT_TOLERANCE;
import static constants.Constants.PERCENT_VALUE;
import static constants.Constants.PERIOD_MAX_ATTRIBUTE;
import static constants.Constants.SUMMARY_AMOUNT;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static utils.LoanCalculator.monthlyPayment;
import static utils.TextParser.clean;
import static utils.UrlBuilder.pageUrl;
import static utils.UrlBuilder.pageUrlPattern;

public class MortgageSteps {
    public final MortgagePage mortgagePage;
    private final Page page;

    public MortgageSteps(Page page) {
        this.page = page;
        this.mortgagePage = new MortgagePage(page);
    }

    @Step("Open main menu")
    public MortgageSteps openMainMenu() {
        mortgagePage.header.openMenu();
        assertThat(mortgagePage.mortgageLink).isVisible();
        return this;
    }

    @Step("Go to Mortgage page from the main menu")
    public MortgageSteps gotoMortgagePage() {
        mortgagePage.mortgageLink.click();
        assertThat(page).hasURL(MORTGAGE_URL);
        return this;
    }

    @Step("Open {locale} Mortgage page")
    public MortgageSteps openMortgagePage(LocaleData locale) {
        page.navigate(pageUrl(locale, MORTGAGE_PATH));
        assertThat(page).hasURL(pageUrlPattern(locale, MORTGAGE_PATH));
        return this;
    }

    @Step("Switch language from {from}")
    public MortgageSteps switchLanguage(LocaleData from) {
        LocaleData to = from.other();
        assertThat(mortgagePage.header.languageLabel).hasText(from.getLanguageSwitchLabel());
        mortgagePage.header.switchLanguage();
        assertThat(page).hasURL(pageUrlPattern(to, MORTGAGE_PATH));
        assertThat(mortgagePage.header.languageLabel).hasText(to.getLanguageSwitchLabel());
        return this;
    }

    public MortgageSteps checkCalculatorOpened() {
        return checkCalculatorOpened(LocaleData.EN);
    }

    @Step("Check Mortgage page is opened with 'By Amount' calculator ({locale})")
    public MortgageSteps checkCalculatorOpened(LocaleData locale) {
        assertThat(mortgagePage.title).hasText(locale.getMortgageTitle());
        assertThat(mortgagePage.calculatorTab).hasText(locale.getCalculatorTab());
        assertThat(mortgagePage.calculatorTab).hasClass(ACTIVE_CLASS);
        assertThat(mortgagePage.byAmountTab).hasText(locale.getByAmountTab());
        assertThat(mortgagePage.byAmountTab).hasClass(ACTIVE_CLASS);
        assertThat(mortgagePage.amountInput).isVisible();
        assertThat(mortgagePage.periodInput).isVisible();
        assertThat(mortgagePage.summary.amount).hasText(SUMMARY_AMOUNT);
        assertThat(mortgagePage.summary.interestRate).hasText(PERCENT_VALUE);
        return this;
    }

    @Step("Choose loan currency {currency}")
    public MortgageSteps chooseCurrency(LoanCurrency currency) {
        mortgagePage.currencyButton(currency.name()).click();
        assertThat(mortgagePage.currencyButton(currency.name())).hasClass(ACTIVE_CURRENCY_CLASS);
        assertThat(mortgagePage.amountCurrencySymbol).hasText(currency.getSymbol());
        assertThat(mortgagePage.periodInput).hasAttribute(PERIOD_MAX_ATTRIBUTE, String.valueOf(currency.getMaxPeriod()));
        return this;
    }

    @Step("Fill loan amount {amount}")
    public MortgageSteps fillAmount(String amount) {
        mortgagePage.amountInput.fill(amount);
        mortgagePage.amountInput.blur();
        assertThat(mortgagePage.amountInput).hasValue(amount);
        return this;
    }

    @Step("Fill loan period {years} years")
    public MortgageSteps fillPeriod(String years) {
        mortgagePage.periodInput.fill(years);
        mortgagePage.periodInput.blur();
        assertThat(mortgagePage.periodInput).hasValue(years);
        return this;
    }

    @Step("Check summary shows {amount} {currency} for {years} years")
    public MortgageSteps checkSummary(String amount, String years, LoanCurrency currency) {
        assertThat(mortgagePage.summary.amount).hasText(amount + " " + currency.getSymbol());
        assertThat(mortgagePage.summary.period).hasText(years);
        return this;
    }

    public MortgageSteps checkSummary(MortgageLoan loan) {
        return checkSummary(String.valueOf(loan.getAmount()), String.valueOf(loan.getYears()), loan.getCurrency());
    }

    @Step("Read mortgage parameters from the calculator summary")
    public MortgageLoan getMortgageLoan() {
        assertThat(mortgagePage.summary.interestRate).hasText(PERCENT_VALUE);
        assertThat(mortgagePage.summary.effectiveRate).hasText(PERCENT_VALUE);

        MortgageLoan loan = MortgageLoan.builder()
                .currency(LoanCurrency.valueOf(clean(mortgagePage.activeCurrency.innerText())))
                .amount((long) mortgagePage.summary.readAmount())
                .years((int) mortgagePage.summary.readPeriod())
                .interestRate(mortgagePage.summary.readInterestRate())
                .effectiveRate(mortgagePage.summary.readEffectiveRate())
                .build();

        Assert.assertTrue(loan.getInterestRate() > 0, "Interest rate should be positive");
        Assert.assertTrue(loan.getEffectiveRate() >= loan.getInterestRate(),
                "Effective interest rate should not be lower than the nominal rate");

        Allure.addAttachment("Mortgage parameters",
                "currency: " + loan.getCurrency()
                        + "\namount: " + loan.getAmount()
                        + "\nyears: " + loan.getYears()
                        + "\ninterest rate: " + loan.getInterestRate() + " %"
                        + "\neffective rate: " + loan.getEffectiveRate() + " %");
        return loan;
    }

    @Step("Check monthly payment for {loan.amount} {loan.currency}, {loan.years} years")
    public MortgageSteps checkMonthlyPayment(MortgageLoan loan) {
        double expected = calculateMonthlyPayment(loan);
        double tolerance = PAYMENT_TOLERANCE;

        try {
            page.waitForCondition(() -> Math.abs(mortgagePage.summary.readMonthlyPayment() - expected) <= tolerance,
                    new Page.WaitForConditionOptions().setTimeout(CALCULATION_TIMEOUT));
        } catch (TimeoutError ignored) {
        }

        double actual = mortgagePage.summary.readMonthlyPayment();
        Allure.addAttachment("Monthly payment calculation",
                "expected: " + expected
                        + "\nactual: " + actual
                        + "\ntolerance: " + tolerance);

        assertThat(mortgagePage.summary.monthlyPayment).containsText(loan.getCurrency().getSymbol());
        Assert.assertEquals(actual, expected, tolerance,
                "Monthly payment for " + loan.getAmount() + " " + loan.getCurrency()
                        + " over " + loan.getYears() + " years is wrong");
        return this;
    }

    @Step("Check amount error '{message}'")
    public MortgageSteps checkAmountError(String message) {
        assertThat(mortgagePage.amountError).isVisible();
        assertThat(mortgagePage.amountError).hasText(message);
        return this;
    }

    @Step("Check period is marked as invalid")
    public MortgageSteps checkPeriodError() {
        assertThat(mortgagePage.periodField).hasClass(ERRORED_CLASS);
        return this;
    }

    @Step("Check period is valid")
    public MortgageSteps checkNoPeriodError() {
        assertThat(mortgagePage.periodField).not().hasClass(ERRORED_CLASS);
        return this;
    }

    @Step("Check there is no amount error")
    public MortgageSteps checkNoAmountError() {
        assertThat(mortgagePage.amountError).isHidden();
        return this;
    }

    private static double calculateMonthlyPayment(MortgageLoan loan) {
        return monthlyPayment(loan.getAmount(), loan.getInterestRate(), loan.getYears() * MONTHS_IN_YEAR);
    }
}
