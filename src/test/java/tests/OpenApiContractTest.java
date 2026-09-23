package tests;

import config.TestConfig;
import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("contract")
class OpenApiContractTest {

    @Test
    @DisplayName("The published OpenAPI document still describes the endpoint contract used by these tests")
    void openApiDocumentContainsRequiredEndpoints() {
        String specification = RestAssured.given()
                .baseUri(TestConfig.baseUri().toString())
                .accept("application/json")
                .when()
                .get("/assets/swagger/openapi.json")
                .then()
                .statusCode(200)
                .extract().asString();

        assertThat(specification)
                .contains("/api/tester/login")
                .contains("/api/automationTask/create")
                .contains("/api/automationTask/getOne")
                .contains("/api/automationTask/getAll")
                .contains("/api/automationTask/deleteOne/{id}")
                .contains("BearerAuth")
                .contains("BasicAuth");
    }
}
