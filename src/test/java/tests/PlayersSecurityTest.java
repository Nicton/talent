package tests;

import api.PlayersApi;
import model.PlayerLookupRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import testdata.PlayerFactory;

import static util.PlayerAssertions.assertUnauthorized;

@Tag("security")
class PlayersSecurityTest {

    @Test
    @DisplayName("Players endpoints reject requests without an Authorization header")
    void endpointsRequireToken() {
        PlayersApi anonymous = PlayersApi.withoutToken();

        assertUnauthorized(anonymous.getAllRaw().then());
        assertUnauthorized(anonymous.getOneRaw(new PlayerLookupRequest("missing@example.test")).then());
        assertUnauthorized(anonymous.createRaw(PlayerFactory.validPlayer()).then());
        assertUnauthorized(anonymous.deleteOneRaw("1").then());
    }

    @Test
    @DisplayName("Players endpoints reject a malformed bearer token")
    void endpointsRejectMalformedToken() {
        PlayersApi malformed = PlayersApi.withRawToken("not-a-real-token");

        assertUnauthorized(malformed.getAllRaw().then());
        assertUnauthorized(malformed.getOneRaw(new PlayerLookupRequest("missing@example.test")).then());
        assertUnauthorized(malformed.createRaw(PlayerFactory.validPlayer()).then());
        assertUnauthorized(malformed.deleteOneRaw("1").then());
    }

    @Test
    @DisplayName("Players endpoints reject a token of the wrong type")
    void endpointsRejectNonBearerScheme() {
        PlayersApi wrongScheme = PlayersApi.withRawToken("Basic dGVzdDp0ZXN0");

        assertUnauthorized(wrongScheme.getAllRaw().then());
        assertUnauthorized(wrongScheme.deleteOneRaw("1").then());
    }
}
