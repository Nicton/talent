package com.slotegrator.assignment.util;

import com.slotegrator.assignment.model.Player;
import com.slotegrator.assignment.model.PlayerCreateRequest;

import static org.assertj.core.api.Assertions.assertThat;

public final class PlayerAssertions {
    private PlayerAssertions() {
    }

    public static void assertMatchesRequest(Player actual, PlayerCreateRequest expected) {
        assertThat(actual.id()).as("player id").isPositive();
        assertThat(actual.username()).as("username").isEqualTo(expected.username());
        assertThat(actual.email()).as("email").isEqualTo(expected.email());
        assertThat(actual.name()).as("name").isEqualTo(expected.name());
        assertThat(actual.surname()).as("surname").isEqualTo(expected.surname());
    }
}
