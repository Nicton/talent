package com.slotegrator.assignment.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PlayerLookupRequest(@JsonProperty("email") String email) {
}
