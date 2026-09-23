package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Token(
        @JsonProperty("accessToken") String accessToken,
        @JsonProperty("user") UserProfile user
) {
}
