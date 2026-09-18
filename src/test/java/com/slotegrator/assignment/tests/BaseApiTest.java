package com.slotegrator.assignment.tests;

import com.slotegrator.assignment.api.AuthApi;
import com.slotegrator.assignment.api.PlayersApi;
import com.slotegrator.assignment.config.TestConfig;
import com.slotegrator.assignment.model.Credentials;
import com.slotegrator.assignment.model.Player;
import com.slotegrator.assignment.model.Token;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseApiTest {
    protected AuthApi authApi;
    protected PlayersApi playersApi;
    protected Token token;
    protected final List<Player> createdPlayers = new ArrayList<>();

    @BeforeEach
    void authenticate() {
        authApi = new AuthApi();
        token = authApi.login(new Credentials(TestConfig.testerEmail(), TestConfig.testerPassword()));
        playersApi = new PlayersApi(token.accessToken());
    }

    @AfterEach
    void cleanUpCreatedPlayers() {
        for (Player player : createdPlayers.reversed()) {
            if (player.id() == null) {
                continue;
            }
            try {
                playersApi.deleteOneRaw(player.id());
            } catch (RuntimeException ignored) {
                // Cleanup should not hide the original test failure.
            }
        }
        createdPlayers.clear();
    }

    protected Player remember(Player player) {
        createdPlayers.add(player);
        return player;
    }
}
