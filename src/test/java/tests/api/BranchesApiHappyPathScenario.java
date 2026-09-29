package tests.api;

import api.models.BranchLocation;
import api.models.Region;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.qameta.allure.TmsLink;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import steps.api.BranchesApiSteps;

import java.util.List;

import static constants.Constants.BRANCH_FILTER;
import static constants.Constants.BRANCH_SEARCH_KEYWORD;
import static constants.Constants.BRANCH_TYPE;
import static constants.Constants.EN_API_LOCALE;
import static constants.Constants.NO_KEYWORD;
import static constants.Constants.REGION_TBILISI;
import static constants.Constants.STATUS_OK;
import static constants.Constants.TWENTY_FOUR_SEVEN_FILTER;

@TmsLink("SCRUM-T17")
@Epic("TBC Bank Digital Services")
@Feature("ATMs & Branches REST API")
public class BranchesApiHappyPathScenario {
    BranchesApiSteps branchesApiSteps = new BranchesApiSteps();

    @Test(description = "Branch filter returns only branches with valid data")
    @Story("Get Branch List Filtered by Type")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Sends POST /atmsAndBranches/list with filter [BRANCH] and locale en-US, checks status 200, and "
            + "validates that every returned location is a branch with id, name, address, region, coordinates "
            + "inside Georgia and valid working hours.")
    public void branchFilterReturnsBranches() {
        Response response = branchesApiSteps.getLocations(
                branchesApiSteps.request(List.of(BRANCH_FILTER), EN_API_LOCALE, NO_KEYWORD));
        branchesApiSteps.checkStatus(response, STATUS_OK);

        List<BranchLocation> branches = branchesApiSteps.readLocations(response);
        branchesApiSteps
                .checkLocations(branches, BRANCH_TYPE)
                .checkWorkingHours(branches);
    }

    @Test(description = "Keyword and 24/7 filters narrow the results")
    @Story("Search Branches by Keyword and 24/7 Filter")
    @Severity(SeverityLevel.NORMAL)
    @Description("Sends the branch list request with keyword 'Rustaveli' and validates every result matches it, then "
            + "adds the 24H filter and validates the result is a non-empty subset of the keyword results in which "
            + "every branch works 24/7.")
    public void keywordAnd24hFiltersNarrowResults() {
        Response byKeyword = branchesApiSteps.getLocations(
                branchesApiSteps.request(List.of(BRANCH_FILTER), EN_API_LOCALE, BRANCH_SEARCH_KEYWORD));
        branchesApiSteps.checkStatus(byKeyword, STATUS_OK);
        List<BranchLocation> matching = branchesApiSteps.readLocations(byKeyword);
        branchesApiSteps
                .checkLocations(matching, BRANCH_TYPE)
                .checkLocationsMatch(matching, BRANCH_SEARCH_KEYWORD);

        Response allDay = branchesApiSteps.getLocations(
                branchesApiSteps.request(List.of(BRANCH_FILTER, TWENTY_FOUR_SEVEN_FILTER), EN_API_LOCALE, BRANCH_SEARCH_KEYWORD));
        branchesApiSteps.checkStatus(allDay, STATUS_OK);
        List<BranchLocation> allDayBranches = branchesApiSteps.readLocations(allDay);

        Assert.assertFalse(allDayBranches.isEmpty(), "Expected at least one 24/7 branch on Rustaveli");
        Assert.assertTrue(allDayBranches.size() <= matching.size(), "24H filter should narrow the results");
        allDayBranches.forEach(b -> {
            Assert.assertTrue(b.isWorksallday(), b.getName() + " is not 24/7");
            Assert.assertTrue(matching.stream().anyMatch(m -> m.getId() == b.getId()),
                    b.getName() + " is not in the keyword results");
        });
    }

    @Test(description = "Regions have names, counts and bounding boxes")
    @Story("Get Regions List")
    @Severity(SeverityLevel.NORMAL)
    @Description("Sends GET /atmsAndBranches/regions, checks status 200, and validates that every region has a name, a "
            + "positive location count and a map bounding box whose north-east corner is above and right of the "
            + "south-west corner, with Tbilisi in the list.")
    public void regionsHaveValidBoundingBoxes() {
        Response response = branchesApiSteps.getRegions(EN_API_LOCALE);
        branchesApiSteps.checkStatus(response, STATUS_OK);

        List<Region> regions = branchesApiSteps.readRegions(response);
        branchesApiSteps
                .checkRegions(regions)
                .checkRegionExists(regions, REGION_TBILISI);
    }
}
