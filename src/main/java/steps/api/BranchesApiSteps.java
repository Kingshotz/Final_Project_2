package steps.api;

import api.models.BranchLocation;
import api.models.BranchesRequest;
import api.models.GeoPoint;
import api.models.Region;
import io.qameta.allure.Step;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;

import java.util.List;

import static constants.Constants.BRANCHES_LIST_ENDPOINT;
import static constants.Constants.BRANCHES_REGIONS_ENDPOINT;
import static constants.Constants.GEORGIA_MAX_LATITUDE;
import static constants.Constants.GEORGIA_MAX_LONGITUDE;
import static constants.Constants.GEORGIA_MIN_LATITUDE;
import static constants.Constants.GEORGIA_MIN_LONGITUDE;
import static constants.Constants.LOCALE_PARAM;
import static constants.Constants.NO_LOCATION_LATITUDE;
import static constants.Constants.NO_LOCATION_LONGITUDE;
import static constants.Constants.TIME_HH_MM;
import static io.restassured.RestAssured.given;

public class BranchesApiSteps extends BaseApiSteps {
    public BranchesRequest request(List<String> filter, String locale, String keyword) {
        return BranchesRequest.builder()
                .filter(filter)
                .locale(locale)
                .keyword(keyword)
                .myLocation(new GeoPoint(NO_LOCATION_LATITUDE, NO_LOCATION_LONGITUDE))
                .build();
    }

    @Step("POST /atmsAndBranches/list with filter {request.filter}, keyword '{request.keyword}'")
    public Response getLocations(BranchesRequest request) {
        return given().spec(SPEC)
                .body(request)
                .when()
                .post(BRANCHES_LIST_ENDPOINT);
    }

    @Step("POST /atmsAndBranches/list with raw body {body}")
    public Response getLocationsWithRawBody(String body) {
        return given().spec(SPEC)
                .body(body)
                .when()
                .post(BRANCHES_LIST_ENDPOINT);
    }

    @Step("Read locations from the response")
    public List<BranchLocation> readLocations(Response response) {
        Assert.assertTrue(response.contentType().startsWith(ContentType.JSON.toString()),
                "Content type: " + response.contentType());
        return response.as(new TypeRef<List<BranchLocation>>() {});
    }

    @Step("Check every location is of type '{type}' and has id, name, address and region")
    public BranchesApiSteps checkLocations(List<BranchLocation> locations, String type) {
        Assert.assertFalse(locations.isEmpty(), "Expected at least one location");
        for (BranchLocation location : locations) {
            Assert.assertEquals(location.getType(), type, "Type of " + location.getName());
            Assert.assertTrue(location.getId() > 0, "Id of " + location.getName());
            Assert.assertFalse(isBlank(location.getName()), "Name is empty for id " + location.getId());
            Assert.assertFalse(isBlank(location.getAddress()), "Address is empty for " + location.getName());
            Assert.assertFalse(isBlank(location.getRegionName()), "Region is empty for " + location.getName());
            Assert.assertTrue(location.getLatitude() > GEORGIA_MIN_LATITUDE && location.getLatitude() < GEORGIA_MAX_LATITUDE,
                    "Latitude of " + location.getName());
            Assert.assertTrue(location.getLongitude() > GEORGIA_MIN_LONGITUDE && location.getLongitude() < GEORGIA_MAX_LONGITUDE,
                    "Longitude of " + location.getName());
        }
        return this;
    }

    @Step("Check every location works 24/7 or has valid working hours")
    public BranchesApiSteps checkWorkingHours(List<BranchLocation> locations) {
        for (BranchLocation location : locations) {
            if (location.isWorksallday()) continue;
            Assert.assertFalse(location.getWorkHours() == null || location.getWorkHours().isEmpty(),
                    location.getName() + " has neither working hours nor 24/7");
            location.getWorkHours().forEach(hours -> {
                Assert.assertFalse(isBlank(hours.getWeekDay()), location.getName() + " weekDay");
                Assert.assertTrue(TIME_HH_MM.matcher(hours.getStartTime()).matches(), location.getName() + " startTime " + hours.getStartTime());
                Assert.assertTrue(TIME_HH_MM.matcher(hours.getEndTime()).matches(), location.getName() + " endTime " + hours.getEndTime());
                Assert.assertTrue(hours.getStartTime().compareTo(hours.getEndTime()) < 0,
                        location.getName() + " opens after it closes: " + hours.getStartTime() + "-" + hours.getEndTime());
            });
        }
        return this;
    }

    @Step("Check every location matches '{keyword}'")
    public BranchesApiSteps checkLocationsMatch(List<BranchLocation> locations, String keyword) {
        Assert.assertFalse(locations.isEmpty(), "Expected at least one location for '" + keyword + "'");
        String lower = keyword.toLowerCase();
        locations.forEach(location -> Assert.assertTrue(
                (location.getAddress() + " " + location.getName()).toLowerCase().contains(lower),
                location.getName() + " / " + location.getAddress() + " does not match '" + keyword + "'"));
        return this;
    }

    @Step("GET /atmsAndBranches/regions?locale={locale}")
    public Response getRegions(String locale) {
        return given().spec(SPEC)
                .queryParam(LOCALE_PARAM, locale)
                .when()
                .get(BRANCHES_REGIONS_ENDPOINT);
    }

    @Step("Read regions from the response")
    public List<Region> readRegions(Response response) {
        return response.as(new TypeRef<List<Region>>() {});
    }

    @Step("Check every region has a name, a positive count and a valid bounding box")
    public BranchesApiSteps checkRegions(List<Region> regions) {
        Assert.assertFalse(regions.isEmpty(), "Expected at least one region");
        for (Region region : regions) {
            Assert.assertFalse(isBlank(region.getName()), "Region without a name");
            Assert.assertTrue(region.getCount() > 0, region.getName() + " count " + region.getCount());
            Assert.assertNotNull(region.getCoordinates(), region.getName() + " has no coordinates");
            GeoPoint northEast = region.getCoordinates().getNorthEast();
            GeoPoint southWest = region.getCoordinates().getSouthWest();
            Assert.assertNotNull(northEast, region.getName() + " northEast");
            Assert.assertNotNull(southWest, region.getName() + " southWest");
            Assert.assertTrue(northEast.getLatitude() >= southWest.getLatitude(), region.getName() + " latitude box");
            Assert.assertTrue(northEast.getLongitude() >= southWest.getLongitude(), region.getName() + " longitude box");
        }
        return this;
    }

    @Step("Check the region list contains '{name}'")
    public BranchesApiSteps checkRegionExists(List<Region> regions, String name) {
        Assert.assertTrue(regions.stream().anyMatch(r -> name.equals(r.getName())),
                "Region '" + name + "' not found");
        return this;
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
