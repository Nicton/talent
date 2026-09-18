package com.slotegrator.assignment.api;

import com.slotegrator.assignment.config.TestConfig;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.ErrorLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class ApiClient {
    protected final RequestSpecification anonymousSpec;

    public ApiClient() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        this.anonymousSpec = new RequestSpecBuilder()
                .setBaseUri(TestConfig.baseUri())
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilter(new ErrorLoggingFilter())
                .build();
    }

    protected RequestSpecification givenAnonymous() {
        return RestAssured.given().spec(anonymousSpec);
    }

    protected RequestSpecification givenBearer(String accessToken) {
        return givenAnonymous().auth().oauth2(accessToken);
    }
}
