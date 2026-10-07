package components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import static constants.Constants.BRANCHES_PATH;
import static constants.Constants.CURRENCY_EXCHANGE_PATH;
import static constants.Constants.OPEN_PANEL_CLASS;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class QuickActionsComponent {
    private final Page page;
    public final Locator root;
    public final Locator panel;
    public final Locator toggleButton;
    public final Locator currencyLink;
    public final Locator branchesLink;

    public QuickActionsComponent(Page page) {
        this.page = page;
        root = page.locator("app-quick-actions");
        panel = root.locator(".tbcx-pw-sticky-actions");
        toggleButton = root.locator("[tbcxpwtogglecontent] button");
        currencyLink = root.locator("a[href$='" + CURRENCY_EXCHANGE_PATH + "']");
        branchesLink = root.locator("a[href$='" + BRANCHES_PATH + "']");
    }

    public void open() {
        assertThat(toggleButton).isVisible();
        String cls = panel.getAttribute("class");
        if (cls == null || !OPEN_PANEL_CLASS.matcher(cls).find()) {
            toggleButton.click();
        }
        assertThat(panel).hasClass(OPEN_PANEL_CLASS);
    }
}
