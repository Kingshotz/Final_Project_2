package steps.ui;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.TimeoutError;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import models.ConsumerLoan;
import models.ConsumerLoanValidation;
import models.LocaleData;
import org.testng.Assert;
import pages.ConsumerLoanPage;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static constants.Constants.ACTIVE_CLASS;
import static constants.Constants.CALCULATION_TIMEOUT;
import static constants.Constants.CONSUMER_LOAN_MAX_AMOUNT;
import static constants.Constants.CONSUMER_LOAN_MAX_MONTHS;
import static constants.Constants.CONSUMER_LOAN_MIN_AMOUNT;
import static constants.Constants.CONSUMER_LOAN_MIN_MONTHS;
import static constants.Constants.CONSUMER_LOAN_PATH;
import static constants.Constants.CONSUMER_LOAN_TITLE;
import static constants.Constants.ERRORED_CLASS;
import static constants.Constants.GEL_SYMBOL;
import static constants.Constants.MAX_ATTRIBUTE;
import static constants.Constants.MIN_ATTRIBUTE;
import static constants.Constants.PAYMENT_TOLERANCE;
import static constants.Constants.RATE_VALUE;
import static constants.Constants.SUMMARY_AMOUNT;
import static utils.LoanCalculator.monthlyPayment;
import static utils.UrlBuilder.pageUrl;
import static utils.UrlBuilder.pageUrlPattern;

public class ConsumerLoanSteps {
    public final ConsumerLoanPage consumerLoanPage;
    private final Page page;

    public ConsumerLoanSteps(Page page) {
        this.page = page;
        this.consumerLoanPage = new ConsumerLoanPage(page);
    }

    @Step("Open English Digital Consumer Loan page")
    public ConsumerLoanSteps openConsumerLoanPage() {
        page.navigate(pageUrl(LocaleData.EN, CONSUMER_LOAN_PATH));
        assertThat(page).hasURL(pageUrlPattern(LocaleData.EN, CONSUMER_LOAN_PATH));
        return this;
    }

    @Step("Check Consumer Loan page is opened with the 'By Amount' calculator")
    public ConsumerLoanSteps checkCalculatorOpened() {
        assertThat(consumerLoanPage.title).hasText(CONSUMER_LOAN_TITLE);
        assertThat(consumerLoanPage.byAmountTab).hasClass(ACTIVE_CLASS);
        assertThat(consumerLoanPage.amountInput).isVisible();
        assertThat(consumerLoanPage.periodInput).isVisible();
        assertThat(consumerLoanPage.amountInput).hasAttribute(MIN_ATTRIBUTE, String.valueOf(CONSUMER_LOAN_MIN_AMOUNT));
        assertThat(consumerLoanPage.amountInput).hasAttribute(MAX_ATTRIBUTE, String.valueOf(CONSUMER_LOAN_MAX_AMOUNT));
        assertThat(consumerLoanPage.periodInput).hasAttribute(MIN_ATTRIBUTE, String.valueOf(CONSUMER_LOAN_MIN_MONTHS));
        assertThat(consumerLoanPage.periodInput).hasAttribute(MAX_ATTRIBUTE, String.valueOf(CONSUMER_LOAN_MAX_MONTHS));
        assertThat(consumerLoanPage.summary.amount).hasText(SUMMARY_AMOUNT);
        assertThat(consumerLoanPage.summary.interestRate).containsText(RATE_VALUE);
        return this;
    }

    @Step("Read the loan from the calculator summary")
    public ConsumerLoan getLoan() {
        assertThat(consumerLoanPage.summary.interestRate).containsText(RATE_VALUE);
        assertThat(consumerLoanPage.summary.effectiveRate).containsText(RATE_VALUE);

        ConsumerLoan loan = ConsumerLoan.builder()
                .amount((long) consumerLoanPage.summary.readAmount())
                .months((int) consumerLoanPage.summary.readPeriod())
                .interestRate(consumerLoanPage.summary.readInterestRate())
                .effectiveRate(consumerLoanPage.summary.readEffectiveRate())
                .build();

        Assert.assertTrue(loan.getInterestRate() > 0, "Interest rate should be positive");
        Assert.assertTrue(loan.getEffectiveRate() >= loan.getInterestRate(),
                "Effective interest rate should not be lower than the nominal rate");

        Allure.addAttachment("Consumer loan parameters",
                "amount: " + loan.getAmount()
                        + "\nmonths: " + loan.getMonths()
                        + "\ninterest rate: " + loan.getInterestRate() + " %"
                        + "\neffective rate: " + loan.getEffectiveRate() + " %");
        return loan;
    }

    @Step("Fill loan amount {amount}")
    public ConsumerLoanSteps fillAmount(String amount) {
        consumerLoanPage.amountInput.fill(amount);
        consumerLoanPage.amountInput.blur();
        assertThat(consumerLoanPage.amountInput).hasValue(amount);
        return this;
    }

    @Step("Fill loan period {months} months")
    public ConsumerLoanSteps fillPeriod(String months) {
        consumerLoanPage.periodInput.fill(months);
        consumerLoanPage.periodInput.blur();
        assertThat(consumerLoanPage.periodInput).hasValue(months);
        return this;
    }

    @Step("Check validation result is {expected}")
    public ConsumerLoanSteps checkValidation(ConsumerLoanValidation expected) {
        if (expected.isAmountInvalid()) {
            assertThat(consumerLoanPage.amountBox).hasClass(ERRORED_CLASS);
        } else {
            assertThat(consumerLoanPage.amountBox).not().hasClass(ERRORED_CLASS);
        }
        if (expected.isPeriodInvalid()) {
            assertThat(consumerLoanPage.periodBox).hasClass(ERRORED_CLASS);
        } else {
            assertThat(consumerLoanPage.periodBox).not().hasClass(ERRORED_CLASS);
        }
        return this;
    }

    @Step("Check summary shows {amount} GEL for {months} months")
    public ConsumerLoanSteps checkSummary(String amount, String months) {
        assertThat(consumerLoanPage.summary.amount).hasText(amount + " " + GEL_SYMBOL);
        assertThat(consumerLoanPage.summary.period).hasText(months);
        return this;
    }

    public ConsumerLoanSteps checkSummary(ConsumerLoan loan) {
        return checkSummary(String.valueOf(loan.getAmount()), String.valueOf(loan.getMonths()));
    }

    @Step("Check monthly payment for {loan.amount} GEL, {loan.months} months")
    public ConsumerLoanSteps checkMonthlyPayment(ConsumerLoan loan) {
        double expected = monthlyPayment(loan.getAmount(), loan.getInterestRate(), loan.getMonths());
        double tolerance = PAYMENT_TOLERANCE;

        try {
            page.waitForCondition(() -> Math.abs(consumerLoanPage.summary.readMonthlyPayment() - expected) <= tolerance,
                    new Page.WaitForConditionOptions().setTimeout(CALCULATION_TIMEOUT));
        } catch (TimeoutError ignored) {
        }

        double actual = consumerLoanPage.summary.readMonthlyPayment();
        Allure.addAttachment("Monthly payment calculation",
                "expected: " + expected
                        + "\nactual: " + actual
                        + "\ntolerance: " + tolerance);

        assertThat(consumerLoanPage.summary.monthlyPayment).containsText(GEL_SYMBOL);
        Assert.assertEquals(actual, expected, tolerance,
                "Monthly payment for " + loan.getAmount() + " GEL over " + loan.getMonths() + " months is wrong");
        return this;
    }
}
