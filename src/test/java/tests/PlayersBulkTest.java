package tests;

import model.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import testdata.PlayerFactory;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("players")
class PlayersBulkTest extends BaseApiTest {
    private static final int BULK_SIZE = 101;

    @Test
    @DisplayName("GET /api/automationTask/getAll returns the whole collection without pagination")
    void allBulkCreatedPlayersAreReturnedByGetAll() {
        List<Player> created = IntStream.rangeClosed(1, BULK_SIZE)
                .mapToObj(index -> remember(playersApi.create(PlayerFactory.validPlayer(index))))
                .toList();

        Set<String> createdIds = created.stream().map(Player::id).collect(Collectors.toSet());

        assertThat(playersApi.getAll())
                .as("getAll is not paginated, so every created player is present")
                .extracting(Player::id)
                .containsAll(createdIds);
    }
}
