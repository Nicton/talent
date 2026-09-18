package com.slotegrator.assignment.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Player(
        @JsonProperty("id") Integer id,
        @JsonProperty("username") String username,
        @JsonProperty("email") String email,
        @JsonProperty("name") String name,
        @JsonProperty("surname") String surname
) {
}
