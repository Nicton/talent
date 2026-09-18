package com.slotegrator.assignment.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PlayerCreateRequest(
        @JsonProperty("currency_code") String currencyCode,
        @JsonProperty("email") String email,
        @JsonProperty("name") String name,
        @JsonProperty("password_change") String passwordChange,
        @JsonProperty("password_repeat") String passwordRepeat,
        @JsonProperty("surname") String surname,
        @JsonProperty("username") String username
) {
}
