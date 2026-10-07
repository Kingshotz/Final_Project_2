package tests.ui;

import api.models.BranchLocation;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.qameta.allure.TmsLink;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import steps.ui.BranchesSteps;

import java.util.List;

import static constants.Constants.BRANCHES_TAB;
import static constants.Constants.BRANCH_FILTER;
import static constants.Constants.BRANCH_ICON;
import static constants.Constants.BRANCH_SEARCH_KEYWORD;
import static constants.Constants.BRANCH_TYPE;
import static constants.Constants.TWENTY_FOUR_SEVEN_CHIP;
import static constants.Constants.TWENTY_FOUR_SEVEN_FILTER;

@TmsLink("SCRUM-T13")
@Epic("TBC Bank Digital Services")
@Feature("ATMs & Branches Finder")
public class BranchesScenario extends BaseTest {
    BranchesSteps branchesSteps;
    List<BranchLocation> locations;

    @BeforeClass
    public void innerSetup() {
        branchesSteps = new BranchesSteps(page);
    }

    @Test(priority = 1, description = "Open ATMs & Branches page from quick actions")
    @Story("Open ATMs & Branches Finder")
    @Severity(SeverityLevel.NORMAL)
    @Description("Opens the quick actions widget on the home page, clicks ATMs & Branches, and validates the finder "
            + "page opens with its title and the location list.")
    public void openBranchesPage() {
        branchesSteps
                .openQuickActions()
                .gotoBranchesPage();

        branchesSteps.checkBranchesPageOpened();
    }

    @Test(priority = 2, description = "Branches tab sends BRANCH filter and shows only branches")
    @Story("Filter Locations by Branch Type")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Clicks the Branches tab while capturing the list request, validates the request sends filter "
            + "[BRANCH] and locale en-US, that the response contains only branches with valid working hours, and "
            + "that the page shows exactly the returned locations.")
    public void filterBranches() {
        locations = branchesSteps.selectTab(BRANCHES_TAB, List.of(BRANCH_FILTER));

        branchesSteps
                .checkAllOfType(locations, BRANCH_TYPE)
                .checkWorkingHours(locations)
                .checkListMatchesResponse(locations, BRANCH_ICON);
    }

    @Test(priority = 3, description = "Search branches by street")
    @Story("Search Branches by Street")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Types 'Rustaveli' in the search box while capturing the list request, validates the request carries "
            + "the keyword, that the page shows exactly the returned locations, and that every result contains the "
            + "street name.")
    public void searchBranchByStreet() {
        locations = branchesSteps.searchLocation(BRANCH_SEARCH_KEYWORD, List.of(BRANCH_FILTER));

        branchesSteps
                .checkAllOfType(locations, BRANCH_TYPE)
                .checkListMatchesResponse(locations, BRANCH_ICON)
                .checkResultsContain(BRANCH_SEARCH_KEYWORD);
    }

    @Test(priority = 4, description = "Combine search with 24/7 filter")
    @Story("Combine Street Search with 24/7 Filter")
    @Severity(SeverityLevel.NORMAL)
    @Description("Turns on the 24/7 chip while capturing the list request, validates the request filter becomes "
            + "[BRANCH, 24H] with the keyword kept, and that only 24/7 branches on the street are shown.")
    public void filterTwentyFourSevenBranches() {
        locations = branchesSteps.toggleChip(TWENTY_FOUR_SEVEN_CHIP,
                List.of(BRANCH_FILTER, TWENTY_FOUR_SEVEN_FILTER), BRANCH_SEARCH_KEYWORD);

        branchesSteps
                .checkAllOfType(locations, BRANCH_TYPE)
                .checkAllWorkAllDay(locations)
                .checkListMatchesResponse(locations, BRANCH_ICON)
                .checkResultsContain(BRANCH_SEARCH_KEYWORD);
    }
}
