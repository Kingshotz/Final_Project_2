package tests.ui;

import database.DatabaseManager;
import database.models.ConsumerLoanCase;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.qameta.allure.TmsLink;
import models.ConsumerLoan;
import models.ConsumerLoanValidation;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import steps.ui.ConsumerLoanSteps;

import java.util.List;


@TmsLink("SCRUM-T19")
@Epic("TBC Bank Digital Services")
@Feature("Consumer Loan Calculator")
public class ConsumerLoanDataDrivenScenario extends BaseTest {
    private final ConsumerLoanCase loanCase;
    ConsumerLoanSteps consumerLoanSteps;
    ConsumerLoan lastValidLoan;

    @Factory(dataProvider = "consumerLoanCases")
    public ConsumerLoanDataDrivenScenario(ConsumerLoanCase loanCase) {
        this.loanCase = loanCase;
    }

    @DataProvider(name = "consumerLoanCases")
    public static Object[][] consumerLoanCases() {
        List<ConsumerLoanCase> cases = DatabaseManager.getActiveConsumerLoanCases();
        Assert.assertFalse(cases.isEmpty(), "consumer_loan_cases table has no active rows");
        return cases.stream()
                .map(loanCase -> new Object[]{loanCase})
                .toArray(Object[][]::new);
    }

    @BeforeClass
    public void innerSetup() {
        consumerLoanSteps = new ConsumerLoanSteps(page);
    }

    @BeforeMethod
    public void addCaseToReport() {
        Allure.parameter("Consumer loan case", loanCase.toString());
    }

    @Test(priority = 1, description = "Open consumer loan calculator")
    @Story("Open Consumer Loan Calculator for Database Case")
    @Severity(SeverityLevel.NORMAL)
    @Description("Opens the English Digital Consumer Loan page and validates the calculator is shown in By Amount mode "
            + "with amount, period and a loan summary.")
    public void openConsumerLoanCalculator() {
        consumerLoanSteps.openConsumerLoanPage();
        consumerLoanSteps.checkCalculatorOpened();
    }

    @Test(priority = 2, description = "Default loan is calculated correctly")
    @Story("Validate Default Consumer Loan Payment")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Reads the default loan from the summary and validates its monthly payment against the annuity "
            + "formula; this is the last valid calculation invalid database cases fall back to.")
    public void checkDefaultLoan() {
        lastValidLoan = consumerLoanSteps.getLoan();
        consumerLoanSteps.checkMonthlyPayment(lastValidLoan);
    }

    @Test(priority = 3, description = "Enter loan amount and period from the database case")
    @Story("Enter Database Case Loan Details")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Enters the amount and number of months of the consumer loan case read from the database through the "
            + "MyBatis mapper.")
    public void enterLoanDetails() {
        consumerLoanSteps
                .fillAmount(String.valueOf(loanCase.getAmount()))
                .fillPeriod(String.valueOf(loanCase.getMonths()));
    }

    @Test(priority = 4, description = "Validation result matches the database case")
    @Story("Validate Consumer Loan Limits for Database Case")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Validates the calculator shows the result expected by the database case: no errors for a valid loan, "
            + "or only the amount or only the period field marked invalid when it is outside 200 - 80000 GEL or "
            + "3 - 48 months.")
    public void checkValidationResult() {
        consumerLoanSteps.checkValidation(loanCase.getExpectedResult());
    }

    @Test(priority = 5, description = "Summary and monthly payment match the database case")
    @Story("Validate Consumer Loan Payment for Database Case")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Validates the summary shows the case's values for every valid field and keeps the default value for "
            + "an invalid field, and validates the monthly payment matches the annuity formula for the shown loan.")
    public void checkSummaryAndPayment() {
        ConsumerLoanValidation result = loanCase.getExpectedResult();
        ConsumerLoan expected = ConsumerLoan.builder()
                .amount(result.isAmountInvalid() ? lastValidLoan.getAmount() : loanCase.getAmount())
                .months(result.isPeriodInvalid() ? lastValidLoan.getMonths() : loanCase.getMonths())
                .interestRate(lastValidLoan.getInterestRate())
                .effectiveRate(lastValidLoan.getEffectiveRate())
                .build();

        consumerLoanSteps.checkSummary(expected);
        ConsumerLoan shown = consumerLoanSteps.getLoan();
        Assert.assertEquals(shown.getAmount(), expected.getAmount(), "Amount in the summary");
        Assert.assertEquals(shown.getMonths(), expected.getMonths(), "Months in the summary");
        consumerLoanSteps.checkMonthlyPayment(shown);
    }
}
