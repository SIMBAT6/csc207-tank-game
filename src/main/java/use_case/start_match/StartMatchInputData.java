package use_case.start_match;

public final class StartMatchInputData {
    private final String mapId;
    private final MatchFormat matchFormat;

    public StartMatchInputData(String mapId, MatchFormat matchFormat) {
        this.mapId = mapId;
        this.matchFormat = matchFormat;
    }

    public String getMapId() {
        return mapId;
    }

    public MatchFormat getMatchFormat() {
        return matchFormat;
    }
}