package interface_adapter.start_match;

import entity.Match;
import use_case.start_match.StartMatchOutputBoundary;

import java.util.function.Consumer;

public final class StartMatchPresenter implements StartMatchOutputBoundary {
    private final Consumer<Match> showGame;
    private final Consumer<String> showError;

    public StartMatchPresenter(Consumer<Match> showGame,
                               Consumer<String> showError) {
        this.showGame = showGame;
        this.showError = showError;
    }

    @Override
    public void presentSuccess(Match match) {
        showGame.accept(match);
    }

    @Override
    public void presentError(String message) {
        showError.accept(message);
    }
}