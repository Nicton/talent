package com.slotegrator.assignment.tests;

import com.slotegrator.assignment.api.PlayersApi;
import com.slotegrator.assignment.model.Player;
import com.slotegrator.assignment.model.PlayerCreateRequest;
import com.slotegrator.assignment.model.PlayerLookupRequest;
import com.slotegrator.assignment.testdata.PlayerFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.slotegrator.assignment.util.PlayerAssertions.assertMatchesRequest;

class PlayersValidationAndSecurityTest extends BaseApiTest {

    @Test
    @DisplayName("POST /api/automationTask/create creates a player matching the request and OpenAPI response schema")
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
        PlayerCreateRequest request = new PlayerCreateRequest(
                valid.currencyCode(), valid.email(), valid.name(), "abc", "abc", valid.surname(), valid.username());

        playersApi.createRaw(request)
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("POST /api/automationTask/create rejects duplicate email")
    void createPlayerRejectsDuplicateEmail() {
        PlayerCreateRequest first = PlayerFactory.validPlayer(4);
        remember(playersApi.create(first));

        PlayerCreateRequest duplicateEmail = PlayerFactory.withEmail(PlayerFactory.validPlayer(5), first.email());

        playersApi.createRaw(duplicateEmail)
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Player endpoints require bearer authentication")
    void protectedEndpointsRejectMissingOrInvalidToken() {
        PlayersApi invalidTokenApi = new PlayersApi("invalid-token");

        invalidTokenApi.getAllRaw().then().statusCode(401);
        invalidTokenApi.getOneRaw(new PlayerLookupRequest("missing@example.test")).then().statusCode(401);
        invalidTokenApi.createRaw(PlayerFactory.validPlayer(6)).then().statusCode(401);
        invalidTokenApi.deleteOneRaw(1).then().statusCode(401);
    }
}
