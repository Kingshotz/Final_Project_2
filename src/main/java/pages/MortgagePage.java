package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import components.CalculatorSummaryComponent;
import components.HeaderComponent;

import static constants.Constants.MORTGAGE_PATH;

public class MortgagePage {
    private final Page page;
    public final HeaderComponent header;
    public final Locator mortgageLink;
    public Locator title;
    public Locator calculatorTab;
    public Locator calculator;
    public Locator byAmountTab;
    public Locator amountInput;
    public Locator amountError;
    public Locator amountCurrencySymbol;
    public Locator activeCurrency;
    public Locator periodInput;
    public Locator periodField;
    public CalculatorSummaryComponent summary;
    public MortgagePage(Page page) {
        this.page = page;
        header = new HeaderComponent(page);
        mortgageLink = header.menuLink(MORTGAGE_PATH);
        title = page.locator("h1");
        calculatorTab = page.locator("button.tbcx-pw-tab-menu__item").first();
        calculator = page.locator("tbc-mortgage-by-amount-calculator");
        byAmountTab = page.locator("button.tbcx-pw-tab-segment__item").first();
        amountInput = calculator.locator("tbcx-text-input input[type='text']");
        amountError = calculator.locator("tbcx-text-input .description.errored");
        amountCurrencySymbol = calculator.locator(".tbcx-pw-calculator-form__input-specifier");
        activeCurrency = calculator.locator(".tbcx-pw-calculator-form__currency--active");
        periodInput = calculator.locator("tbcx-slider-input input[type='number']");
        periodField = calculator.locator("tbcx-slider-input .tbcx-text-input");
        summary = new CalculatorSummaryComponent(calculator);
    }

    public Locator currencyButton(String code) {
        return calculator.locator(".tbcx-pw-calculator-form__currency:text-is('" + code + "')");
    }
}
