package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import components.QuickActionsComponent;

public class CurrencyExchangePage {
    private final Page page;
    public final QuickActionsComponent quickActions;
    public Locator currencyElements;
    public Locator sellAmountInput;
    public Locator buyAmountInput;
    public final Locator sellCurrencyDropdown;
    public final Locator buyCurrencyDropdown;
    public final Locator sellSelected;
    public final Locator buySelected;
    public final Locator dropdownPanel;
    public final Locator swapButton;
    public CurrencyExchangePage(Page page) {
        this.page = page;
        quickActions = new QuickActionsComponent(page);
        currencyElements = page.locator("div.tbcx-pw-exchange-rates__table .ag-row");
        sellAmountInput = page.locator("#sell-amount");
        buyAmountInput = page.locator("#buy-amount");
        sellCurrencyDropdown = page.locator("app-currency-dropdown[formcontrolname='sellCurrency'] button");
        buyCurrencyDropdown  = page.locator("app-currency-dropdown[formcontrolname='buyCurrency'] button");
        sellSelected = page.locator("app-currency-dropdown[formcontrolname='sellCurrency'] .currency-dropdown__selected");
        buySelected  = page.locator("app-currency-dropdown[formcontrolname='buyCurrency'] .currency-dropdown__selected");
        dropdownPanel = page.locator(".currency-dropdown__panel");
        swapButton = page.locator(".currency-calculator__swap__button");
    }

    private Locator row(int index) {
        return currencyElements.nth(index);
    }

    public Locator currencyCode(int index) {
        return row(index).locator("[col-id='currencyName'] .tbcx-pw-currency-badge");
    }

    public Locator buyRate(int index) {
        return row(index).locator("[col-id='buyRate']");
    }

    public Locator sellRate(int index) {
        return row(index).locator("[col-id='sellRate']");
    }

    public Locator currencyBaseAmount(int index) {
        return row(index).locator("[col-id='currencyName'] .tbcx-pw-table-cell__content__title");
    }

    public Locator option(String code) {
        return dropdownPanel.locator(".currency-dropdown__item")
                .filter(new Locator.FilterOptions()
                        .setHas(page.locator(".currency-dropdown__item-label:text-is('" + code + "')")));
    }
}
