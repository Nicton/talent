package com.slotegrator.assignment.tests;

import com.slotegrator.assignment.config.TestConfig;
import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OpenApiContractTest {

    @Test
    @DisplayName("Published OpenAPI document is reachable and describes required Players API endpoints")
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
