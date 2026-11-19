package com.bookit.step_definitions;

import com.bookit.pages.SelfPage;
import com.bookit.pages.SignInPage;
import com.bookit.utilities.BookitUtils;
import com.bookit.utilities.BrowserUtils;
import com.bookit.utilities.DB_Util;
import com.bookit.utilities.Environment;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.Matchers;
import org.junit.Assert;

import java.util.List;
import java.util.Map;

public class APIStepDefs {

    public Logger LOG= LogManager.getLogger();


    RequestSpecification givenPart=RestAssured.given().log().all();
    Response response;
    ValidatableResponse thenPart;
    String token;

    @Given("I logged in Bookit api as a {string}")
    public void i_logged_in_bookit_api_as_a(String role) {

        token=BookitUtils.generateTokenByRole(role);

        LOG.info("token --> "+token);

        givenPart.header("Authorization",token);

    }
    @Given("request accept type is {string}")
    public void request_accept_type_is(String acceptHeader) {

        givenPart.accept(acceptHeader);

    }
    @When("I sent GET request to {string} endpoint")
    public void i_sent_get_request_to_endpoint(String endpoint) {

        LOG.info("Endpoint ---> "+endpoint);

        response = givenPart.when().get(Environment.BASE_URL + endpoint);
        thenPart = response.then();

        LOG.info(response.prettyPrint());

    }
    @Then("status code should be {int}")
    public void status_code_should_be(int expectedStatusCode) {

        // OPT 1
        System.out.println(response.statusCode());
        // OPT 2
        Assert.assertEquals(expectedStatusCode,response.statusCode());

        LOG.info("Status Code ---> "+response.statusCode());

    }
    @Then("response content type is {string}")
    public void response_content_type_is(String contentType) {

        // OPT 1
        thenPart.contentType(contentType);

        // OPT 2
        Assert.assertEquals(contentType,response.contentType());


    }
    @Then("Each {string} field should not be null")
    public void each_field_should_not_be_null(String path) {

        // OPT 1
        thenPart.body(path, Matchers.everyItem(Matchers.notNullValue()));

        // OPT 2
        List<Object> allData = thenPart.extract().jsonPath().getList(path);
        for (Object eachData : allData) {
            Assert.assertNotNull(eachData);
        }


    }


    /**
     * USER FEATURE
     *
     */
    @Given("request contenttype is {string}")
    public void request_contenttype_is(String contentType) {
        givenPart.contentType(contentType);
    }
    Map<String, Object> randomStudent;
    @Given("I create a random student as a body")
    public void i_create_a_random_student_as_a_body() {
        randomStudent = BookitUtils.getRandomStudent();
        givenPart.queryParams(randomStudent);
        LOG.info("Student body --> "+randomStudent);
    }

    @When("I sent POST request to {string} endpoint")
    public void i_sent_post_request_to_endpoint(String endpoint) {

        response = givenPart.when().post(Environment.BASE_URL + endpoint);
        thenPart = response.then();

        LOG.info(response.prettyPrint());

    }

    JsonPath jp;
    @Then("{string} should be {string}")
    public void should_be(String path, String expectedValue) {

        jp = thenPart.extract().jsonPath();

        String actualValue = jp.getString(path);
        Assert.assertEquals(expectedValue,actualValue);

    }
    Integer entryID;
    @Then("{string} field should not be null")
    public void field_should_not_be_null(String path) {

        entryID = jp.getInt(path);
        Assert.assertNotNull(entryID);

    }


    @Then("created user following information should match with database")
    public void created_user_following_information_should_match_with_database(List<String> fields) {

      String query="select firstname,lastname,email,location,batch_number,name " +
              "from users u " +
              "inner join campus c on u.campus_id=c.id " +
              "inner join team t on t.id=u.team_id " +
              "where u.id="+entryID;

        DB_Util.runQuery(query);

        Map<String, String> actualMap = DB_Util.getRowMap(1);
        LOG.info("DB actual map --> "+ actualMap);

        // DB firstname vs API BODY firstname
        String actFirstName = actualMap.get("firstname");
        String expFirstName = (String) randomStudent.get(fields.get(0));
        Assert.assertEquals(expFirstName,actFirstName);


    }
    @Then("created user should be able to login Bookit UI")
    public void created_user_should_be_able_to_login_bookit_ui() {
        SignInPage signInPage=new SignInPage();
        String email = (String) randomStudent.get("email");
        String password = (String) randomStudent.get("password");

        LOG.info("New User is logged in with follwing credentials");
        LOG.info("Email --> "+email);
        LOG.info("Password --> "+password);

        signInPage.login(email,password);

    }
    @Then("created user name should appear in Self Page")
    public void created_user_name_should_appear_in_self_page() {
        SelfPage selfPage = new SelfPage();
        selfPage.goToSelf();
        BrowserUtils.waitFor(2);

        String actualFullName = selfPage.name.getText();
        String firstname = (String) randomStudent.get("first-name");
        String lastname = (String) randomStudent.get("last-name");
        String expectedFullName=firstname+" "+lastname;

        Assert.assertEquals(expectedFullName,actualFullName);

    }
    @Then("I sent DELETE request to {string} endpoint")
    public void i_sent_delete_request_to_endpoint(String endpoint) {


        response = RestAssured.given().log().all()
                .header("Authorization",token)
                .pathParam("id", entryID)
                .when().delete(Environment.BASE_URL+endpoint);

        LOG.info("User is DELETER "+entryID);

        thenPart = response.then();
    }




}
