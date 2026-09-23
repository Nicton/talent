package tests;

import api.AuthApi;
import config.TestConfig;
import model.Credentials;
import model.Token;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static util.PlayerAssertions.assertUnauthorized;

@Tag("authentication")
class AuthenticationTest {
    private final AuthApi authApi = new AuthApi();

    @Test
    @DisplayName("POST /api/tester/login returns a usable bearer token for valid credentials")
    void loginWithValidCredentialsReturnsToken() {
        Token token = authApi.login(new Credentials(TestConfig.testerEmail(), TestConfig.testerPassword()));

        assertThat(token.accessToken()).isNotBlank();
        assertThat(token.user().email()).isEqualTo(TestConfig.testerEmail());
    }

    @Test
    @DisplayName("POST /api/tester/login rejects a wrong password")
    void loginWithWrongPasswordIsUnauthorized() {
        Credentials credentials = new Credentials(TestConfig.testerEmail(), "wrong-password-" + System.nanoTime());

        assertUnauthorized(authApi.loginRaw(credentials).then());
    }

    @Test
    @DisplayName("POST /api/tester/login rejects an unknown account")
    void loginWithUnknownEmailIsUnauthorized() {
        Credentials credentials = new Credentials("missing-" + System.nanoTime() + "@example.test", "irrelevant-password");

        assertUnauthorized(authApi.loginRaw(credentials).then());
    }

    @Test
    @DisplayName("POST /api/tester/login rejects an empty payload")
    void loginWithEmptyPayloadIsRejected() {
        assertUnauthorized(authApi.loginRaw(new Credentials(null, null)).then());
    }

    @Test
    @DisplayName("POST /api/tester/login rejects credentials that were swapped")
    void loginWithSwappedCredentialsIsUnauthorized() {
        Credentials credentials = new Credentials(TestConfig.testerPassword(), TestConfig.testerEmail());

        assertUnauthorized(authApi.loginRaw(credentials).then());
    }
}
