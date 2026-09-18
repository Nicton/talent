package com.slotegrator.assignment.testdata;

import com.github.javafaker.Faker;
import com.slotegrator.assignment.config.TestConfig;
import com.slotegrator.assignment.model.PlayerCreateRequest;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

public final class PlayerFactory {
    private static final Faker FAKER = new Faker(Locale.ENGLISH);
    private static final String RUN_ID = Long.toString(Instant.now().toEpochMilli(), 36);

    private PlayerFactory() {
    }

    public static PlayerCreateRequest validPlayer(int index) {
        String unique = RUN_ID + "-" + index + "-" + UUID.randomUUID().toString().substring(0, 8);
        String username = ("qa" + unique).replace("-", "");
        String password = "Pass" + index + "word!";

        return new PlayerCreateRequest(
                TestConfig.defaultCurrency(),
                "qa+" + unique + "@example.test",
                sanitize(FAKER.name().firstName()),
                password,
                password,
                sanitize(FAKER.name().lastName()),
                username
        );
    }

    public static PlayerCreateRequest withEmail(PlayerCreateRequest source, String email) {
        return new PlayerCreateRequest(
                source.currencyCode(),
                email,
                source.name(),
                source.passwordChange(),
                source.passwordRepeat(),
                source.surname(),
                source.username()
        );
    }

    public static PlayerCreateRequest withUsername(PlayerCreateRequest source, String username) {
        return new PlayerCreateRequest(
                source.currencyCode(),
                source.email(),
                source.name(),
                source.passwordChange(),
                source.passwordRepeat(),
                source.surname(),
                username
        );
    }

    private static String sanitize(String value) {
        return value.replaceAll("[^A-Za-z]", "");
    }
}
