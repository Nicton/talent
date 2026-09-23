package tests;

import api.AuthApi;
import config.TestConfig;
import model.Credentials;
import model.Player;
import model.PlayerCreateRequest;
import model.PlayerLookupRequest;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import testdata.PlayerFactory;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * These tests describe how the API is expected to behave. They currently fail
 * against the test account, and every failure is a defect listed in DEFECTS.md.
 *
 * They are excluded from the default run so that the regular suite stays green.
 * Run them explicitly with:
 *
 *   mvn test -DexcludedGroups= -Dgroups=known-issues
 */
@Tag("known-issues")
class PlayersKnownDefectsTest extends BaseApiTest {

    @Test
    @DisplayName("POST /api/tester/login should answer 200 as documented")
    void loginShouldAnswer200() {
        authApi.loginRaw(new Credentials(TestConfig.testerEmail(), TestConfig.testerPassword()))
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("POST /api/tester/login should return the documented token payload")
    void loginShouldReturnTheDocumentedTokenPayload() {
        String body = authApi.loginRaw(new Credentials(TestConfig.testerEmail(), TestConfig.testerPassword()))
                .then()
                .extract().asString();

        assertThat(body)
                .as("documented TokenDTO fields")
                .contains("access_token")
                .contains("token_type")
                .contains("expires_in")
                .contains("scope");
    }

    @Test
    @DisplayName("POST /api/automationTask/create should not echo the password")
    void createShouldNotEchoThePassword() {
        PlayerCreateRequest request = PlayerFactory.validPlayer();
        String body = playersApi.createRaw(request).then().extract().asString();
        remember(playersApi.create(request));

        assertThat(body).as("response body").doesNotContain(request.passwordChange());
    }

    @Test
    @DisplayName("POST /api/automationTask/create should reject missing required fields")
    void createShouldRejectMissingRequiredFields() {
        PlayerCreateRequest request = PlayerFactory.withEmail(
                PlayerFactory.withUsername(PlayerFactory.validPlayer(), null), null);
        var response = playersApi.createRaw(request).then();

        if (response.extract().statusCode() == 201) {
            remember(response.extract().as(Player.class));
        }
        response.statusCode(400);
    }

    @Test
    @DisplayName("POST /api/automationTask/create should reject a duplicate email with 409")
    void createShouldRejectDuplicateEmail() {
        Player first = remember(playersApi.create(PlayerFactory.validPlayer()));
        PlayerCreateRequest duplicateEmail = PlayerFactory.withEmail(PlayerFactory.validPlayer(), first.email());

        playersApi.createRaw(duplicateEmail).then().statusCode(409);
    }

    @Test
    @DisplayName("POST /api/automationTask/getOne should return 404 for an unknown email")
    void getOneShouldReturn404ForUnknownEmail() {
        PlayerLookupRequest request = new PlayerLookupRequest("missing-" + System.nanoTime() + "@example.test");

        playersApi.getOneRaw(request).then().statusCode(404);
    }

    @Test
    @DisplayName("DELETE /api/automationTask/deleteOne/{id} should return 404 for an unknown id")
    void deleteShouldReturn404ForUnknownId() {
        playersApi.deleteOneRaw("missing-" + System.nanoTime()).then().statusCode(404);
    }

    @Test
    @DisplayName("DELETE /api/automationTask/deleteOne/{id} should not delete the same id twice")
    void deleteShouldNotSucceedTwice() {
        Player player = remember(playersApi.create(PlayerFactory.validPlayer()));
        playersApi.deleteOne(player.id());

        playersApi.deleteOneRaw(player.id()).then().statusCode(404);
    }

    @Test
    @DisplayName("Players should not be able to read accounts created by other testers")
    void playersShouldNotReadForeignAccounts() {
        List<Player> existing = playersApi.getAll();
        Assumptions.assumeTrue(!existing.isEmpty(), "no pre-existing players to probe");

        Player foreign = existing.getFirst();

        playersApi.getOneRaw(new PlayerLookupRequest(foreign.email()))
                .then()
                .statusCode(org.hamcrest.Matchers.anyOf(org.hamcrest.Matchers.is(403), org.hamcrest.Matchers.is(404)));
    }
}
