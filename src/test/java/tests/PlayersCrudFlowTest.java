package tests;

import config.TestConfig;
import model.Player;
import model.PlayerCreateRequest;
import model.PlayerLookupRequest;
import testdata.PlayerFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static util.PlayerAssertions.assertMatchesRequest;
import static org.assertj.core.api.Assertions.assertThat;

@Tag("players")
class PlayersCrudFlowTest extends BaseApiTest {
    private static final int PLAYERS_TO_CREATE = 12;

    @Test
    @DisplayName("Create 12 players, read them back, sort by name, delete them and verify the list is empty")
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

        verifyProfileMatchesCreateResponse(requests.getFirst(), created.getFirst());
        verifyCreatedPlayersAreListedAndSorted(created);

        created.forEach(player -> playersApi.deleteOne(player.id()));
        createdPlayers.clear();

        assertThat(playersApi.getAll())
                .as("getAll should be empty after the players created in this run were deleted")
                .isEmpty();
    }

    private void verifyProfileMatchesCreateResponse(PlayerCreateRequest request, Player fromCreate) {
        Player fromProfile = playersApi.getOne(new PlayerLookupRequest(request.email()), TestConfig.getOneExpectedStatus());

        assertThat(fromProfile).usingRecursiveComparison().isEqualTo(fromCreate);
    }

    private void verifyCreatedPlayersAreListedAndSorted(List<Player> created) {
        Comparator<Player> byName = Comparator
                .comparing(Player::name, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(Player::surname, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(Player::id);

        Set<Integer> createdIds = created.stream().map(Player::id).collect(Collectors.toSet());

        List<Player> listedCreatedPlayers = playersApi.getAll().stream()
                .filter(player -> createdIds.contains(player.id()))
                .toList();

        assertThat(listedCreatedPlayers).hasSize(PLAYERS_TO_CREATE);
        assertThat(listedCreatedPlayers.stream().sorted(byName).toList()).isSortedAccordingTo(byName);
    }
}
