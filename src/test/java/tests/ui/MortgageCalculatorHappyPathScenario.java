package tests.ui;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.qameta.allure.TmsLink;
import models.LoanCurrency;
import models.MortgageLoan;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import steps.ui.MortgageSteps;

import static utils.RandomGenerator.randomMortgageAmount;
import static utils.RandomGenerator.randomMortgagePeriod;

@TmsLink("SCRUM-T12")
@Epic("TBC Bank Digital Services")
@Feature("Mortgage Loan Calculator")
public class MortgageCalculatorHappyPathScenario extends BaseTest {
    MortgageSteps mortgageSteps;
    MortgageLoan loan;

    @BeforeClass
    public void innerSetup() {
        mortgageSteps = new MortgageSteps(page);
    }

    @Test(priority = 1, description = "Navigate to Mortgage page from the main menu")
    @Story("Navigate to Mortgage Calculator")
    @Severity(SeverityLevel.NORMAL)
    @Description("Opens the main menu on the home page, clicks Mortgage, and validates the mortgage page opens with "
            + "the calculator in By Amount mode.")
    public void navigateToMortgagePage() {
        mortgageSteps
                .openMainMenu()
                .gotoMortgagePage();

        mortgageSteps.checkCalculatorOpened();
    }

    @Test(priority = 2, description = "Default calculation is correct")
    @Story("Validate Default Monthly Payment")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Reads the default loan from the summary, validates it is in GEL, and validates the monthly payment "
            + "against the annuity formula.")
    public void checkDefaultCalculation() {
        loan = mortgageSteps.getMortgageLoan();
        Assert.assertEquals(loan.getCurrency(), LoanCurrency.GEL);

        mortgageSteps.checkMonthlyPayment(loan);
    }

    @Test(priority = 3, description = "Calculate GEL mortgage for random amount and period")
    @Story("Calculate GEL Monthly Payment")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Enters a random amount and a period valid in every currency (1 - 10 years), validates there are no "
            + "errors and the summary shows the entered values, and validates the monthly payment against the "
            + "annuity formula.")
    public void calculateGelMortgage() {
        String amount = randomMortgageAmount();
        String years = randomMortgagePeriod();

        mortgageSteps
                .fillAmount(amount)
                .fillPeriod(years)
                .checkNoAmountError()
                .checkNoPeriodError()
                .checkSummary(amount, years, LoanCurrency.GEL);

        loan = mortgageSteps.getMortgageLoan();
        mortgageSteps.checkMonthlyPayment(loan);
    }

    @Test(priority = 4, description = "Switch loan currency to USD")
    @Story("Recalculate Monthly Payment in USD")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Switches the loan currency to USD, validates the amount and period are kept and the USD interest "
            + "rate is used, and validates the recalculated monthly payment against the annuity formula.")
    public void switchCurrencyToUsd() {
        mortgageSteps
                .chooseCurrency(LoanCurrency.USD)
                .checkSummary(String.valueOf(loan.getAmount()), String.valueOf(loan.getYears()), LoanCurrency.USD);

        loan = mortgageSteps.getMortgageLoan();
        Assert.assertEquals(loan.getCurrency(), LoanCurrency.USD);

        mortgageSteps.checkMonthlyPayment(loan);
    }

    @Test(priority = 5, description = "Switch loan currency to EUR")
    @Story("Recalculate Monthly Payment in EUR")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Switches the loan currency to EUR, validates the amount and period are kept and the EUR interest "
            + "rate is used, and validates the recalculated monthly payment against the annuity formula.")
    public void switchCurrencyToEur() {
        mortgageSteps
                .chooseCurrency(LoanCurrency.EUR)
                .checkSummary(String.valueOf(loan.getAmount()), String.valueOf(loan.getYears()), LoanCurrency.EUR);

        loan = mortgageSteps.getMortgageLoan();
        Assert.assertEquals(loan.getCurrency(), LoanCurrency.EUR);

        mortgageSteps.checkMonthlyPayment(loan);
    }
}
