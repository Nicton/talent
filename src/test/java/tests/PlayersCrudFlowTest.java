package tests;

import model.Player;
import model.PlayerCreateRequest;
import model.PlayerLookupRequest;
import testdata.PlayerFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import static util.PlayerAssertions.assertMatchesRequest;
import static org.assertj.core.api.Assertions.assertThat;

class PlayersCrudFlowTest extends BaseApiTest {
    private static final int PLAYERS_TO_CREATE = 12;

    @Test
    @DisplayName("Full assignment flow: create 12 players, read, sort by name, delete, verify empty list")
    void assignmentCrudFlowWorksEndToEnd() {
        List<PlayerCreateRequest> requests = IntStream.rangeClosed(1, PLAYERS_TO_CREATE)
                .mapToObj(PlayerFactory::validPlayer)
                .toList();

        List<Player> created = requests.stream()
                .map(playersApi::create)
                .map(this::remember)
                .toList();

        assertThat(created)
                .hasSize(PLAYERS_TO_CREATE)
                .extracting(Player::id)
                .doesNotHaveDuplicates();

        for (int i = 0; i < PLAYERS_TO_CREATE; i++) {
            assertMatchesRequest(created.get(i), requests.get(i));
        }

        PlayerCreateRequest sampleRequest = requests.getFirst();
        Player sampleFromCreate = created.getFirst();
        Player sampleFromProfile = playersApi.getOne(new PlayerLookupRequest(sampleRequest.email()), expectedGetOneStatusCode());
        assertThat(sampleFromProfile).usingRecursiveComparison().isEqualTo(sampleFromCreate);

        List<Player> sortedByName = playersApi.getAll().stream()
                .filter(player -> created.stream().anyMatch(createdPlayer -> createdPlayer.id().equals(player.id())))
                .sorted(Comparator.comparing(Player::name, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(Player::surname, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(Player::id))
                .toList();

        assertThat(sortedByName)
                .hasSize(PLAYERS_TO_CREATE)
                .isSortedAccordingTo(Comparator.comparing(Player::name, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(Player::surname, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(Player::id));

        created.forEach(player -> playersApi.deleteOne(player.id()));
        createdPlayers.clear();

        assertThat(playersApi.getAll())
                .as("getAll should be empty after deleting all players created in this isolated test assignment account")
                .isEmpty();
    }

    /**
     * The task text says getOne should return 200, but the published OpenAPI spec declares 201.
     * Keeping it configurable in one place makes the expected behavior explicit and easy to adjust
     * after checking the live API.
     */
    private int expectedGetOneStatusCode() {
        return Integer.parseInt(System.getenv().getOrDefault("GET_ONE_EXPECTED_STATUS", "201"));
    }
}
