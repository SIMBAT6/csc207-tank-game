package data_access;

import entity.Player;
import use_case.session.CurrentPlayerDataAccessInterface;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class InMemoryCurrentPlayerDataAccessObject implements CurrentPlayerDataAccessInterface {
    private final Map<Integer, Player> players = new ConcurrentHashMap<>();

    @Override
    public void save(int side, Player player) {
        if (side != 1 && side != 2) {
            throw new IllegalArgumentException("Choose Player 1 or Player 2.");
        }
        players.put(side, player);
    }

    @Override
    public Optional<Player> findBySide(int side) {
        return Optional.ofNullable(players.get(side));
    }
}
