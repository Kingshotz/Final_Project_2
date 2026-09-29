package steps.api;

import api.models.CommercialRate;
import api.models.CommercialRates;
import api.models.ExchangeRate;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static constants.Constants.COMMERCIAL_RATES_ENDPOINT;
import static constants.Constants.EXCHANGE_RATE_ENDPOINT;
import static constants.Constants.ISO1_PARAM;
import static constants.Constants.ISO2_PARAM;
import static constants.Constants.LOCALE_PARAM;
import static io.restassured.RestAssured.given;

public class ExchangeRatesApiSteps extends BaseApiSteps {
    @Step("GET /exchangeRates/commercialList?locale={locale}")
    public Response getCommercialRates(String locale) {
        return given().spec(SPEC)
                .queryParam(LOCALE_PARAM, locale)
                .when()
                .get(COMMERCIAL_RATES_ENDPOINT);
    }

    @Step("Read the currencies the bank exchanges")
    public List<String> readCurrencyCodes(Response response) {
        List<CommercialRate> rates = response.as(CommercialRates.class).getRates();
        Assert.assertNotNull(rates, "rates list is missing");
        Assert.assertTrue(rates.size() >= 2, "Expected at least two currencies, got " + rates.size());
        return rates.stream().map(CommercialRate::getIso).toList();
    }

    @Step("Pick two different random currencies")
    public List<String> randomCurrencyPair(List<String> codes) {
        List<String> shuffled = new ArrayList<>(codes);
        Collections.shuffle(shuffled);
        return shuffled.subList(0, 2);
    }

    @Step("GET /exchangeRates/getExchangeRate?Iso1={iso1}&Iso2={iso2}")
    public Response getExchangeRate(String iso1, String iso2) {
        return given().spec(SPEC)
                .queryParam(ISO1_PARAM, iso1)
                .queryParam(ISO2_PARAM, iso2)
                .when()
                .get(EXCHANGE_RATE_ENDPOINT);
    }

    @Step("Read the {iso1} -> {iso2} rate and check it is valid")
    public ExchangeRate readExchangeRate(Response response, String iso1, String iso2) {
        Assert.assertTrue(response.contentType().startsWith(ContentType.JSON.toString()),
                "Content type: " + response.contentType());
        ExchangeRate rate = response.as(ExchangeRate.class);

        Assert.assertEquals(rate.getIso1(), iso1, "iso1");
        Assert.assertEquals(rate.getIso2(), iso2, "iso2");
        Assert.assertTrue(rate.getBuyRate() > 0 && rate.getSellRate() > 0,
                iso1 + " -> " + iso2 + " rates should be positive");
        Assert.assertTrue(rate.getBuyRate() < rate.getSellRate(),
                iso1 + " -> " + iso2 + ": bank buy rate should be lower than sell rate");
        Assert.assertTrue(rate.getCurrencyWeight() > 0, "currencyWeight should be positive");

        Allure.addAttachment("Rate from the API",
                iso1 + " -> " + iso2
                        + "\nbuy rate: " + rate.getBuyRate()
                        + "\nsell rate: " + rate.getSellRate()
                        + "\ncurrency weight: " + rate.getCurrencyWeight()
                        + "\nupdated: " + rate.getUpdateDate());
        return rate;
    }
}
