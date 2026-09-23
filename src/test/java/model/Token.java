package model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Token(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("expires_in") String expiresIn,
        @JsonProperty("scope") String scope
) {
}
