package testdata;

import com.github.javafaker.Faker;
import config.TestConfig;
import model.PlayerCreateRequest;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public final class PlayerFactory {
    private static final Faker FAKER = new Faker(Locale.ENGLISH);
    private static final String RUN_ID = Long.toString(Instant.now().toEpochMilli(), 36);
    private static final AtomicInteger SEQUENCE = new AtomicInteger();

    private PlayerFactory() {
    }

    public static PlayerCreateRequest validPlayer() {
        return validPlayer(SEQUENCE.incrementAndGet());
    }

    public static PlayerCreateRequest validPlayer(int index) {
        String password = defaultPassword(index);
        return new PlayerCreateRequest(
                TestConfig.defaultCurrency(),
                uniqueEmail(),
                sanitize(FAKER.name().firstName()),
                password,
                password,
                sanitize(FAKER.name().lastName()),
                uniqueUsername(index)
        );
    }

    public static String uniqueEmail() {
        return "qa+" + uniqueToken() + "@example.test";
    }

    public static String uniqueUsername(int index) {
        return ("qa" + RUN_ID + index + uniqueToken()).replace("-", "");
    }

    public static String defaultPassword(int index) {
        return "Pass" + index + "word!";
    }

    public static PlayerCreateRequest withUsername(PlayerCreateRequest source, String username) {
        return copy(source, source.currencyCode(), source.email(), source.name(), source.passwordChange(),
                source.passwordRepeat(), source.surname(), username);
    }

    public static PlayerCreateRequest withEmail(PlayerCreateRequest source, String email) {
        return copy(source, source.currencyCode(), email, source.name(), source.passwordChange(),
                source.passwordRepeat(), source.surname(), source.username());
    }

    public static PlayerCreateRequest withPassword(PlayerCreateRequest source, String password, String passwordRepeat) {
        return copy(source, source.currencyCode(), source.email(), source.name(), password,
                passwordRepeat, source.surname(), source.username());
    }

    public static PlayerCreateRequest withCurrency(PlayerCreateRequest source, String currencyCode) {
        return copy(source, currencyCode, source.email(), source.name(), source.passwordChange(),
                source.passwordRepeat(), source.surname(), source.username());
    }

    public static PlayerCreateRequest withName(PlayerCreateRequest source, String name) {
        return copy(source, source.currencyCode(), source.email(), name, source.passwordChange(),
                source.passwordRepeat(), source.surname(), source.username());
    }

    private static PlayerCreateRequest copy(PlayerCreateRequest source, String currencyCode, String email, String name,
                                            String password, String passwordRepeat, String surname, String username) {
        return new PlayerCreateRequest(currencyCode, email, name, password, passwordRepeat, surname, username);
    }

    private static String uniqueToken() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private static String sanitize(String value) {
        return value.replaceAll("[^A-Za-z]", "");
    }
}
