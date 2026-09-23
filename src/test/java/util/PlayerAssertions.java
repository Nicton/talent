package util;

import io.restassured.response.ValidatableResponse;
import model.Player;
import model.PlayerCreateRequest;

import static org.assertj.core.api.Assertions.assertThat;

public final class PlayerAssertions {
    private PlayerAssertions() {
    }

    public static void assertMatchesRequest(Player actual, PlayerCreateRequest expected) {
        assertThat(actual.id()).as("player id").isNotBlank();
        assertThat(actual.username()).as("username").isEqualTo(expected.username());
        assertThat(actual.email()).as("email").isEqualTo(expected.email());
        assertThat(actual.name()).as("name").isEqualTo(expected.name());
        assertThat(actual.surname()).as("surname").isEqualTo(expected.surname());
    }

    public static void assertUnauthorized(ValidatableResponse response) {
        assertThat(response.extract().statusCode()).as("response status").isEqualTo(401);
    }

    /**
     * Lookup and deletion of an unknown record are reported as a bad request by
     * the current API.
     */
    public static void assertBadRequest(ValidatableResponse response) {
        assertThat(response.extract().statusCode()).as("response status").isEqualTo(400);
    }
}
