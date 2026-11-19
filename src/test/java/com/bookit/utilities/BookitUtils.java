package com.bookit.utilities;

import com.github.javafaker.Faker;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class BookitUtils {
    public static Logger LOG = LogManager.getLogger();

    public static String generateToken(String email, String password) {

        Response response = given()
                .accept(ContentType.JSON)
                .queryParam("email", email)
                .and()
                .queryParam("password", password)
                .when().get(Environment.BASE_URL + "/sign");

        String token = "Bearer " + response.path("accessToken");

        return token;
    }

    //teacher , team-leader , team-member
    public static String generateTokenByRole(String role) {

        //returnCredentials(role);


        String token = given()
                .queryParams(returnCredentials(role))
                .when().get(Environment.BASE_URL + "/sign")
                .then().statusCode(200).extract().response().path("accessToken");


        return "Bearer " + token;

    }

    public static Map<String, String> returnCredentials(String role) {
        String email = "";
        String password = "";

        switch (role) {
            case "teacher":
                email = Environment.TEACHER_EMAIL;
                password = Environment.TEACHER_PASSWORD;
                break;

            case "team-member":
                email = Environment.MEMBER_EMAIL;
                password = Environment.MEMBER_PASSWORD;
                break;
            case "team-leader":
                email = Environment.LEADER_EMAIL;
                password = Environment.LEADER_PASSWORD;
                break;
            default:
                LOG.warn("Invalid Role Entry :\n>> " + role + " <<");
                throw new RuntimeException("Invalid Role Entry :\n>> " + role + " <<");
        }
        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", email);
        credentials.put("password", password);

        LOG.info("Role is "+role);

        // Bad practice
        // LOG.info(credentials);

        return credentials;

    }


    public static Map<String,Object> getRandomStudent(){

        Faker faker = new Faker() ;
        Map<String,Object> student = new LinkedHashMap<>();
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        String email=firstName+lastName+"@library";
        System.out.println(email);
        student.put("first-name", firstName );
        student.put("last-name", lastName );
        student.put("email", email.toLowerCase());
        student.put("password", "bookitUser");
        student.put("role", "student-team-member");
        student.put("campus-location","VA");
        student.put("batch-number", "34");
        student.put("team-name", "Nicee");

        return student ;
    }


}
