package tests;

import api.AuthApi;
import model.Credentials;
import model.Player;
import model.PlayerCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import testdata.PlayerFactory;

import static util.PlayerAssertions.assertUnauthorized;

@Tag("security")
class PlayersAccountAccessTest extends BaseApiTest {

    @Test
    @DisplayName("A created player cannot be used to sign in as a tester")
    void createdPlayerCredentialsCannotAuthenticate() {
        PlayerCreateRequest request = PlayerFactory.validPlayer();
        remember(playersApi.create(request));

        assertUnauthorized(new AuthApi().loginRaw(new Credentials(request.email(), request.passwordChange())).then());
    }
}
