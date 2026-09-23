package util;

import io.restassured.response.ValidatableResponse;
import model.Player;
import model.PlayerCreateRequest;

import static org.assertj.core.api.Assertions.assertThat;

public final class PlayerAssertions {
    private PlayerAssertions() {
    }

    public static void assertMatchesRequest(Player actual, PlayerCreateRequest expected) {
        assertThat(actual.id()).as("player id").isPositive();
        assertThat(actual.username()).as("username").isEqualTo(expected.username());
        assertThat(actual.email()).as("email").isEqualTo(expected.email());
        assertThat(actual.name()).as("name").isEqualTo(expected.name());
        assertThat(actual.surname()).as("surname").isEqualTo(expected.surname());
    }

    /**
     * The API contract does not pin an exact status for malformed input, so negative
     * cases only require a client error (4xx) rather than a specific code.
     */
    public static void assertClientError(ValidatableResponse response) {
        assertThat(response.extract().statusCode())
                .as("response should be a 4xx client error")
                .isBetween(400, 499);
    }

    public static void assertUnauthorized(ValidatableResponse response) {
        assertThat(response.extract().statusCode())
                .as("response should be 401 Unauthorized")
                .isEqualTo(401);
    }

    public static void assertNotFound(ValidatableResponse response) {
        assertThat(response.extract().statusCode())
                .as("response should be 404 Not Found")
                .isEqualTo(404);
    }
}
