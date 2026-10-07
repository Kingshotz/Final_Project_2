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
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import steps.ui.MortgageSteps;

import static constants.Constants.EN_MORTGAGE_AMOUNT_ERROR;
import static constants.Constants.MORTGAGE_AMOUNT_ABOVE_MAX;
import static constants.Constants.MORTGAGE_GEL_ONLY_PERIOD;
import static constants.Constants.MORTGAGE_PERIOD_ABOVE_MAX;
import static utils.RandomGenerator.randomMortgageAmount;
import static utils.RandomGenerator.randomMortgagePeriod;

@TmsLink("SCRUM-T15")
@Epic("TBC Bank Digital Services")
@Feature("Mortgage Loan Calculator")
public class MortgageCalculatorNegativeScenario extends BaseTest {
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

    @Test(priority = 2, description = "Enter a valid GEL loan")
    @Story("Enter Valid GEL Loan")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Enters a random amount and a period valid in every currency (1 - 10 years), validates the summary "
            + "and the monthly payment; this is the last valid calculation the next tests fall back to.")
    public void enterValidGelLoan() {
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

    @Test(priority = 3, description = "Period allowed only in GEL is accepted in GEL")
    @Story("Accept 15 Year Period in GEL")
    @Severity(SeverityLevel.NORMAL)
    @Description("Enters a 15 year period with GEL selected and validates it is accepted, since GEL loans can last up "
            + "to 20 years.")
    public void enterGelOnlyPeriod() {
        String gelOnlyPeriod = String.valueOf(MORTGAGE_GEL_ONLY_PERIOD);

        mortgageSteps
                .fillPeriod(gelOnlyPeriod)
                .checkNoPeriodError()
                .checkSummary(String.valueOf(loan.getAmount()), gelOnlyPeriod, LoanCurrency.GEL);
    }

    @Test(priority = 4, description = "Period allowed in GEL is rejected in USD")
    @Story("Reject 15 Year Period in USD")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Switches the loan currency to USD and validates the 15 year period is marked invalid (USD loans last "
            + "at most 10 years) while the summary still shows the GEL calculation.")
    public void switchToUsdWithTooLongPeriod() {
        mortgageSteps
                .chooseCurrency(LoanCurrency.USD)
                .checkPeriodError()
                .checkSummary(String.valueOf(loan.getAmount()), String.valueOf(MORTGAGE_GEL_ONLY_PERIOD), LoanCurrency.GEL);
    }

    @Test(priority = 5, description = "Corrected period is accepted in USD")
    @Story("Accept Corrected Period in USD")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Enters the maximum USD period of 10 years, validates the error disappears, and validates the summary "
            + "and monthly payment are recalculated in USD.")
    public void correctPeriodForUsd() {
        String usdMaxPeriod = String.valueOf(LoanCurrency.USD.getMaxPeriod());

        mortgageSteps
                .fillPeriod(usdMaxPeriod)
                .checkNoPeriodError()
                .checkSummary(String.valueOf(loan.getAmount()), usdMaxPeriod, LoanCurrency.USD);

        loan = mortgageSteps.getMortgageLoan();
        mortgageSteps.checkMonthlyPayment(loan);
    }

    @Test(priority = 6, description = "Period above maximum is not applied")
    @Story("Reject Period Above 20 Years")
    @Severity(SeverityLevel.NORMAL)
    @Description("Switches back to GEL, enters a 25 year period, and validates it is marked invalid while the summary "
            + "and monthly payment keep the last valid values.")
    public void periodAboveMaximum() {
        mortgageSteps
                .chooseCurrency(LoanCurrency.GEL)
                .checkSummary(String.valueOf(loan.getAmount()), String.valueOf(loan.getYears()), LoanCurrency.GEL);
        loan = mortgageSteps.getMortgageLoan();

        mortgageSteps
                .fillPeriod(String.valueOf(MORTGAGE_PERIOD_ABOVE_MAX))
                .checkPeriodError()
                .checkSummary(loan)
                .checkMonthlyPayment(loan);
    }

    @Test(priority = 7, description = "Amount above maximum shows an error")
    @Story("Reject Amount Above 500 000 GEL")
    @Severity(SeverityLevel.NORMAL)
    @Description("Restores a valid period, enters an amount of 500 001, and validates the 'Income must be lower than "
            + "500000' error while the summary and monthly payment keep the last valid values.")
    public void amountAboveMaximum() {
        mortgageSteps
                .fillPeriod(String.valueOf(loan.getYears()))
                .checkNoPeriodError()
                .fillAmount(String.valueOf(MORTGAGE_AMOUNT_ABOVE_MAX))
                .checkAmountError(EN_MORTGAGE_AMOUNT_ERROR)
                .checkSummary(loan)
                .checkMonthlyPayment(loan);
    }
}
