package interface_adapter.start_match;

import use_case.start_match.MatchFormat;
import use_case.start_match.StartMatchInputBoundary;
import use_case.start_match.StartMatchInputData;

public final class StartMatchController {
    private final StartMatchInputBoundary interactor;

    public StartMatchController(StartMatchInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void startMatch(String mapId, MatchFormat format) {
        interactor.execute(new StartMatchInputData(mapId, format));
    }
}