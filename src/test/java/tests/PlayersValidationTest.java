package tests;

import model.Player;
import model.PlayerCreateRequest;
import model.PlayerLookupRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import testdata.PlayerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static util.PlayerAssertions.assertClientError;
import static util.PlayerAssertions.assertMatchesRequest;
import static util.PlayerAssertions.assertNotFound;

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
    @DisplayName("POST /api/automationTask/create accepts a password with special characters")
    void createPlayerAcceptsSpecialCharactersInPassword() {
        PlayerCreateRequest valid = PlayerFactory.validPlayer();
        String strongPassword = "P@ssw0rd!#$%^&*()";
        PlayerCreateRequest request = PlayerFactory.withPassword(valid, strongPassword, strongPassword);

        Player player = remember(playersApi.create(request));

        assertThat(player.email()).isEqualTo(request.email());
    }

    @Test
    @DisplayName("POST /api/automationTask/create validates username presence")
    void createPlayerRejectsMissingUsername() {
        PlayerCreateRequest request = PlayerFactory.withUsername(PlayerFactory.validPlayer(), null);

        assertClientError(playersApi.createRaw(request).then());
    }

    @Test
    @DisplayName("POST /api/automationTask/create validates empty username")
    void createPlayerRejectsEmptyUsername() {
        PlayerCreateRequest request = PlayerFactory.withUsername(PlayerFactory.validPlayer(), "");

        assertClientError(playersApi.createRaw(request).then());
    }

    @Test
    @DisplayName("POST /api/automationTask/create validates username minimum length")
    void createPlayerRejectsTooShortUsername() {
        PlayerCreateRequest request = PlayerFactory.withUsername(PlayerFactory.validPlayer(), "ab");

        assertClientError(playersApi.createRaw(request).then());
    }

    @Test
    @DisplayName("POST /api/automationTask/create validates email presence")
    void createPlayerRejectsMissingEmail() {
        PlayerCreateRequest request = PlayerFactory.withEmail(PlayerFactory.validPlayer(), null);

        assertClientError(playersApi.createRaw(request).then());
    }

    @Test
    @DisplayName("POST /api/automationTask/create validates empty email")
    void createPlayerRejectsEmptyEmail() {
        PlayerCreateRequest request = PlayerFactory.withEmail(PlayerFactory.validPlayer(), "");

        assertClientError(playersApi.createRaw(request).then());
    }

    @Test
    @DisplayName("POST /api/automationTask/create validates email format")
    void createPlayerRejectsInvalidEmailFormat() {
        for (String invalidEmail : new String[]{"not-an-email", "@example.test", "player@@example.test"}) {
            PlayerCreateRequest request = PlayerFactory.withEmail(PlayerFactory.validPlayer(), invalidEmail);

            assertClientError(playersApi.createRaw(request).then());
        }
    }

    @Test
    @DisplayName("POST /api/automationTask/create validates password presence")
    void createPlayerRejectsMissingPassword() {
        PlayerCreateRequest request = PlayerFactory.withPassword(PlayerFactory.validPlayer(), null, null);

        assertClientError(playersApi.createRaw(request).then());
    }

    @Test
    @DisplayName("POST /api/automationTask/create validates password minimum length")
    void createPlayerRejectsTooShortPassword() {
        PlayerCreateRequest request = PlayerFactory.withPassword(PlayerFactory.validPlayer(), "abc", "abc");

        assertClientError(playersApi.createRaw(request).then());
    }

    @Test
    @DisplayName("POST /api/automationTask/create validates the password confirmation")
    void createPlayerRejectsPasswordConfirmationMismatch() {
        PlayerCreateRequest valid = PlayerFactory.validPlayer();
        PlayerCreateRequest request = PlayerFactory.withPassword(valid, valid.passwordChange(), valid.passwordChange() + "x");

        assertClientError(playersApi.createRaw(request).then());
    }

    @Test
    @DisplayName("POST /api/automationTask/create rejects a duplicate email")
    void createPlayerRejectsDuplicateEmail() {
        PlayerCreateRequest first = PlayerFactory.validPlayer();
        remember(playersApi.create(first));

        PlayerCreateRequest duplicateEmail = PlayerFactory.withEmail(PlayerFactory.validPlayer(), first.email());

        assertClientError(playersApi.createRaw(duplicateEmail).then());
    }

    @Test
    @DisplayName("POST /api/automationTask/getOne reports an unknown email as not found")
    void getOneRejectsUnknownPlayer() {
        PlayerLookupRequest request = new PlayerLookupRequest("missing-" + System.nanoTime() + "@example.test");

        assertNotFound(playersApi.getOneRaw(request).then());
    }

    @Test
    @DisplayName("POST /api/automationTask/getOne validates email presence")
    void getOneRejectsMissingEmail() {
        assertClientError(playersApi.getOneRaw(new PlayerLookupRequest(null)).then());
    }

    @Test
    @DisplayName("DELETE /api/automationTask/deleteOne/{id} reports an unknown id as not found")
    void deleteRejectsUnknownPlayerId() {
        assertNotFound(playersApi.deleteOneRaw(Integer.MAX_VALUE).then());
    }
}
