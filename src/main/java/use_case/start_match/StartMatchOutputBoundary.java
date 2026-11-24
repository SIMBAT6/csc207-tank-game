package use_case.start_match;

import entity.Match;

public interface StartMatchOutputBoundary {
    void presentSuccess(Match match);

    void presentError(String message);
}