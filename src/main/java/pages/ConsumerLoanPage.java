package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import components.CalculatorSummaryComponent;

public class ConsumerLoanPage {
    private final Page page;
    public Locator title;
    public Locator byAmountTab;
    public Locator calculator;
    public Locator amountField;
    public Locator amountInput;
    public Locator amountBox;
    public Locator periodField;
    public Locator periodInput;
    public Locator periodBox;
    public CalculatorSummaryComponent summary;
    public ConsumerLoanPage(Page page) {
        this.page = page;
        title = page.locator(".tbcx-pw-cta-section__info__title").first();
        byAmountTab = page.locator("button.tbcx-pw-tab-segment__item").first();
        calculator = page.locator("fcl-by-amount-calculator");
        amountField = calculator.locator("tbcx-slider-input").nth(0);
        amountInput = amountField.locator("input");
        amountBox = amountField.locator(".tbcx-text-input");
        periodField = calculator.locator("tbcx-slider-input").nth(1);
        periodInput = periodField.locator("input");
        periodBox = periodField.locator(".tbcx-text-input");
        summary = new CalculatorSummaryComponent(calculator);
    }
}
