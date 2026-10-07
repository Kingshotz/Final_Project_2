package tests.ui;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.qameta.allure.TmsLink;
import models.LoanCurrency;
import models.LocaleData;
import models.MortgageLoan;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import steps.ui.MortgageSteps;

import static utils.RandomGenerator.randomMortgageAmount;
import static utils.RandomGenerator.randomMortgagePeriod;
import static constants.Constants.MORTGAGE_AMOUNT_ABOVE_MAX;

@TmsLink("SCRUM-T14")
@Epic("TBC Bank Digital Services")
@Feature("Website Localization (Georgian / English)")
public class LocalizationScenario extends BaseTest {
    MortgageSteps mortgageSteps;

    @BeforeClass
    public void innerSetup() {
        mortgageSteps = new MortgageSteps(page);
    }

    @DataProvider(name = "locales")
    public Object[][] locales() {
        return new Object[][]{
                {LocaleData.KA},
                {LocaleData.EN}
        };
    }

    @Test(priority = 1, dataProvider = "locales", description = "Mortgage calculation journey is localized")
    @Story("Localized Mortgage Calculator")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Opens the mortgage page in the given language, validates the localized title and tabs, calculates a "
            + "random loan and validates the monthly payment, then enters an amount above the limit and validates "
            + "the localized error message.")
    public void mortgageJourneyIsLocalized(LocaleData locale) {
        mortgageSteps.openMortgagePage(locale);
        mortgageSteps.checkCalculatorOpened(locale);

        String amount = randomMortgageAmount();
        String years = randomMortgagePeriod();
        mortgageSteps
                .fillAmount(amount)
                .fillPeriod(years)
                .checkNoAmountError()
                .checkSummary(amount, years, LoanCurrency.GEL);

        MortgageLoan loan = mortgageSteps.getMortgageLoan();
        mortgageSteps
                .checkMonthlyPayment(loan)
                .fillAmount(String.valueOf(MORTGAGE_AMOUNT_ABOVE_MAX))
                .checkAmountError(locale.getMortgageAmountError())
                .checkSummary(loan);
    }

    @Test(priority = 2, dataProvider = "locales", description = "Journey keeps working after switching language")
    @Story("Switch Language on Mortgage Page")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Opens the mortgage page in the given language, switches language from the header, validates the same "
            + "page is shown in the other language, then calculates a random loan and validates the monthly "
            + "payment.")
    public void journeyWorksAfterLanguageSwitch(LocaleData locale) {
        mortgageSteps.openMortgagePage(locale);
        mortgageSteps.checkCalculatorOpened(locale);

        mortgageSteps.switchLanguage(locale);
        mortgageSteps.checkCalculatorOpened(locale.other());

        String amount = randomMortgageAmount();
        String years = randomMortgagePeriod();
        mortgageSteps
                .fillAmount(amount)
                .fillPeriod(years)
                .checkSummary(amount, years, LoanCurrency.GEL);

        MortgageLoan loan = mortgageSteps.getMortgageLoan();
        mortgageSteps.checkMonthlyPayment(loan);
    }
}
