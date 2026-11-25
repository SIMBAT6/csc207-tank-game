package entity;

import java.util.ArrayList;
import java.util.List;

/**
 * A local two-player match: the maze, both tanks, and everything currently on the field.
 * A game tick is run by Control Tank (UC3) and Collect Power-Up (UC4), not by the match itself.
 * TODO (UC2 lead): startRound().
 */
public class Match {
    /** Round winner: player 1's tank survived. */
    public static final int PLAYER_1 = 1;
    /** Round winner: player 2's tank survived. */
    public static final int PLAYER_2 = 2;
    /** Round winner: both tanks were destroyed on the same tick. */
    public static final int DRAW = -1;
    /** No one has won the match yet. */
    public static final int NO_WINNER = 0;

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
    private int winsToWin = 1;

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

    public int getWinsToWin() {
        return winsToWin;
    }

    /**
     * Sets how many round wins end the match (1 for BO1, 2 for BO3, 3 for BO5). Set by Start Match (UC2).
     *
     * @param winsToWin round wins needed, at least 1
     */
    public void setWinsToWin(int winsToWin) {
        if (winsToWin < 1) {
            throw new IllegalArgumentException("winsToWin must be at least 1, got " + winsToWin);
        }
        this.winsToWin = winsToWin;
    }

    /**
     * Ends the current round and gives the surviving player a point. A draw gives no point.
     *
     * @param winner {@link #PLAYER_1}, {@link #PLAYER_2} or {@link #DRAW}
     */
    public void endRound(int winner) {
        if (winner == PLAYER_1) {
            score1++;
        }
        else if (winner == PLAYER_2) {
            score2++;
        }
        else if (winner != DRAW) {
            throw new IllegalArgumentException("unknown round winner " + winner);
        }
        roundOver = true;
    }

    /** True once either player has {@link #getWinsToWin()} round wins. */
    public boolean isMatchOver() {
        return getMatchWinner() != NO_WINNER;
    }

    /**
     * The player who won the whole match.
     *
     * @return {@link #PLAYER_1}, {@link #PLAYER_2}, or {@link #NO_WINNER} while the match is still going
     */
    public int getMatchWinner() {
        if (score1 >= winsToWin) {
            return PLAYER_1;
        }
        if (score2 >= winsToWin) {
            return PLAYER_2;
        }
        return NO_WINNER;
    }

    /**
     * The player on one side of the match.
     *
     * @param side {@link #PLAYER_1} or {@link #PLAYER_2}
     */
    public Player getPlayer(int side) {
        if (side == PLAYER_1) {
            return player1;
        }
        if (side == PLAYER_2) {
            return player2;
        }
        throw new IllegalArgumentException("unknown side " + side);
    }
}
