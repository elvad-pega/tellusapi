package stepdefinitions;

import io.cucumber.java.en.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class OAuth2Steps {

    private static final String TOKEN_URL = "/prweb/PRRestService/oauth2/v1/token"; // Replace with actual token URL
    private Response response;

    @Given("the OAuth2 token endpoint is available")
    public void the_token_endpoint_is_available() {
        // Optional: Implement a health check if needed
    }

    @When("I request an access token with client_id {string} and client_secret {string}")
    public void i_request_access_token(String clientId, String clientSecret) {
        response = RestAssured
            .given()
                .auth()
                .preemptive()
                .basic(clientId, clientSecret)
                .contentType("application/x-www-form-urlencoded")
                .formParam("grant_type", "client_credentials")
            .when()
                .post(TOKEN_URL)
            .then()
                .extract()
                .response();
        
        // Store the access token in global variable for use in other scenarios
        GlobalStore.accessToken = response.jsonPath().getString("access_token");        
    }

    @Then("I should receive an access token in the response")
    public void i_should_receive_access_token() {
        String accessToken = response.jsonPath().getString("access_token");
        assertThat("Access token should not be null or empty", accessToken, not(emptyOrNullString()));            
    }
}
