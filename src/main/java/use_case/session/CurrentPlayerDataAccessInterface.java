package use_case.session;

import entity.Player;

import java.util.Optional;

public interface CurrentPlayerDataAccessInterface {
    void save(int side, Player player);
    Optional<Player> findBySide(int side);
}
