package com.slotegrator.assignment.tests;

import com.slotegrator.assignment.api.AuthApi;
import com.slotegrator.assignment.config.TestConfig;
import com.slotegrator.assignment.model.Credentials;
import com.slotegrator.assignment.model.Token;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthenticationTest {
    private final AuthApi authApi = new AuthApi();

    @Test
    @DisplayName("POST /api/tester/login returns a usable bearer token for valid credentials")
    void loginWithValidCredentialsReturnsToken() {
        Token token = authApi.login(new Credentials(TestConfig.testerEmail(), TestConfig.testerPassword()));

        assertThat(token.accessToken()).isNotBlank();
        assertThat(token.tokenType()).isNotBlank();
        assertThat(token.expiresIn()).isNotBlank();
        assertThat(token.scope()).isNotNull();
    }

    @Test
    @DisplayName("POST /api/tester/login rejects invalid credentials")
    void loginWithInvalidCredentialsIsUnauthorized() {
        authApi.loginRaw(new Credentials(TestConfig.testerEmail(), "wrong-password"))
                .then()
                .statusCode(401);
    }
}
