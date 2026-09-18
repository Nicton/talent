package com.slotegrator.assignment.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Credentials(
        @JsonProperty("email") String email,
        @JsonProperty("password") String password
) {
}
