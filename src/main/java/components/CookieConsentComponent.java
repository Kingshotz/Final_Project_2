package components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.TimeoutError;

import static constants.Constants.COOKIE_BANNER_TIMEOUT;
import static constants.Constants.COOKIE_REJECT_TEXT;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class CookieConsentComponent {
    private final Page page;
    public final Locator root;
    public final Locator rejectButton;

    public CookieConsentComponent(Page page) {
        this.page = page;
        root = page.locator("app-cookie-consent");
        rejectButton = root.locator("button").filter(new Locator.FilterOptions().setHasText(COOKIE_REJECT_TEXT));
    }

    public void rejectIfShown() {
        try {
            rejectButton.click(new Locator.ClickOptions().setTimeout(COOKIE_BANNER_TIMEOUT));
            assertThat(rejectButton).isHidden();
        } catch (TimeoutError e) {
        }
    }
}
