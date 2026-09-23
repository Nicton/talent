package model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The API is not fully consistent between endpoints: creation and deletion
 * return the document under "_id", while lookup and listing return it under
 * "id". Both keys are mapped to {@link #id}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Player(
        @JsonAlias("_id") @JsonProperty("id") String id,
        @JsonProperty("currency_code") String currencyCode,
        @JsonProperty("username") String username,
        @JsonProperty("email") String email,
        @JsonProperty("name") String name,
        @JsonProperty("surname") String surname
) {
}
