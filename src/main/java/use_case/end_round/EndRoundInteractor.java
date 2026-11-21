package use_case.end_round;

import entity.Match;
import entity.Player;
import use_case.DataAccessException;

/**
 * Ends a round: gives the survivor a point, saves each logged-in player's win or loss,
 * and reports whether the match is over.
 */
public class EndRoundInteractor implements EndRoundInputBoundary {
    private final EndRoundMatchDataAccessInterface matchDataAccess;
    private final EndRoundStatsDataAccessInterface statsDataAccess;
    private final EndRoundOutputBoundary presenter;

    public EndRoundInteractor(EndRoundMatchDataAccessInterface matchDataAccess,
                              EndRoundStatsDataAccessInterface statsDataAccess,
                              EndRoundOutputBoundary presenter) {
        this.matchDataAccess = matchDataAccess;
        this.statsDataAccess = statsDataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(EndRoundInputData inputData) {
        final Match match = matchDataAccess.getCurrentMatch();
        final int winner = inputData.getWinner();
        match.endRound(winner);

        boolean resultsSaved = true;
        if (winner != Match.DRAW) {
            final int loser = winner == Match.PLAYER_1 ? Match.PLAYER_2 : Match.PLAYER_1;
            resultsSaved = record(match.getPlayer(winner), true);
            resultsSaved = record(match.getPlayer(loser), false) && resultsSaved;
        }

        final int matchWinner = match.getMatchWinner();
        final EndRoundOutputData outputData = new EndRoundOutputData(
                winner,
                winner == Match.DRAW ? null : match.getPlayer(winner).getUsername(),
                match.getScore1(),
                match.getScore2(),
                matchWinner,
                matchWinner == Match.NO_WINNER ? null : match.getPlayer(matchWinner).getUsername(),
                resultsSaved);

        if (match.isMatchOver()) {
            presenter.prepareMatchOverView(outputData);
        }
        else {
            presenter.prepareRoundOverView(outputData);
        }
    }

    /**
     * Updates the player's record and saves it. Guests are not saved.
     *
     * @return false if saving failed
     */
    private boolean record(Player player, boolean won) {
        if (player.isGuest()) {
            return true;
        }
        if (won) {
            player.recordWin();
        }
        else {
            player.recordLoss();
        }
        try {
            statsDataAccess.recordResult(player.getUsername(), won);
            return true;
        }
        catch (DataAccessException ex) {
            return false;
        }
    }
}
