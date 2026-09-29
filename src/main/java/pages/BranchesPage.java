package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import components.QuickActionsComponent;

import static utils.TextParser.exactText;

public class BranchesPage {
    private final Page page;
    public final QuickActionsComponent quickActions;
    public Locator sectionTitle;
    public Locator searchInput;
    public Locator tabs;
    public Locator resultItems;
    public Locator resultTitles;
    public Locator resultDescriptions;
    public Locator resultIcons;
    public BranchesPage(Page page) {
        this.page = page;
        quickActions = new QuickActionsComponent(page);
        sectionTitle = page.locator(".tbcx-pw-atm-branches-section__title");
        searchInput = page.locator("input.search-input");
        tabs = page.locator("button.tbcx-pw-tab-menu__item");
        resultItems = page.locator(".tbcx-pw-atm-branches-section__list--desktop-view app-atm-branches-section-list-item");
        resultTitles = resultItems.locator(".tbcx-pw-atm-branches-section__list-item-title");
        resultDescriptions = resultItems.locator(".tbcx-pw-atm-branches-section__list-item-description");
        resultIcons = resultItems.locator(".tbcx-pw-atm-branches-section__list-item-icon tbcx-icon");
    }

    public Locator tab(String label) {
        return tabs.filter(new Locator.FilterOptions().setHasText(exactText(label)));
    }

    public Locator chip(String label) {
        return page.locator("label.tbcx-pw-chip").filter(new Locator.FilterOptions().setHasText(exactText(label)));
    }

    public Locator chipCheckbox(String label) {
        return chip(label).locator("input.tbcx-pw-chip__input");
    }
}
