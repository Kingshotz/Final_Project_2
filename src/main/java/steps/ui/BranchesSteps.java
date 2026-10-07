package steps.ui;

import api.models.BranchLocation;
import api.models.BranchesRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Response;
import com.microsoft.playwright.assertions.LocatorAssertions;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.testng.Assert;
import pages.BranchesPage;

import java.util.List;

import static constants.Constants.ACTIVE_CLASS;
import static constants.Constants.BRANCHES_FIRST_RENDER_TIMEOUT;
import static constants.Constants.BRANCHES_LIST_URL;
import static constants.Constants.BRANCHES_TITLE;
import static constants.Constants.BRANCHES_URL;
import static constants.Constants.EN_API_LOCALE;
import static constants.Constants.NO_KEYWORD;
import static constants.Constants.POST;
import static constants.Constants.STATUS_OK;
import static constants.Constants.TIME_HH_MM;
import static constants.Constants.TWENTY_FOUR_SEVEN_TEXT;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static utils.TextParser.clean;

public class BranchesSteps {
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public final BranchesPage branchesPage;
    private final Page page;

    public BranchesSteps(Page page) {
        this.page = page;
        this.branchesPage = new BranchesPage(page);
    }

    @Step("Open quick actions panel")
    public BranchesSteps openQuickActions() {
        branchesPage.quickActions.open();
        return this;
    }

    @Step("Go to ATMs & Branches page")
    public BranchesSteps gotoBranchesPage() {
        branchesPage.quickActions.branchesLink.click();
        assertThat(page).hasURL(BRANCHES_URL);
        return this;
    }

    @Step("Check ATMs & Branches page is opened")
    public BranchesSteps checkBranchesPageOpened() {
        assertThat(branchesPage.sectionTitle).hasText(BRANCHES_TITLE);
        assertThat(branchesPage.searchInput).isVisible();
        assertThat(branchesPage.resultItems.first())
                .isVisible(new LocatorAssertions.IsVisibleOptions().setTimeout(BRANCHES_FIRST_RENDER_TIMEOUT));
        return this;
    }

    @Step("Select tab '{label}' and validate the list request")
    public List<BranchLocation> selectTab(String label, List<String> expectedFilter) {
        Response response = waitForListResponse(expectedFilter, NO_KEYWORD, () -> branchesPage.tab(label).click());
        assertThat(branchesPage.tab(label)).hasClass(ACTIVE_CLASS);
        return validateListResponse(response, expectedFilter, NO_KEYWORD);
    }

    @Step("Search location '{keyword}' and validate the list request")
    public List<BranchLocation> searchLocation(String keyword, List<String> expectedFilter) {
        Response response = waitForListResponse(expectedFilter, keyword, () -> branchesPage.searchInput.fill(keyword));
        assertThat(branchesPage.searchInput).hasValue(keyword);
        return validateListResponse(response, expectedFilter, keyword);
    }

    @Step("Turn on '{label}' filter and validate the list request")
    public List<BranchLocation> toggleChip(String label, List<String> expectedFilter, String keyword) {
        Response response = waitForListResponse(expectedFilter, keyword, () -> branchesPage.chip(label).click());
        assertThat(branchesPage.chipCheckbox(label)).isChecked();
        return validateListResponse(response, expectedFilter, keyword);
    }

    @Step("Check every result is of type '{type}'")
    public BranchesSteps checkAllOfType(List<BranchLocation> items, String type) {
        Assert.assertFalse(items.isEmpty(), "Response should contain at least one location");
        items.forEach(item -> Assert.assertEquals(item.getType(), type,
                "Unexpected type for " + item.getName()));
        return this;
    }

    @Step("Check every branch has working hours or works 24/7")
    public BranchesSteps checkWorkingHours(List<BranchLocation> items) {
        for (BranchLocation item : items) {
            if (item.isWorksallday()) continue;
            Assert.assertFalse(item.getWorkHours() == null || item.getWorkHours().isEmpty(),
                    item.getName() + " has neither working hours nor 24/7");
            item.getWorkHours().forEach(h -> {
                Assert.assertTrue(TIME_HH_MM.matcher(h.getStartTime()).matches(), item.getName() + " start time: " + h.getStartTime());
                Assert.assertTrue(TIME_HH_MM.matcher(h.getEndTime()).matches(), item.getName() + " end time: " + h.getEndTime());
            });
        }
        return this;
    }

    @Step("Check every result works 24/7")
    public BranchesSteps checkAllWorkAllDay(List<BranchLocation> items) {
        Assert.assertFalse(items.isEmpty(), "Response should contain at least one 24/7 location");
        items.forEach(item -> Assert.assertTrue(item.isWorksallday(), item.getName() + " is not 24/7"));
        branchesPage.resultDescriptions.all().forEach(d -> assertThat(d).containsText(TWENTY_FOUR_SEVEN_TEXT));
        return this;
    }

    @Step("Check the list on the page matches the API response")
    public BranchesSteps checkListMatchesResponse(List<BranchLocation> items, String expectedIcon) {
        assertThat(branchesPage.resultItems).hasCount(items.size());
        List<String> titles = branchesPage.resultTitles.allInnerTexts().stream().map(t -> clean(t)).toList();
        for (BranchLocation item : items) {
            String address = clean(item.getAddress());
            Assert.assertTrue(titles.stream().anyMatch(t -> t.contains(address)),
                    "Address '" + address + "' from the API is not shown on the page");
        }
        branchesPage.resultIcons.all().forEach(icon -> assertThat(icon).hasText(expectedIcon));
        return this;
    }

    @Step("Check every result contains '{keyword}'")
    public BranchesSteps checkResultsContain(String keyword) {
        assertThat(branchesPage.resultTitles.first()).isVisible();
        branchesPage.resultTitles.all().forEach(t -> assertThat(t).containsText(keyword));
        return this;
    }

    private Response waitForListResponse(List<String> expectedFilter, String expectedKeyword, Runnable action) {
        return page.waitForResponse(
                r -> r.url().equals(BRANCHES_LIST_URL)
                        && POST.equals(r.request().method())
                        && expectedFilter.equals(readRequest(r).getFilter())
                        && expectedKeyword.equals(readRequest(r).getKeyword()),
                action);
    }

    private List<BranchLocation> validateListResponse(Response response, List<String> expectedFilter, String expectedKeyword) {
        BranchesRequest request = readRequest(response);
        Allure.addAttachment("Request " + response.request().method() + " " + response.url(), response.request().postData());

        Assert.assertEquals(response.url(), BRANCHES_LIST_URL, "Endpoint");
        Assert.assertEquals(response.request().method(), POST, "HTTP method");
        Assert.assertEquals(response.status(), STATUS_OK, "Status");
        Assert.assertEquals(request.getFilter(), expectedFilter, "Request filter");
        Assert.assertEquals(request.getKeyword(), expectedKeyword, "Request keyword");
        Assert.assertEquals(request.getLocale(), EN_API_LOCALE, "Request locale");

        String body = response.text();
        Allure.addAttachment("Response " + response.status(), "application/json", body, ".json");
        try {
            return MAPPER.readValue(body, new TypeReference<List<BranchLocation>>() {});
        } catch (Exception e) {
            throw new AssertionError("Response is not a list of locations: " + body, e);
        }
    }

    private static BranchesRequest readRequest(Response response) {
        try {
            String postData = response.request().postData();
            return postData == null ? new BranchesRequest() : MAPPER.readValue(postData, BranchesRequest.class);
        } catch (Exception e) {
            return new BranchesRequest();
        }
    }
}
