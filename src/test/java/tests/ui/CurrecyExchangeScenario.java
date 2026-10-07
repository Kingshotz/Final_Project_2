package tests.ui;

import io.qameta.allure.*;
import models.CurrencyActions;
import models.CurrencyRate;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import steps.ui.CurrencyExchangeSteps;

import static utils.RandomGenerator.randomExchangeAmount;
import static constants.Constants.CURRENCY_GEL;

@TmsLink("SCRUM-T11")
@Epic("TBC Bank Digital Services")
@Feature("Currency Exchange Calculator")
public class CurrecyExchangeScenario extends BaseTest {
    CurrencyExchangeSteps currencyExchangeSteps;
    CurrencyRate currency;

    @BeforeClass
    public void innerSetup() {
        currencyExchangeSteps = new CurrencyExchangeSteps(page);
    }

    @Test(priority = 1, description = "Open quick actions panel")
    @Story("Open Quick Actions Panel")
    @Severity(SeverityLevel.NORMAL)
    @Description("Clicks the quick actions button on the home page and validates the panel opens with the Currency "
            + "Exchange link visible.")
    public void openToggle() {
        currencyExchangeSteps
                .openQuickActions()
                .checkQuickActionsOpened();
    }

    @Test(priority = 2, description = "Navigate to Currency Exchange page")
    @Story("Navigate to Currency Exchange Page")
    @Severity(SeverityLevel.NORMAL)
    @Description("Clicks the Currency Exchange link in the quick actions panel and validates the exchange rates page "
            + "opens.")
    public void navigateToCurrencyExchangePage() {
        currencyExchangeSteps
                .gotoCurrencyExchangePage();
    }

    @Test(priority = 3, description = "Choose random sell currency")
    @Story("Select Foreign Currency to Sell")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Reads a random currency's code, base amount, buy and sell rates from the rates table, validates the "
            + "rates are positive with buy lower than sell, checks the converter starts as USD to GEL, and selects "
            + "the currency to sell with GEL to receive.")
    public void chooseSellCurrency() {
        currency = currencyExchangeSteps.chooseRandomCurrency();
        Assert.assertNotNull(currency);

        currencyExchangeSteps
                .checkStartCurrencies()
                .chooseSellCurrency(currency.getCode());

        Assert.assertEquals(currencyExchangeSteps.selectedSellCurrency(), currency.getCode());
        Assert.assertEquals(currencyExchangeSteps.selectedBuyCurrency(), CURRENCY_GEL);
    }

    @Test(priority = 4, description = "Convert foreign currency to GEL")
    @Story("Convert Foreign Currency to GEL")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Enters a random amount to sell and validates the GEL amount equals amount x buy rate / base amount "
            + "taken from the rates table.")
    public void fillAmount() {
        currencyExchangeSteps
                .fillSellAmount(randomExchangeAmount())
                .checkCalculator(currency, CurrencyActions.BUYGEL);
    }

    @Test(priority = 5, description = "Swap sell and buy currencies")
    @Story("Swap Converter Currencies")
    @Severity(SeverityLevel.NORMAL)
    @Description("Clicks the swap button and validates GEL becomes the currency to sell and the chosen foreign "
            + "currency becomes the currency to receive.")
    public void swapCurrencies() {
        currencyExchangeSteps.swapCurrencies();

        Assert.assertEquals(currencyExchangeSteps.selectedSellCurrency(), CURRENCY_GEL);
        Assert.assertEquals(currencyExchangeSteps.selectedBuyCurrency(), currency.getCode());
    }

    @Test(priority = 6,  description = "Choose random buy currency")
    @Story("Select Foreign Currency to Receive")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Reads another random currency from the rates table, selects it as the currency to receive, and "
            + "validates GEL stays the currency to sell.")
    public void chooseRandomBuyCurrency() {
        currency = currencyExchangeSteps.chooseRandomCurrency();
        Assert.assertNotNull(currency);

        currencyExchangeSteps.chooseBuyCurrency(currency.getCode());

        Assert.assertEquals(currencyExchangeSteps.selectedBuyCurrency(), currency.getCode());
        Assert.assertEquals(currencyExchangeSteps.selectedSellCurrency(), CURRENCY_GEL);
    }

    @Test(priority = 7, description = "Convert GEL to foreign currency")
    @Story("Convert GEL to Foreign Currency")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Enters a random GEL amount and validates the foreign currency amount equals amount x base amount / "
            + "sell rate taken from the rates table.")
    public void fillAmountAfterSwap() {
        currencyExchangeSteps
                .fillSellAmount(randomExchangeAmount())
                .checkCalculator(currency, CurrencyActions.SELLGEL);
    }
}
