package stepdefinitions;

import io.cucumber.java.en.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Map;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;

public class Incident {

    private static final String API_URL = "/prweb/app/tell-us-more-refrence/api/application/v2";    
    private String assignmentURL;    
    private Response response;    

    @Then("the response status code should be {int}")
    public void the_response_status_code_should_be(Integer expectedStatusCode) {
        assertEquals(expectedStatusCode.intValue(), response.getStatusCode());
    }

    @Then("the response should include assignment ID {string}")
    public void the_response_should_include_assignment_id(String expectedId) {        
        String assignmentId = "ASSIGN-WORKLIST SL-TELLUSMOREREF-WORK " + GlobalStore.caseId + "!" + expectedId;
        String actualId = response.jsonPath().getString("nextAssignmentInfo.ID");
        assertThat("Assignment ID should match", actualId, equalTo(assignmentId));
    }

    @Then("the open link href should be {string}")
    public void the_open_link_href_should_be(String expectedHref) {
        String assignmentHref = "/assignments/ASSIGN-WORKLIST SL-TELLUSMOREREF-WORK " + GlobalStore.caseId + "!" + expectedHref;
        String actualHref = response.jsonPath().getString("nextAssignmentInfo.links.open.href");
        assertThat("Open link href should match", actualHref, equalTo(assignmentHref));
    }

    @Given("the Create Case API endpoint is available")
    public void the_Create_Case_API_endpoint_is_available() {
        // Optionally, implement endpoint health check here.
    }

    @When("I create a new incident case")
    public void i_create_a_new_incident_case() {
        String payload = "{\"caseTypeID\":\"SL-TellUsMoreRef-Work-Incident\",\"content\":{},\"processID\":\"pyStartCase\"}";

        String casesURL = API_URL + "/cases?viewType=page";

        response = RestAssured
            .given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + GlobalStore.accessToken)
                .body(payload)
            .when()
                .post(casesURL)
            .then()
                .extract()
                .response();

        // Store the case ID in global variable for use in other scenarios
        GlobalStore.caseId = response.jsonPath().getString("data.caseInfo.content.pyID");        
        GlobalStore.eTag = response.getHeader("eTag");
    }

    @Then("the response should include case ID")
    public void the_response_should_include_case_id() {
        String actualId = response.jsonPath().getString("ID");
        assertThat("Case ID should not be null or empty", actualId, not(emptyOrNullString()));        
    }

    @Then("the response should include next assignment ID {string}")
    public void the_response_should_include_next_assignment_id(String expectedAssignmentId) {
        String nextAssignmentId = "ASSIGN-WORKLIST SL-TELLUSMOREREF-WORK " + GlobalStore.caseId + "!" + expectedAssignmentId;
        String actualAssignmentId = response.jsonPath().getString("nextAssignmentInfo.ID");
        assertThat("Next Assignment ID should match", actualAssignmentId, equalTo(nextAssignmentId));
    }

    @Given("the DetermineCategory API endpoint is available")
    public void the_DetermineCategory_API_endpoint_is_available() {
        // Optionally implement a health check here
    }

    @When("I submit the following incident details:")
    public void i_submit_the_following_incident_details(io.cucumber.datatable.DataTable dataTable) {
        Map<String, String> data = dataTable.asMaps().get(0);

        assignmentURL = API_URL + "/assignments/ASSIGN-WORKLIST%20SL-TELLUSMOREREF-WORK%20" + GlobalStore.caseId + "!CREATEFORM_DEFAULT/actions/DetermineCategory?viewType=page";

        String payload = String.format(
            "{\"content\":{\"IncidentType\":\"%s\",\"IncidentSubType\":\"%s\"},\"pageInstructions\":[]}",
            data.get("IncidentType"), data.get("IncidentSubType")
        );

        response = RestAssured
            .given()
                .contentType(ContentType.JSON)
                .urlEncodingEnabled(false)
                .header("Authorization", "Bearer " + GlobalStore.accessToken)
                .header("if-match", GlobalStore.eTag)
                .body(payload)
            .when()
                .patch(assignmentURL)
            .then()
                .extract()
                .response();

        GlobalStore.eTag = response.getHeader("eTag");
    }

    @Given("the ServiceDetails API endpoint is available")
    public void the_ServiceDetails_API_endpoint_is_available() {
        // Optionally implement a health check here
    }

    @When("I submit the following service details:")
    public void i_submit_the_following_service_details(io.cucumber.datatable.DataTable dataTable) {
        Map<String, String> data = dataTable.asMaps().get(0);
        
        assignmentURL = API_URL + "/assignments/ASSIGN-WORKLIST%20SL-TELLUSMOREREF-WORK%20" + GlobalStore.caseId + "!CREATEFORM_DEFAULT/actions/ServiceDetails?viewType=page";

        String payload = String.format(
            "{\"content\":{" +
                "\"CommunicationChannel\":\"%s\"," +
                "\"What\":\"%s\"," +
                "\"When\":\"%s\"," +
                "\"Where\":\"%s\"," +
                "\"WhereThereAnyOtherPeopleAffected\":%s" +
            "},\"pageInstructions\":[]}",
            data.get("CommunicationChannel"),
            data.get("What"),
            data.get("When"),
            data.get("Where"),
            data.get("WhereThereAnyOtherPeopleAffected")
        );

        response = RestAssured
            .given()
                .contentType(ContentType.JSON)
                .urlEncodingEnabled(false)
                .header("Authorization", "Bearer " + GlobalStore.accessToken)
                .header("if-match", GlobalStore.eTag)
                .body(payload)                
            .when()
                .patch(assignmentURL)
            .then()
                .extract()
                .response();
        
        GlobalStore.eTag = response.getHeader("eTag");
    }

    @Given("the ContactInfo API endpoint is available")
    public void the_ContactInfo_API_endpoint_is_available() {
        // Optionally implement a health check here.
    }

    @When("I submit the following contact details:")
    public void i_submit_the_following_contact_details(io.cucumber.datatable.DataTable dataTable) {
        Map<String, String> data = dataTable.asMaps().get(0);

        assignmentURL = API_URL + "/assignments/ASSIGN-WORKLIST%20SL-TELLUSMOREREF-WORK%20" + GlobalStore.caseId + "!CREATEFORM_DEFAULT/actions/ContactInfo?viewType=page";

        String payload = String.format(
            "{\"content\":{\"Customer\":{" +
                "\"FName\":\"%s\"," +
                "\"LName\":\"%s\"," +
                "\"EMail\":\"%s\"," +
                "\"PhoneNumber\":\"%s\"," +
                "\"AddressMode\":\"%s\"," +
                "\"Address\":{" +
                    "\"pyCountry\":\"%s\"," +
                    "\"pyCity\":\"%s\"," +
                    "\"pyStreet\":\"%s\"," +
                    "\"pyPostalCode\":\"%s\"," +
                    "\"pyState\":\"%s\"" +
                "}" +
            "}},\"pageInstructions\":[]}",
            data.get("FName"),
            data.get("LName"),
            data.get("EMail"),
            data.get("PhoneNumber"),
            data.get("AddressMode"),
            data.get("pyCountry"),
            data.get("pyCity"),
            data.get("pyStreet"),
            data.get("pyPostalCode"),
            data.get("pyState") == null ? "" : data.get("pyState")
        );
        
        response = RestAssured
            .given()
                .contentType(ContentType.JSON)
                .urlEncodingEnabled(false)
                .header("Authorization", "Bearer " + GlobalStore.accessToken)
                .header("if-match", GlobalStore.eTag)
                .body(payload)
            .when()
                .patch(assignmentURL)
            .then()
                .extract()
                .response();
        
        GlobalStore.eTag = response.getHeader("eTag");
    }

    @Given("the Review API endpoint is available")
    public void the_Review_API_endpoint_is_available() {
        // Optionally, implement endpoint health check here.
    }

    @When("I submit review consent as follows:")
    public void i_submit_review_consent_as_follows(io.cucumber.datatable.DataTable dataTable) {
        Map<String, String> data = dataTable.asMaps().get(0);

        assignmentURL = API_URL + "/assignments/ASSIGN-WORKLIST%20SL-TELLUSMOREREF-WORK%20" + GlobalStore.caseId + "!CREATEFORM_DEFAULT/actions/Review?viewType=page";

        String payload = String.format(
            "{\"content\":{\"UserConsent\":%s,\"PrivacyPolicy\":%s},\"pageInstructions\":[]}",
            data.get("UserConsent"),
            data.get("PrivacyPolicy")
        );

        response = RestAssured
            .given()
                .contentType(ContentType.JSON)
                .urlEncodingEnabled(false)
                .header("Authorization", "Bearer " + GlobalStore.accessToken)
                .header("if-match", GlobalStore.eTag)
                .body(payload)
            .when()
                .patch(assignmentURL)
            .then()
                .extract()
                .response();
        
        GlobalStore.eTag = response.getHeader("eTag");
    }    

    @Then("the response should contain confirmation note {string}")
    public void the_response_should_contain_confirmation_note(String expectedNote) {
        String actualNote = response.jsonPath().getString("confirmationNote");
        assertThat("Confirmation note should match", actualNote, equalTo(expectedNote));
    }

    @Then("the getNextWork link href should be {string}")
    public void the_getNextWork_link_href_should_be(String expectedHref) {
        String actualHref = response.jsonPath().getString("confirmationLinks.getNextWork.href");
        assertThat("getNextWork link href should match", actualHref, equalTo(expectedHref));
    }

    @Given("the Cases API endpoint is available")
    public void the_Cases_API_endpoint_is_available() {
        // Write code here that turns the phrase above into concrete actions
    }

    @When("I get the case data")
    public void I_get_the_case_data() {
        String casesURL = API_URL + "/cases/SL-TELLUSMOREREF-WORK%20" + GlobalStore.caseId;

        response = RestAssured
            .given()
                .contentType(ContentType.JSON)
                .urlEncodingEnabled(false)
                .header("Authorization", "Bearer " + GlobalStore.accessToken)                                
            .when()
                .get(casesURL)
            .then()
                .extract()
                .response();
                

        System.out.println(response.asString());
    }

    @Then("the case status is {string}")
    public void the_case_status_is(String expectedStatus) {
        String actualStatus = response.jsonPath().getString("data.caseInfo.content.pyStatusWork");
        assertThat("Case status should match", actualStatus, equalTo(expectedStatus));
    }

    @Then("the case urgency is {string}")
    public void the_case_urgency_is(String expectedUrgency) {
        String actualUrgency = response.jsonPath().getString("data.caseInfo.content.pxUrgencyWork");
        assertThat("Urgency should match", actualUrgency, equalTo(expectedUrgency));
    }

    @Then("the assignment is routed to the work queue is {string}")
    public void the_assignment_is_routed_to_the_work_queue_is(String expectedRouteTo) {
        String actualRouteTo = response.jsonPath().getString("data.caseInfo.content.content.RouteTo");
        assertThat("Routing should match", actualRouteTo, equalTo(expectedRouteTo));
    }
}
