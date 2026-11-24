package use_case.start_match;

import entity.Match;
import entity.Maze;
import entity.Player;
import entity.Tank;
import entity.Wall;
import use_case.session.CurrentPlayerDataAccessInterface;

import java.util.ArrayList;
import java.util.List;

public final class StartMatchInteractor implements StartMatchInputBoundary {
    private static final int CELL_SIZE = 46;

    private final CurrentPlayerDataAccessInterface currentPlayers;
    private final StartMatchOutputBoundary presenter;

    public StartMatchInteractor(CurrentPlayerDataAccessInterface currentPlayers,
                                StartMatchOutputBoundary presenter) {
        this.currentPlayers = currentPlayers;
        this.presenter = presenter;
    }

    @Override
    public void execute(StartMatchInputData inputData) {
        if (inputData.getMatchFormat() == null) {
            presenter.presentError("Choose a match format.");
            return;
        }

        String mapId = inputData.getMapId();
        List<String> layout;

        if (PresetMaps.RANDOM.equals(mapId)) {
            layout = RandomMaze.generate();
        } else if (mapId != null && PresetMaps.getMapIds().contains(mapId)) {
            layout = PresetMaps.getLayout(mapId);
        } else {
            presenter.presentError("Choose a map.");
            return;
        }

        Match match = createMatch(layout, inputData.getMatchFormat());
        presenter.presentSuccess(match);
    }

    private Match createMatch(List<String> layout, MatchFormat format) {
        int columns = layout.get(0).length();
        List<Wall> walls = new ArrayList<>();
        Tank tank1 = null;
        Tank tank2 = null;

        for (int row = 0; row < layout.size(); row++) {
            for (int column = 0; column < columns; column++) {
                char cell = layout.get(row).charAt(column);
                int x = column * CELL_SIZE;
                int y = row * CELL_SIZE;

                if (cell == '#') {
                    walls.add(new Wall(x, y, CELL_SIZE, CELL_SIZE));
                } else if (cell == '1') {
                    tank1 = new Tank(Match.PLAYER_1,
                            x + CELL_SIZE / 2.0, y + CELL_SIZE / 2.0, 0);
                } else if (cell == '2') {
                    tank2 = new Tank(Match.PLAYER_2,
                            x + CELL_SIZE / 2.0, y + CELL_SIZE / 2.0, Math.PI);
                }
            }
        }

        Maze maze = new Maze(layout.size(), columns, CELL_SIZE, walls);
        Match match = new Match(playerForSide(Match.PLAYER_1),
                playerForSide(Match.PLAYER_2), maze, tank1, tank2);
        match.setWinsToWin(format.getWinsToWin());
        return match;
    }

    private Player playerForSide(int side) {
        return currentPlayers.findBySide(side)
                .orElseGet(() -> new Player("Guest " + side, true, 0, 0));
    }
}