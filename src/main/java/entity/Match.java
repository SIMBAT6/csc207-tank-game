package entity;

import java.util.ArrayList;
import java.util.List;

/**
 * A local two-player match: the maze, both tanks, and everything currently on the field.
 * TODO (UC2 lead): startRound().  TODO (UC3 lead): tick().  TODO (UC5 lead): endRound(winner).
 */
public class Match {
    private final Player player1;
    private final Player player2;
    private Tank tank1;
    private Tank tank2;
    private Maze maze;
    private final List<Bullet> bullets = new ArrayList<>();
    private final List<PowerUp> powerUps = new ArrayList<>();
    private int score1;
    private int score2;
    private boolean roundOver;

    public Match(Player player1, Player player2, Maze maze, Tank tank1, Tank tank2) {
        this.player1 = player1;
        this.player2 = player2;
        this.maze = maze;
        this.tank1 = tank1;
        this.tank2 = tank2;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public Tank getTank1() {
        return tank1;
    }

    public Tank getTank2() {
        return tank2;
    }

    public Maze getMaze() {
        return maze;
    }

    public List<Bullet> getBullets() {
        return bullets;
    }

    public List<PowerUp> getPowerUps() {
        return powerUps;
    }

    public int getScore1() {
        return score1;
    }

    public int getScore2() {
        return score2;
    }

    public boolean isRoundOver() {
        return roundOver;
    }
}
