package tests.ui;

import com.microsoft.playwright.*;
import components.CookieConsentComponent;
import io.qameta.allure.Allure;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;

import java.io.ByteArrayInputStream;

import static constants.Constants.DEFAULT_HEADLESS;
import static constants.Constants.HEADLESS_PROPERTY;
import static constants.Constants.BASE_URL;
import static constants.Constants.BROWSER_PROPERTY;
import static constants.Constants.DEFAULT_BROWSER;
import static constants.Constants.DEFAULT_SLOW_MO;
import static constants.Constants.SCREENSHOT_ATTACHMENT;
import static constants.Constants.SCREENSHOT_EXTENSION;
import static constants.Constants.SCREENSHOT_TYPE;
import static constants.Constants.SLOW_MO_PROPERTY;
import static constants.Constants.URL_ATTACHMENT;
import static constants.Constants.VIEWPORT_HEIGHT;
import static constants.Constants.VIEWPORT_WIDTH;
import static constants.Constants.FIREFOX;
import static constants.Constants.WEBKIT;

public class BaseTest {
    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext browserContext;
    protected Page page;

    @BeforeClass
    public void beforeClass() {
        playwright = Playwright.create();
        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                .setHeadless(Boolean.parseBoolean(System.getProperty(HEADLESS_PROPERTY, DEFAULT_HEADLESS)))
                .setSlowMo(Double.parseDouble(System.getProperty(SLOW_MO_PROPERTY, DEFAULT_SLOW_MO)))
        ;
        browser = browserType().launch(launchOptions);
        browserContext = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(VIEWPORT_WIDTH, VIEWPORT_HEIGHT));
        page = browserContext.newPage();
        page.navigate(BASE_URL);
        new CookieConsentComponent(page).rejectIfShown();
    }

    private BrowserType browserType() {
        return switch (System.getProperty(BROWSER_PROPERTY, DEFAULT_BROWSER).toLowerCase()) {
            case FIREFOX -> playwright.firefox();
            case WEBKIT -> playwright.webkit();
            default -> playwright.chromium();
        };
    }

    @AfterMethod(alwaysRun = true)
    public void attachScreenshotOnFailure(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE && page != null) {
            byte[] screenshot = page.screenshot(new Page.ScreenshotOptions().setFullPage(false));
            Allure.addAttachment(SCREENSHOT_ATTACHMENT, SCREENSHOT_TYPE,
                    new ByteArrayInputStream(screenshot), SCREENSHOT_EXTENSION);
            Allure.addAttachment(URL_ATTACHMENT, page.url());
        }
    }

    @AfterClass(alwaysRun = true)
    public void afterClass() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }
}
