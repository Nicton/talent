package tests;

import model.Player;
import model.PlayerCreateRequest;
import model.PlayerLookupRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import testdata.PlayerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static util.PlayerAssertions.assertBadRequest;
import static util.PlayerAssertions.assertMatchesRequest;

/**
 * The test account API currently stores whatever it receives: it does not enforce
 * field rules and does not reject duplicate emails. The tests below pin that
 * behaviour so that a future change in the validation logic is noticed.
 */
@Tag("validation")
class PlayersValidationTest extends BaseApiTest {

    @Test
    @DisplayName("POST /api/automationTask/create returns the created player and matches the response schema")
    void createPlayerReturnsDocumentedResponse() {
        PlayerCreateRequest request = PlayerFactory.validPlayer();

        Player player = remember(playersApi.create(request));

        assertMatchesRequest(player, request);
    }

    @Test
    @DisplayName("POST /api/automationTask/create keeps every provided field value")
    void createPlayerStoresProvidedValues() {
        PlayerCreateRequest valid = PlayerFactory.validPlayer();
        String password = "P@ssw0rd!#$%^&*()";
        PlayerCreateRequest request = PlayerFactory.withPassword(valid, password, password);
        request = PlayerFactory.withUsername(request, "qa_user-01!");

        Player player = remember(playersApi.create(request));

        assertThat(player.username()).isEqualTo("qa_user-01!");
        assertThat(player.email()).isEqualTo(request.email());
    }

    @Test
    @DisplayName("POST /api/automationTask/create accepts values at the edge of the field rules")
    void createPlayerAcceptsBoundaryValues() {
        PlayerCreateRequest valid = PlayerFactory.validPlayer();
        String longUsername = "qa" + "x".repeat(120);
        String longPassword = "P" + "a".repeat(120) + "1!";
        PlayerCreateRequest request = PlayerFactory.withPassword(
                PlayerFactory.withUsername(valid, longUsername), longPassword, longPassword);

        Player player = remember(playersApi.create(request));

        assertThat(player.username()).isEqualTo(longUsername);
    }

    @Test
    @DisplayName("POST /api/automationTask/create accepts a repeated email")
    void createPlayerAcceptsDuplicateEmail() {
        Player first = remember(playersApi.create(PlayerFactory.validPlayer()));

        PlayerCreateRequest duplicateEmail = PlayerFactory.withEmail(PlayerFactory.validPlayer(), first.email());

        Player second = remember(playersApi.create(duplicateEmail));

        assertThat(second.email()).isEqualTo(first.email());
        assertThat(second.id()).isNotEqualTo(first.id());
    }

    @Test
    @DisplayName("POST /api/automationTask/getOne reports an unknown email as a bad request")
    void getOneRejectsUnknownPlayer() {
        PlayerLookupRequest request = new PlayerLookupRequest("missing-" + System.nanoTime() + "@example.test");

        assertBadRequest(playersApi.getOneRaw(request).then());
    }

    @Test
    @DisplayName("DELETE /api/automationTask/deleteOne/{id} reports an unknown id as a bad request")
    void deleteRejectsUnknownPlayerId() {
        assertBadRequest(playersApi.deleteOneRaw("missing-" + System.nanoTime()).then());
    }

    @Test
    @DisplayName("DELETE /api/automationTask/deleteOne/{id} without an id does not match any route")
    void deleteWithoutIdDoesNotMatchTheRoute() {
        assertThat(playersApi.deleteOneRaw("").then().extract().statusCode()).isEqualTo(404);
    }

    @Test
    @DisplayName("DELETE /api/automationTask/deleteOne/{id} removes a player that was created before")
    void deleteRemovesAnExistingPlayer() {
        Player player = remember(playersApi.create(PlayerFactory.validPlayer()));

        Player deleted = playersApi.deleteOne(player.id());

        assertThat(deleted.id()).isEqualTo(player.id());
    }
}
