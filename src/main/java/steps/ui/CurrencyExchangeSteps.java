package steps.ui;

import api.models.ExchangeRate;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.TimeoutError;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import models.CurrencyActions;
import models.CurrencyRate;
import org.testng.Assert;
import pages.CurrencyExchangePage;

import java.util.Random;

import static constants.Constants.BASE_AMOUNT;
import static constants.Constants.CALCULATION_TIMEOUT;
import static constants.Constants.CURRENCY_EXCHANGE_URL;
import static constants.Constants.CURRENCY_CODE;
import static constants.Constants.CURRENCY_GEL;
import static constants.Constants.CURRENCY_USD;
import static constants.Constants.EXCHANGE_AMOUNT;
import static constants.Constants.EXCHANGE_RELATIVE_TOLERANCE;
import static constants.Constants.EXCHANGE_TOLERANCE;
import static constants.Constants.OPEN_PANEL_CLASS;
import static constants.Constants.RATE_NUMBER;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class CurrencyExchangeSteps {
    public final CurrencyExchangePage currencyExchangePage;
    private final Page page;
    private final Random rand;

    public CurrencyExchangeSteps(Page page) {
        this.page = page;
        this.currencyExchangePage = new CurrencyExchangePage(page);
        rand = new Random();
    }

    @Step("Open quick actions panel")
    public CurrencyExchangeSteps openQuickActions() {
        currencyExchangePage.quickActions.open();
        return this;
    }

    @Step("Check quick actions panel is open")
    public CurrencyExchangeSteps checkQuickActionsOpened() {
        assertThat(currencyExchangePage.quickActions.panel).hasClass(OPEN_PANEL_CLASS);
        assertThat(currencyExchangePage.quickActions.currencyLink).isVisible();
        assertThat(currencyExchangePage.quickActions.branchesLink).isVisible();
        return this;
    }

    @Step("Go to Currency Exchange page")
    public CurrencyExchangeSteps gotoCurrencyExchangePage() {
        currencyExchangePage.quickActions.currencyLink.click();
        assertThat(page).hasURL(CURRENCY_EXCHANGE_URL);
        return this;
    }

    @Step("Choose sell currency {code}")
    public CurrencyExchangeSteps chooseSellCurrency(String code) {
        currencyExchangePage.sellCurrencyDropdown.click();
        assertThat(currencyExchangePage.option(code)).isVisible();
        currencyExchangePage.option(code).click();
        assertThat(currencyExchangePage.dropdownPanel).isHidden();
        assertThat(currencyExchangePage.sellSelected).hasText(code);
        return this;
    }

    @Step("Choose buy currency {code}")
    public CurrencyExchangeSteps chooseBuyCurrency(String code) {
        currencyExchangePage.buyCurrencyDropdown.click();
        assertThat(currencyExchangePage.option(code)).isVisible();
        currencyExchangePage.option(code).click();
        assertThat(currencyExchangePage.dropdownPanel).isHidden();
        assertThat(currencyExchangePage.buySelected).hasText(code);
        return this;
    }

    @Step("Pick a random currency from the exchange rates table")
    public CurrencyRate chooseRandomCurrency() {
        assertThat(currencyExchangePage.currencyCode(0)).hasText(CURRENCY_CODE);
        int count = currencyExchangePage.currencyElements.count();
        Assert.assertTrue(count > 0, "Exchange rates table should contain currencies");
        int index = rand.nextInt(count);

        assertThat(currencyExchangePage.currencyCode(index)).hasText(CURRENCY_CODE);
        assertThat(currencyExchangePage.buyRate(index)).hasText(RATE_NUMBER);
        assertThat(currencyExchangePage.sellRate(index)).hasText(RATE_NUMBER);
        assertThat(currencyExchangePage.currencyBaseAmount(index)).hasText(BASE_AMOUNT);

        CurrencyRate currency = CurrencyRate.builder()
                .code(clean(currencyExchangePage.currencyCode(index).innerText()))
                .buyRate(Double.parseDouble(clean(currencyExchangePage.buyRate(index).innerText())))
                .sellRate(Double.parseDouble(clean(currencyExchangePage.sellRate(index).innerText())))
                .baseAmount(Integer.parseInt(clean(currencyExchangePage.currencyBaseAmount(index).innerText()).split(" ")[0]))
                .build();

        Assert.assertTrue(currency.getBuyRate() > 0 && currency.getSellRate() > 0,
                currency.getCode() + " rates should be positive");
        Assert.assertTrue(currency.getBuyRate() < currency.getSellRate(),
                currency.getCode() + ": bank buy rate should be lower than sell rate");

        Allure.addAttachment("Chosen currency",
                "code: " + currency.getCode()
                        + "\nbase amount: " + currency.getBaseAmount()
                        + "\nbuy rate: " + currency.getBuyRate()
                        + "\nsell rate: " + currency.getSellRate());
        return currency;
    }

    @Step("Check calculator starts with USD -> GEL")
    public CurrencyExchangeSteps checkStartCurrencies() {
        assertThat(currencyExchangePage.sellSelected).hasText(CURRENCY_USD);
        assertThat(currencyExchangePage.buySelected).hasText(CURRENCY_GEL);
        return this;
    }

    public String selectedSellCurrency() {
        return clean(currencyExchangePage.sellSelected.innerText());
    }

    public String selectedBuyCurrency() {
        return clean(currencyExchangePage.buySelected.innerText());
    }

    @Step("Swap sell and buy currencies")
    public CurrencyExchangeSteps swapCurrencies() {
        String sellBefore = selectedSellCurrency();
        String buyBefore = selectedBuyCurrency();
        currencyExchangePage.swapButton.click();
        assertThat(currencyExchangePage.sellSelected).hasText(buyBefore);
        assertThat(currencyExchangePage.buySelected).hasText(sellBefore);
        return this;
    }

    @Step("Fill sell amount {input}")
    public CurrencyExchangeSteps fillSellAmount(String input) {
        currencyExchangePage.sellAmountInput.fill(input);
        assertThat(currencyExchangePage.sellAmountInput).hasValue(EXCHANGE_AMOUNT);
        return this;
    }

    @Step("Check converted amount for {currency.code} ({action})")
    public CurrencyExchangeSteps checkCalculator(CurrencyRate currency, CurrencyActions action) {
        double sellAmount = parseAmount(currencyExchangePage.sellAmountInput.inputValue());

        double expected;
        double tolerance;
        if (action == CurrencyActions.BUYGEL) {
            expected = sellAmount * currency.getBuyRate() / currency.getBaseAmount();
            tolerance = EXCHANGE_TOLERANCE;
        } else {
            expected = sellAmount * currency.getBaseAmount() / currency.getSellRate();
            tolerance = Math.max(EXCHANGE_TOLERANCE, expected * EXCHANGE_RELATIVE_TOLERANCE);
        }

        return checkBuyAmount(sellAmount, expected, tolerance,
                sellAmount + " " + currency.getCode() + " (" + action + ") converted incorrectly");
    }

    @Step("Check converted amount uses the API rate {rate.iso1} -> {rate.iso2}")
    public CurrencyExchangeSteps checkConversion(ExchangeRate rate) {
        assertThat(currencyExchangePage.sellSelected).hasText(rate.getIso1());
        assertThat(currencyExchangePage.buySelected).hasText(rate.getIso2());

        double sellAmount = parseAmount(currencyExchangePage.sellAmountInput.inputValue());
        double expected = sellAmount * rate.getBuyRate() / rate.getCurrencyWeight();
        double tolerance = Math.max(EXCHANGE_TOLERANCE, expected * EXCHANGE_RELATIVE_TOLERANCE);

        return checkBuyAmount(sellAmount, expected, tolerance,
                sellAmount + " " + rate.getIso1() + " -> " + rate.getIso2() + " converted incorrectly");
    }

    private CurrencyExchangeSteps checkBuyAmount(double sellAmount, double expected, double tolerance, String message) {
        try {
            page.waitForCondition(() -> Math.abs(readBuyAmount() - expected) <= tolerance,
                    new Page.WaitForConditionOptions().setTimeout(CALCULATION_TIMEOUT));
        } catch (TimeoutError ignored) {
        }

        double actual = readBuyAmount();
        Allure.addAttachment("Calculation",
                "sell amount: " + sellAmount
                        + "\nexpected: " + expected
                        + "\nactual: " + actual
                        + "\ntolerance: " + tolerance);

        Assert.assertEquals(actual, expected, tolerance, message);
        return this;
    }

    private double readBuyAmount() {
        return parseAmount(currencyExchangePage.buyAmountInput.inputValue());
    }

    private static double parseAmount(String text) {
        String cleaned = clean(text).replace(",", "");
        return cleaned.isEmpty() ? Double.NaN : Double.parseDouble(cleaned);
    }

    private static String clean(String text) {
        return text.replace(' ', ' ').replaceAll("\\s+", " ").trim();
    }
}
