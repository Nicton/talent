package model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PlayerLookupRequest(@JsonProperty("email") String email) {
}
