package api;

import model.Credentials;
import model.Token;
import io.restassured.response.Response;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;

public class AuthApi extends ApiClient {
    private static final String LOGIN_PATH = "/api/tester/login";

    public Response loginRaw(Credentials credentials) {
        return givenAnonymous()
                .body(credentials)
                .when()
                .post(LOGIN_PATH);
    }

    public Token login(Credentials credentials) {
        Response response = loginRaw(credentials)
                .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/token.schema.json"))
                .extract().response();

        Token token = response.as(Token.class);
        assertThat(token.accessToken()).as("access token").isNotBlank();
        assertThat(token.tokenType()).as("token type").isNotBlank();
        return token;
    }
}
