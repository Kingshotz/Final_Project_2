package tests.api;

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
import steps.api.ContentApiSteps;

import java.util.List;

import static constants.Constants.BRANCH_FILTER;
import static constants.Constants.EMPTY_JSON_BODY;
import static constants.Constants.EN_API_LOCALE;
import static constants.Constants.FILTER_ERROR_FIELD;
import static constants.Constants.FILTER_ERROR_MESSAGE;
import static constants.Constants.INVALID_FILTER;
import static constants.Constants.LOCALE_ERROR_FIELD;
import static constants.Constants.LOCALE_ERROR_MESSAGE;
import static constants.Constants.NO_KEYWORD;
import static constants.Constants.STATUS_BAD_REQUEST;
import static constants.Constants.STATUS_OK;
import static constants.Constants.UNKNOWN_KEYWORD;
import static constants.Constants.UNKNOWN_PAGE_ID;

@TmsLink("SCRUM-T18")
@Epic("TBC Bank Digital Services")
@Feature("ATMs & Branches REST API")
public class BranchesApiNegativeScenario {
    BranchesApiSteps branchesApiSteps = new BranchesApiSteps();
    ContentApiSteps contentApiSteps = new ContentApiSteps();

    @Test(description = "Unknown filter value is rejected")
    @Story("Reject Unknown Location Type Filter")
    @Severity(SeverityLevel.NORMAL)
    @Description("Sends the branch list request with filter [INVALID] and validates a 400 problem+json response whose "
            + "Filter[0] error lists the allowed filter values.")
    public void unknownFilterIsRejected() {
        Response response = branchesApiSteps.getLocations(
                branchesApiSteps.request(List.of(INVALID_FILTER), EN_API_LOCALE, NO_KEYWORD));
        branchesApiSteps.checkStatus(response, STATUS_BAD_REQUEST);
        branchesApiSteps.checkValidationError(response, STATUS_BAD_REQUEST, FILTER_ERROR_FIELD, FILTER_ERROR_MESSAGE);
    }

    @Test(description = "Request without locale is rejected")
    @Story("Reject Branch List Request Without Locale")
    @Severity(SeverityLevel.NORMAL)
    @Description("Sends the branch list request with an empty JSON body and validates a 400 problem+json response with "
            + "a Locale format error.")
    public void missingLocaleIsRejected() {
        Response response = branchesApiSteps.getLocationsWithRawBody(EMPTY_JSON_BODY);
        branchesApiSteps.checkStatus(response, STATUS_BAD_REQUEST);
        branchesApiSteps.checkValidationError(response, STATUS_BAD_REQUEST, LOCALE_ERROR_FIELD, LOCALE_ERROR_MESSAGE);
    }

    @Test(description = "Keyword without matches returns an empty list")
    @Story("Search With Keyword That Matches No Location")
    @Severity(SeverityLevel.MINOR)
    @Description("Sends the branch list request with a keyword no location contains and validates the service answers "
            + "200 with an empty list instead of an error.")
    public void unknownKeywordReturnsEmptyList() {
        Response response = branchesApiSteps.getLocations(
                branchesApiSteps.request(List.of(BRANCH_FILTER), EN_API_LOCALE, UNKNOWN_KEYWORD));
        branchesApiSteps.checkStatus(response, STATUS_OK);
        Assert.assertTrue(branchesApiSteps.readLocations(response).isEmpty(), "Expected no locations");
    }

    @Test(description = "Unknown content page returns 404")
    @Story("Request Content Page That Does Not Exist")
    @Severity(SeverityLevel.NORMAL)
    @Description("Sends GET /sites/pages/{pageId} with a page id that does not exist and validates a 404 problem+json "
            + "response titled 'Object not found' that names the requested page id.")
    public void unknownContentPageReturns404() {
        Response response = contentApiSteps.getPage(UNKNOWN_PAGE_ID, EN_API_LOCALE);
        contentApiSteps.checkPageNotFound(response, UNKNOWN_PAGE_ID);
    }
}
