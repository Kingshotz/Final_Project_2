package tests.ui;

import api.models.ExchangeRate;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.qameta.allure.TmsLink;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import steps.api.ExchangeRatesApiSteps;
import steps.ui.CurrencyExchangeSteps;

import java.util.List;

import static constants.Constants.EN_API_LOCALE;
import static constants.Constants.STATUS_OK;
import static utils.RandomGenerator.randomExchangeAmount;

@TmsLink("SCRUM-T16")
@Epic("TBC Bank Digital Services")
@Feature("Currency Exchange Calculator")
public class CurrencyConverterApiUiScenario extends BaseTest {
    CurrencyExchangeSteps currencyExchangeSteps;
    ExchangeRatesApiSteps exchangeRatesApiSteps = new ExchangeRatesApiSteps();
    String sellCurrency;
    String buyCurrency;
    ExchangeRate rate;

    @BeforeClass
    public void innerSetup() {
        currencyExchangeSteps = new CurrencyExchangeSteps(page);
    }

    @Test(priority = 1, description = "Navigate to Currency Exchange page")
    @Story("Navigate to Currency Exchange Page")
    @Severity(SeverityLevel.NORMAL)
    @Description("Opens the quick actions panel on the home page, clicks Currency Exchange, and validates the exchange "
            + "rates page opens.")
    public void navigateToCurrencyExchangePage() {
        currencyExchangeSteps
                .openQuickActions()
                .checkQuickActionsOpened()
                .gotoCurrencyExchangePage();
    }

    @Test(priority = 2, description = "Get the rate of a random currency pair from the API")
    @Story("Get Cross Currency Rate from API")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Sends GET /exchangeRates/commercialList, picks two different random currencies, sends GET "
            + "/exchangeRates/getExchangeRate for the pair, and validates status 200 and a positive rate with buy "
            + "lower than sell.")
    public void getRateFromApi() {
        Response ratesResponse = exchangeRatesApiSteps.getCommercialRates(EN_API_LOCALE);
        exchangeRatesApiSteps.checkStatus(ratesResponse, STATUS_OK);
        List<String> pair = exchangeRatesApiSteps.randomCurrencyPair(exchangeRatesApiSteps.readCurrencyCodes(ratesResponse));
        sellCurrency = pair.get(0);
        buyCurrency = pair.get(1);

        Response rateResponse = exchangeRatesApiSteps.getExchangeRate(sellCurrency, buyCurrency);
        exchangeRatesApiSteps.checkStatus(rateResponse, STATUS_OK);
        rate = exchangeRatesApiSteps.readExchangeRate(rateResponse, sellCurrency, buyCurrency);
    }

    @Test(priority = 3, description = "Choose the currency pair in the converter")
    @Story("Select API Currency Pair in Converter")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Selects the first currency of the API pair to sell and the second to receive, and validates the "
            + "converter shows the chosen pair.")
    public void chooseCurrencyPair() {
        currencyExchangeSteps
                .chooseSellCurrency(sellCurrency)
                .chooseBuyCurrency(buyCurrency);
    }

    @Test(priority = 4, description = "Converted amount uses the API rate")
    @Story("Convert Cross Currency with API Rate")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Enters a random amount to sell and validates the amount to receive equals amount x API buy rate / "
            + "currency weight.")
    public void convertWithApiRate() {
        currencyExchangeSteps
                .fillSellAmount(randomExchangeAmount())
                .checkConversion(rate);
    }

    @Test(priority = 5, description = "Swap currencies and get the reverse rate from the API")
    @Story("Swap Pair and Get Reverse Rate from API")
    @Severity(SeverityLevel.NORMAL)
    @Description("Clicks the swap button, validates the currencies change places, then sends GET "
            + "/exchangeRates/getExchangeRate for the reversed pair and validates the returned rate.")
    public void swapAndGetReverseRate() {
        currencyExchangeSteps.swapCurrencies();

        Response rateResponse = exchangeRatesApiSteps.getExchangeRate(buyCurrency, sellCurrency);
        exchangeRatesApiSteps.checkStatus(rateResponse, STATUS_OK);
        rate = exchangeRatesApiSteps.readExchangeRate(rateResponse, buyCurrency, sellCurrency);
    }

    @Test(priority = 6, description = "Converted amount after swap uses the reverse API rate")
    @Story("Convert Reversed Pair with API Rate")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Enters a random amount to sell and validates the amount to receive equals amount x the reversed "
            + "pair's API buy rate / currency weight.")
    public void convertAfterSwap() {
        currencyExchangeSteps
                .fillSellAmount(randomExchangeAmount())
                .checkConversion(rate);
    }
}
