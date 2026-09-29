package components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class HeaderComponent {
    private final Page page;
    public final Locator root;
    public final Locator menuButton;
    public final Locator desktopNavigation;
    public final Locator languageSwitcher;
    public final Locator languageLabel;

    public HeaderComponent(Page page) {
        this.page = page;
        root = page.locator("app-header");
        menuButton = root.locator(".tbcx-pw-hamburger-menu__button");
        desktopNavigation = root.locator("tbcx-pw-navigation.show-desktop-only .tbcx-pw-navigation-item--selected");
        languageSwitcher = root.locator(".tbcx-language-select__field");
        languageLabel = languageSwitcher.locator(".tbcx-language-select__selected");
    }

    public Locator menuLink(String path) {
        return root.locator(".show-tablet-up a[href$='" + path + "']");
    }

    public void openMenu() {
        assertThat(languageSwitcher).isVisible();
        if (menuButton.isVisible()) {
            menuButton.click();
        } else {
            assertThat(desktopNavigation).isVisible();
            desktopNavigation.hover();
        }
    }

    public void switchLanguage() {
        assertThat(languageSwitcher).isVisible();
        languageSwitcher.click();
    }
}
