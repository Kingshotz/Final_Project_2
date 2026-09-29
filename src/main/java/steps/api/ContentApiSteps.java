package steps.api;

import api.models.ProblemDetails;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.testng.Assert;

import static constants.Constants.CONTENT_PAGE_ENDPOINT;
import static constants.Constants.LOCALE_PARAM;
import static constants.Constants.NOT_FOUND_TITLE;
import static constants.Constants.PAGE_ID_PARAM;
import static constants.Constants.PROBLEM_JSON;
import static constants.Constants.STATUS_NOT_FOUND;
import static io.restassured.RestAssured.given;

public class ContentApiSteps extends BaseApiSteps {
    @Step("GET /sites/pages/{pageId}?locale={locale}")
    public Response getPage(String pageId, String locale) {
        return given().spec(SPEC)
                .pathParam(PAGE_ID_PARAM, pageId)
                .queryParam(LOCALE_PARAM, locale)
                .when()
                .get(CONTENT_PAGE_ENDPOINT);
    }

    @Step("Check unknown page returns 404 Object not found")
    public ContentApiSteps checkPageNotFound(Response response, String pageId) {
        checkStatus(response, STATUS_NOT_FOUND);
        Assert.assertTrue(response.contentType().startsWith(PROBLEM_JSON),
                "Content type: " + response.contentType());
        ProblemDetails problem = response.as(ProblemDetails.class);
        Assert.assertEquals(problem.getStatus(), STATUS_NOT_FOUND, "status in body");
        Assert.assertEquals(problem.getTitle(), NOT_FOUND_TITLE, "title in body");
        Assert.assertTrue(problem.getDetail().contains(pageId), "detail should name the page: " + problem.getDetail());
        return this;
    }
}
