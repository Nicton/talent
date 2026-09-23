package tests;

import api.PlayersApi;
import model.Player;
import model.PlayerCreateRequest;
import model.PlayerLookupRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import testdata.PlayerFactory;

import static util.PlayerAssertions.assertMatchesRequest;

class PlayersValidationAndSecurityTest extends BaseApiTest {

    @Test
    @DisplayName("POST /api/automationTask/create creates a player matching the request and schema")
    void createPlayerReturnsDocumentedResponse() {
        PlayerCreateRequest request = PlayerFactory.validPlayer(1);

        Player player = remember(playersApi.create(request));

        assertMatchesRequest(player, request);
    }

    @Test
    @DisplayName("POST /api/automationTask/create validates username min length")
    void createPlayerRejectsTooShortUsername() {
        PlayerCreateRequest request = PlayerFactory.withUsername(PlayerFactory.validPlayer(2), "abc");

        playersApi.createRaw(request)
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("POST /api/automationTask/create validates password min length")
    void createPlayerRejectsTooShortPassword() {
        PlayerCreateRequest valid = PlayerFactory.validPlayer(3);
        PlayerCreateRequest request = PlayerFactory.withPassword(valid, "abc", "abc");

        playersApi.createRaw(request)
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("POST /api/automationTask/create validates password confirmation")
    void createPlayerRejectsPasswordConfirmationMismatch() {
        PlayerCreateRequest valid = PlayerFactory.validPlayer(4);
        PlayerCreateRequest request = PlayerFactory.withPassword(valid, valid.passwordChange(), valid.passwordChange() + "x");

        playersApi.createRaw(request)
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("POST /api/automationTask/create validates required email")
    void createPlayerRejectsMissingEmail() {
        PlayerCreateRequest request = PlayerFactory.withEmail(PlayerFactory.validPlayer(5), null);

        playersApi.createRaw(request)
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("POST /api/automationTask/create validates email format")
    void createPlayerRejectsInvalidEmailFormat() {
        PlayerCreateRequest request = PlayerFactory.withEmail(PlayerFactory.validPlayer(6), "not-an-email");

        playersApi.createRaw(request)
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("POST /api/automationTask/create rejects duplicate email")
    void createPlayerRejectsDuplicateEmail() {
        PlayerCreateRequest first = PlayerFactory.validPlayer(7);
        remember(playersApi.create(first));

        PlayerCreateRequest duplicateEmail = PlayerFactory.withEmail(PlayerFactory.validPlayer(8), first.email());

        playersApi.createRaw(duplicateEmail)
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("POST /api/automationTask/getOne returns not found for unknown email")
    void getOneRejectsUnknownPlayer() {
        playersApi.getOneRaw(new PlayerLookupRequest("missing-" + System.nanoTime() + "@example.test"))
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("DELETE /api/automationTask/deleteOne/{id} returns not found for unknown id")
    void deleteRejectsUnknownPlayerId() {
        playersApi.deleteOneRaw(Integer.MAX_VALUE)
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Player endpoints require bearer authentication")
    void protectedEndpointsRejectInvalidToken() {
        PlayersApi invalidTokenApi = new PlayersApi("invalid-token");

        invalidTokenApi.getAllRaw().then().statusCode(401);
        invalidTokenApi.getOneRaw(new PlayerLookupRequest("missing@example.test")).then().statusCode(401);
        invalidTokenApi.createRaw(PlayerFactory.validPlayer(9)).then().statusCode(401);
        invalidTokenApi.deleteOneRaw(1).then().statusCode(401);
    }
}
