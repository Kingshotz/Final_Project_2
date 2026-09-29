package steps.api;

import api.models.ProblemDetails;
import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;

import static constants.Constants.ACCEPT_HEADER_VALUE;
import static constants.Constants.API_BASE_URL;
import static constants.Constants.ORIGIN_HEADER;
import static constants.Constants.PROBLEM_JSON;
import static constants.Constants.REFERER_HEADER;
import static constants.Constants.SITE_URL;
import static constants.Constants.USER_AGENT;
import static constants.Constants.USER_AGENT_HEADER;

public abstract class BaseApiSteps {
    protected static final RequestSpecification SPEC = new RequestSpecBuilder()
            .setBaseUri(API_BASE_URL)
            .setContentType(ContentType.JSON)
            .setAccept(ACCEPT_HEADER_VALUE)
            .addHeader(ORIGIN_HEADER, SITE_URL)
            .addHeader(REFERER_HEADER, SITE_URL + "/")
            .addHeader(USER_AGENT_HEADER, USER_AGENT)
            .addFilter(new AllureRestAssured())
            .build();

    @Step("Check status code is {expected}")
    public void checkStatus(Response response, int expected) {
        Assert.assertEquals(response.statusCode(), expected,
                "Unexpected status, body: " + response.asString());
    }

    @Step("Check error body: status {status}, '{field}' error contains '{messagePart}'")
    public ProblemDetails checkValidationError(Response response, int status, String field, String messagePart) {
        Assert.assertTrue(response.contentType().startsWith(PROBLEM_JSON),
                "Content type: " + response.contentType());
        ProblemDetails problem = response.as(ProblemDetails.class);
        Assert.assertEquals(problem.getStatus(), status, "status field in the body");
        Assert.assertNotNull(problem.getErrors(), "errors object is missing");
        Assert.assertTrue(problem.getErrors().containsKey(field),
                "No error for '" + field + "', errors: " + problem.getErrors());
        Assert.assertTrue(problem.getErrors().get(field).stream().anyMatch(m -> m.contains(messagePart)),
                "'" + field + "' errors " + problem.getErrors().get(field) + " do not mention '" + messagePart + "'");
        return problem;
    }
}
