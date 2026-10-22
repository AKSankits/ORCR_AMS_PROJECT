package api;

import base.BaseTest;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ApiTest extends BaseTest {

    private static final String BASE_URI = "https://jsonplaceholder.typicode.com";

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = BASE_URI;
    }

    @BeforeMethod
    public void createReportNode(Method method) {
        Test annotation = method.getAnnotation(Test.class);
        String desc = (annotation != null && !annotation.description().isEmpty())
                ? annotation.description()
                : method.getName();
        ExtentTest node = getExtent().createTest("<b>[API]</b> " + method.getName()
                + " <i style='color:#555;font-size:12px;'> — " + desc + "</i>");
        setCurrentTest(node);
    }

    // helper — captures request+response as string and logs to report
    private RequestSpecification req() {
        return given().contentType(ContentType.JSON);
    }

    private void logPass(String endpoint, String method, int status, String responseBody) {
        ExtentTest t = getCurrentTest();
        if (t == null) return;
        t.log(Status.INFO,
                "<b>Request:</b> " + method + " " + BASE_URI + endpoint);
        t.log(Status.INFO,
                "<b>Response Status:</b> " + status);
        t.log(Status.INFO,
                "<pre>" + responseBody + "</pre>");
        t.pass("PASSED");
    }

    private void logFail(String endpoint, String method, int status, String responseBody, Throwable e) {
        ExtentTest t = getCurrentTest();
        if (t == null) return;
        t.log(Status.INFO,
                "<b>Request:</b> " + method + " " + BASE_URI + endpoint);
        t.log(Status.INFO,
                "<b>Response Status:</b> " + status);
        t.log(Status.INFO,
                "<pre>" + responseBody + "</pre>");
        t.fail(e);
    }

    @AfterClass
    public void flushReport() {
        if (getExtent() != null) getExtent().flush();
    }

    // ─────────────────────────────────────────────
    // GET TESTS
    // ─────────────────────────────────────────────

    @Test(priority = 1, description = "GET all quotes — status 200, list non-empty")
    public void getAllQuotes_returns200AndNonEmptyList() {
        Response res = req().when().get("/posts").then().extract().response();
        try {
            res.then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", not(empty()))
                .body("size()", greaterThan(0));
            logPass("/posts", "GET", res.statusCode(), "Array size: " + res.jsonPath().getList("$").size());
        } catch (AssertionError e) {
            logFail("/posts", "GET", res.statusCode(), res.asPrettyString(), e);
            throw e;
        }
    }

    @Test(priority = 2, description = "GET single quote by ID — status 200, required fields present")
    public void getQuoteById_returns200AndRequiredFields() {
        Response res = req().when().get("/posts/1").then().extract().response();
        try {
            res.then()
                .statusCode(200)
                .body("id",     equalTo(1))
                .body("userId", notNullValue())
                .body("title",  not(emptyOrNullString()))
                .body("body",   not(emptyOrNullString()));
            logPass("/posts/1", "GET", res.statusCode(), res.asPrettyString());
        } catch (AssertionError e) {
            logFail("/posts/1", "GET", res.statusCode(), res.asPrettyString(), e);
            throw e;
        }
    }

    @Test(priority = 3, description = "GET quote — verify exact field values for quote ID 1")
    public void getQuoteById_verifyExactFieldValues() {
        Response res = req().when().get("/posts/1").then().statusCode(200).extract().response();
        try {
            int id       = res.jsonPath().getInt("id");
            int userId   = res.jsonPath().getInt("userId");
            String title = res.jsonPath().getString("title");
            assert id == 1     : "Expected id=1, got " + id;
            assert userId == 1 : "Expected userId=1, got " + userId;
            assert title != null && !title.isEmpty() : "title should not be empty";
            logPass("/posts/1", "GET", res.statusCode(),
                    "id=" + id + " | userId=" + userId + " | title=" + title);
        } catch (AssertionError e) {
            logFail("/posts/1", "GET", res.statusCode(), res.asPrettyString(), e);
            throw e;
        }
    }

    @Test(priority = 4, description = "GET policyholder details — status 200, contact fields present")
    public void getPolicyholder_returns200AndContactFields() {
        Response res = req().when().get("/users/1").then().extract().response();
        try {
            res.then()
                .statusCode(200)
                .body("id",           equalTo(1))
                .body("name",         not(emptyOrNullString()))
                .body("email",        not(emptyOrNullString()))
                .body("phone",        not(emptyOrNullString()))
                .body("address",      notNullValue())
                .body("address.city", not(emptyOrNullString()))
                .body("company",      notNullValue());
            logPass("/users/1", "GET", res.statusCode(), res.asPrettyString());
        } catch (AssertionError e) {
            logFail("/users/1", "GET", res.statusCode(), res.asPrettyString(), e);
            throw e;
        }
    }

    @Test(priority = 5, description = "GET non-existent quote — status 404")
    public void getQuoteById_nonExistentId_returns404() {
        Response res = req().when().get("/posts/99999").then().extract().response();
        try {
            res.then().statusCode(404);
            logPass("/posts/99999", "GET", res.statusCode(), res.asPrettyString());
        } catch (AssertionError e) {
            logFail("/posts/99999", "GET", res.statusCode(), res.asPrettyString(), e);
            throw e;
        }
    }

    // ─────────────────────────────────────────────
    // POST TESTS
    // ─────────────────────────────────────────────

    @Test(priority = 6, description = "POST create quote — status 201, response echoes sent fields")
    public void createQuote_returns201AndEchosFields() {
        String body = "{\"userId\":1,\"title\":\"Vehicle Insurance Quote - Honda City\","
                    + "\"body\":\"Comprehensive plan for Honda City 2022, IDV 8,00,000\"}";
        Response res = req().body(body).when().post("/posts").then().extract().response();
        try {
            res.then()
                .statusCode(201)
                .body("id",     notNullValue())
                .body("userId", equalTo(1))
                .body("title",  equalTo("Vehicle Insurance Quote - Honda City"))
                .body("body",   containsString("Honda City"));
            logPass("/posts", "POST", res.statusCode(), res.asPrettyString());
        } catch (AssertionError e) {
            logFail("/posts", "POST", res.statusCode(), res.asPrettyString(), e);
            throw e;
        }
    }

    @Test(priority = 7, description = "POST create quote — response contains auto-generated id")
    public void createQuote_responseContainsGeneratedId() {
        String body = "{\"userId\":2,\"title\":\"Health Insurance Quote - Family Floater\","
                    + "\"body\":\"Family floater plan covering 4 members, sum insured 10,00,000\"}";
        Response res = req().body(body).when().post("/posts").then().statusCode(201).extract().response();
        try {
            int generatedId = res.jsonPath().getInt("id");
            assert generatedId > 0 : "Expected positive id, got " + generatedId;
            logPass("/posts", "POST", res.statusCode(), "Generated id=" + generatedId);
        } catch (AssertionError e) {
            logFail("/posts", "POST", res.statusCode(), res.asPrettyString(), e);
            throw e;
        }
    }

    @Test(priority = 8, description = "POST create quote — response Content-Type is application/json")
    public void createQuote_responseHasJsonContentType() {
        String body = "{\"userId\":3,\"title\":\"Term Life Insurance Quote\","
                    + "\"body\":\"20-year term plan, sum assured 50,00,000\"}";
        Response res = req().body(body).when().post("/posts").then().extract().response();
        try {
            res.then()
                .statusCode(201)
                .header("Content-Type", containsString("application/json"));
            logPass("/posts", "POST", res.statusCode(),
                    "Content-Type: " + res.header("Content-Type"));
        } catch (AssertionError e) {
            logFail("/posts", "POST", res.statusCode(), res.asPrettyString(), e);
            throw e;
        }
    }

    // ─────────────────────────────────────────────
    // INVALID INPUT TESTS
    // ─────────────────────────────────────────────

    @Test(priority = 9, description = "GET with invalid string ID — status 404")
    public void getQuote_invalidStringId_returns404() {
        Response res = req().when().get("/posts/invalid-id").then().extract().response();
        try {
            res.then().statusCode(404);
            logPass("/posts/invalid-id", "GET", res.statusCode(), res.asPrettyString());
        } catch (AssertionError e) {
            logFail("/posts/invalid-id", "GET", res.statusCode(), res.asPrettyString(), e);
            throw e;
        }
    }

    @Test(priority = 10, description = "POST empty body — sandbox returns 201 (real API would return 400)")
    public void createQuote_emptyBody_sandboxStillResponds() {
        Response res = req().body("{}").when().post("/posts").then().extract().response();
        try {
            res.then()
                .statusCode(201)
                .body("id", notNullValue());
            logPass("/posts", "POST {}", res.statusCode(), res.asPrettyString());
        } catch (AssertionError e) {
            logFail("/posts", "POST {}", res.statusCode(), res.asPrettyString(), e);
            throw e;
        }
    }

    @Test(priority = 11, description = "POST missing userId — sandbox accepts, real API would return 400")
    public void createQuote_missingUserId_sandboxBehaviour() {
        String body = "{\"title\":\"Quote without userId\",\"body\":\"Missing policyholder reference\"}";
        Response res = req().body(body).when().post("/posts").then().statusCode(201).extract().response();
        try {
            assert res.jsonPath().getInt("id") > 0 : "Expected id in response";
            logPass("/posts", "POST (no userId)", res.statusCode(), res.asPrettyString());
        } catch (AssertionError e) {
            logFail("/posts", "POST (no userId)", res.statusCode(), res.asPrettyString(), e);
            throw e;
        }
    }

    @Test(priority = 12, description = "GET quotes filtered by userId — every item has userId=1")
    public void getQuotesByUserId_returnsFilteredList() {
        Response res = req().queryParam("userId", 1).when().get("/posts").then().extract().response();
        try {
            res.then()
                .statusCode(200)
                .body("$",      not(empty()))
                .body("userId", everyItem(equalTo(1)));
            logPass("/posts?userId=1", "GET", res.statusCode(),
                    "Returned " + res.jsonPath().getList("$").size() + " quotes for userId=1");
        } catch (AssertionError e) {
            logFail("/posts?userId=1", "GET", res.statusCode(), res.asPrettyString(), e);
            throw e;
        }
    }

    // ─────────────────────────────────────────────
    // PUT TESTS —
    // ─────────────────────────────────────────────

    @Test(priority = 13, description = "PUT update quote — status 200, response reflects updated fields")
    public void updateQuote_returns200AndUpdatedFields() {
        String body = "{\"id\":1,\"userId\":1,"
                + "\"title\":\"Updated Vehicle Insurance Quote - Honda City\","
                + "\"body\":\"Updated comprehensive plan, IDV revised to 9,00,000\"}";
        Response res = req().body(body).when().put("/posts/1").then().extract().response();
        try {
            res.then()
                .statusCode(200)
                .body("id",    equalTo(1))
                .body("title", equalTo("Updated Vehicle Insurance Quote - Honda City"))
                .body("body",  containsString("IDV revised"));
            logPass("/posts/1", "PUT", res.statusCode(), res.asPrettyString());
        } catch (AssertionError e) {
            logFail("/posts/1", "PUT", res.statusCode(), res.asPrettyString(), e);
            throw e;
        }
    }

    @Test(priority = 14, description = "PUT update quote — response Content-Type is application/json")
    public void updateQuote_responseHasJsonContentType() {
        String body = "{\"id\":2,\"userId\":1,"
                + "\"title\":\"Health Insurance Full Update\","
                + "\"body\":\"Full replacement of health insurance quote details\"}";
        Response res = req().body(body).when().put("/posts/2").then().extract().response();
        try {
            res.then()
                .statusCode(200)
                .header("Content-Type", containsString("application/json"));
            logPass("/posts/2", "PUT", res.statusCode(),
                    "Content-Type: " + res.header("Content-Type"));
        } catch (AssertionError e) {
            logFail("/posts/2", "PUT", res.statusCode(), res.asPrettyString(), e);
            throw e;
        }
    }

    // ─────────────────────────────────────────────
    // PATCH TESTS —
    // ─────────────────────────────────────────────

    @Test(priority = 15, description = "PATCH partial update quote — status 200, only patched field changes")
    public void partialUpdateQuote_returns200AndPatchedField() {
        String body = "{\"title\":\"Patched Title - Term Life Insurance\"}";
        Response res = req().body(body).when().patch("/posts/1").then().extract().response();
        try {
            res.then()
                .statusCode(200)
                .body("title", equalTo("Patched Title - Term Life Insurance"));
            logPass("/posts/1", "PATCH", res.statusCode(), res.asPrettyString());
        } catch (AssertionError e) {
            logFail("/posts/1", "PATCH", res.statusCode(), res.asPrettyString(), e);
            throw e;
        }
    }

    @Test(priority = 16, description = "PATCH partial update — id field remains unchanged after patch")
    public void partialUpdateQuote_idRemainsUnchanged() {
        String body = "{\"body\":\"Patched body — only this field should change\"}";
        Response res = req().body(body).when().patch("/posts/1").then().extract().response();
        try {
            res.then()
                .statusCode(200)
                .body("id", equalTo(1));
            logPass("/posts/1", "PATCH", res.statusCode(),
                    "id still = " + res.jsonPath().getInt("id"));
        } catch (AssertionError e) {
            logFail("/posts/1", "PATCH", res.statusCode(), res.asPrettyString(), e);
            throw e;
        }
    }

    // ─────────────────────────────────────────────
    // DELETE TESTS —
    // ─────────────────────────────────────────────

    @Test(priority = 17, description = "DELETE quote — status 200 and response body is empty object")
    public void deleteQuote_returns200AndEmptyBody() {
        Response res = req().when().delete("/posts/1").then().extract().response();
        try {
            res.then().statusCode(200);
            // Sandbox returns {} with optional whitespace — strip and check it has no real fields
            String stripped = res.asPrettyString().replaceAll("\\s", "");
            assert stripped.equals("{}") : "Expected empty body after DELETE, got: " + res.asPrettyString();
            logPass("/posts/1", "DELETE", res.statusCode(),
                    "Response body after delete: " + res.asPrettyString());
        } catch (AssertionError e) {
            logFail("/posts/1", "DELETE", res.statusCode(), res.asPrettyString(), e);
            throw e;
        }
    }

    @Test(priority = 18, description = "DELETE quote — response Content-Type is application/json")
    public void deleteQuote_responseHasJsonContentType() {
        Response res = req().when().delete("/posts/2").then().extract().response();
        try {
            res.then()
                .statusCode(200)
                .header("Content-Type", containsString("application/json"));
            logPass("/posts/2", "DELETE", res.statusCode(),
                    "Content-Type: " + res.header("Content-Type"));
        } catch (AssertionError e) {
            logFail("/posts/2", "DELETE", res.statusCode(), res.asPrettyString(), e);
            throw e;
        }
    }

    // ─────────────────────────────────────────────
    // ID CHAINING — POST then PUT then DELETE
    // ─────────────────────────────────────────────

    @Test(priority = 19, description = "ID chaining — POST to create, extract id, PUT to update a known existing id")
    public void createThenUpdateQuote_idChaining() {
        // Step 1: POST — create a new quote, extract generated id (sandbox returns 101+)
        String createBody = "{\"userId\":1,"
                + "\"title\":\"New Quote for ID Chaining Test\","
                + "\"body\":\"This quote will be updated in the next step\"}";
        int createdId = req().body(createBody).when().post("/posts")
                .then().statusCode(201).extract().jsonPath().getInt("id");

        // Step 2: PUT — sandbox only supports PUT on existing IDs (1–100)
        // Use id=1 to demonstrate the update flow with the created id logged
        String updateBody = "{\"id\":1,\"userId\":1,"
                + "\"title\":\"Updated Quote via ID Chaining\","
                + "\"body\":\"This quote was updated using the id from POST response\"}";
        Response res = req().body(updateBody).when().put("/posts/1").then().extract().response();
        try {
            res.then()
                .statusCode(200)
                .body("title", equalTo("Updated Quote via ID Chaining"));
            logPass("/posts/1", "POST→PUT", res.statusCode(),
                    "POST created id=" + createdId + " | PUT updated /posts/1. Response: " + res.asPrettyString());
        } catch (AssertionError e) {
            logFail("/posts/1", "POST→PUT", res.statusCode(), res.asPrettyString(), e);
            throw e;
        }
    }

    @Test(priority = 20, description = "ID chaining — POST to create, extract id, DELETE using that id")
    public void createThenDeleteQuote_idChaining() {
        // Step 1: POST — create a new quote, extract generated id
        String createBody = "{\"userId\":2,"
                + "\"title\":\"Quote to be Deleted\","
                + "\"body\":\"This quote will be deleted in the next step\"}";
        int quoteId = req().body(createBody).when().post("/posts")
                .then().statusCode(201).extract().jsonPath().getInt("id");

        // Step 2: DELETE — delete using the extracted id
        Response res = req().when().delete("/posts/" + quoteId).then().extract().response();
        try {
            res.then().statusCode(200);
            logPass("/posts/" + quoteId, "POST→DELETE", res.statusCode(),
                    "Created id=" + quoteId + " then deleted. Response: " + res.asPrettyString());
        } catch (AssertionError e) {
            logFail("/posts/" + quoteId, "POST→DELETE", res.statusCode(), res.asPrettyString(), e);
            throw e;
        }
    }

    // ─────────────────────────────────────────────
    // HEADERS VALIDATION
    // ─────────────────────────────────────────────

    @Test(priority = 21, description = "Headers validation — GET response has Content-Type, Pragma, Cache-Control")
    public void getQuote_validateResponseHeaders() {
        Response res = req().when().get("/posts/1").then().extract().response();
        try {
            res.then()
                .statusCode(200)
                .header("Content-Type",  containsString("application/json"))
                .header("Pragma",        notNullValue())
                .header("Cache-Control", notNullValue());
            logPass("/posts/1", "GET", res.statusCode(),
                    "Content-Type: " + res.header("Content-Type")
                    + " | Pragma: "       + res.header("Pragma")
                    + " | Cache-Control: " + res.header("Cache-Control"));
        } catch (AssertionError e) {
            logFail("/posts/1", "GET", res.statusCode(), res.asPrettyString(), e);
            throw e;
        }
    }

    @Test(priority = 22, description = "Headers validation — POST response has Content-Type header")
    public void createQuote_validateResponseHeaders() {
        String body = "{\"userId\":1,\"title\":\"Header Validation Quote\","
                + "\"body\":\"Checking response headers on POST\"}";
        Response res = req().body(body).when().post("/posts").then().extract().response();
        try {
            res.then()
                .statusCode(201)
                .header("Content-Type", containsString("application/json"));
            logPass("/posts", "POST", res.statusCode(),
                    "Content-Type: " + res.header("Content-Type"));
        } catch (AssertionError e) {
            logFail("/posts", "POST", res.statusCode(), res.asPrettyString(), e);
            throw e;
        }
    }

    // ─────────────────────────────────────────────
    // AUTHENTICATION HEADER
    // ─────────────────────────────────────────────

    @Test(priority = 23, description = "Auth header — GET with Authorization header, sandbox ignores it, status 200")
    public void getQuote_withAuthorizationHeader() {
        // Sandbox does not enforce auth — this test documents how to send auth header
        // In a real API, missing/invalid token would return 401 Unauthorized
        Response res = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer sandbox-token-no-real-auth-needed")
                .when().get("/posts/1").then().extract().response();
        try {
            res.then()
                .statusCode(200)
                .body("id", equalTo(1));
            logPass("/posts/1", "GET + Auth Header", res.statusCode(),
                    "Auth header sent. Response: " + res.asPrettyString());
        } catch (AssertionError e) {
            logFail("/posts/1", "GET + Auth Header", res.statusCode(), res.asPrettyString(), e);
            throw e;
        }
    }

    @Test(priority = 24, description = "Auth header — POST with Authorization header, sandbox accepts, status 201")
    public void createQuote_withAuthorizationHeader() {
        // Sandbox does not enforce auth — this test documents how to send auth header on POST
        // In a real API, this token would be validated before creating the resource
        String body = "{\"userId\":1,\"title\":\"Auth Header POST Test\","
                + "\"body\":\"Sent with Authorization header\"}";
        Response res = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer sandbox-token-no-real-auth-needed")
                .body(body)
                .when().post("/posts").then().extract().response();
        try {
            res.then()
                .statusCode(201)
                .body("id", notNullValue());
            logPass("/posts", "POST + Auth Header", res.statusCode(), res.asPrettyString());
        } catch (AssertionError e) {
            logFail("/posts", "POST + Auth Header", res.statusCode(), res.asPrettyString(), e);
            throw e;
        }
    }
    @Test(priority = 25, description = "GET post - verify status and ID")
    public void verifyPostStatusAndId() {
        req().when()
                .get("/posts/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1));
    }
}
