package data_access;

import entity.Player;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;
import use_case.DataAccessException;
import use_case.end_round.EndRoundStatsDataAccessInterface;
import use_case.view_leaderboard.ViewLeaderboardDataAccessInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Saves and loads every player's win/loss record through the CSC207 account API
 * (the same API as the course's DBUserDataAccessObject).
 *
 * <p>The API cannot list all users, so all records live in the "info" JSON of one shared
 * leaderboard account: {@code {"players": {"alice": {"wins": 3, "losses": 1}, ...}}}.
 * The account is created the first time a result is saved.</p>
 */
public class DBPlayerStatsDataAccessObject implements EndRoundStatsDataAccessInterface,
        ViewLeaderboardDataAccessInterface {
    public static final String API_URL = "https://csc207-20269-vm.teach.cs.toronto.edu";
    public static final String LEADERBOARD_ACCOUNT = "ricochet-tanks-leaderboard";
    private static final String LEADERBOARD_PASSWORD = "ricochet-tanks-207";

    private static final int SUCCESS_CODE = 200;
    private static final int NOT_FOUND_CODE = 404;
    private static final MediaType JSON = MediaType.parse("application/json");
    private static final String STATUS_CODE = "status_code";
    private static final String MESSAGE = "message";
    private static final String PLAYERS = "players";
    private static final String WINS = "wins";
    private static final String LOSSES = "losses";

    private final OkHttpClient client = new OkHttpClient();
    private final String apiUrl;
    private final String account;
    private final String password;

    public DBPlayerStatsDataAccessObject() {
        this(API_URL, LEADERBOARD_ACCOUNT, LEADERBOARD_PASSWORD);
    }

    /**
     * @param apiUrl   base URL of the account API
     * @param account  the shared account that holds every record
     * @param password that account's password
     */
    public DBPlayerStatsDataAccessObject(String apiUrl, String account, String password) {
        this.apiUrl = apiUrl;
        this.account = account;
        this.password = password;
    }

    @Override
    public void recordResult(String username, boolean won) throws DataAccessException {
        final JSONObject players = loadPlayers();
        final JSONObject record = players.optJSONObject(username);
        final JSONObject updated = new JSONObject();
        updated.put(WINS, (record == null ? 0 : record.optInt(WINS)) + (won ? 1 : 0));
        updated.put(LOSSES, (record == null ? 0 : record.optInt(LOSSES)) + (won ? 0 : 1));
        players.put(username, updated);
        savePlayers(players);
    }

    @Override
    public List<Player> getAllPlayers() throws DataAccessException {
        final JSONObject players = loadPlayers();
        final List<Player> result = new ArrayList<>();
        for (String username : players.keySet()) {
            final JSONObject record = players.getJSONObject(username);
            result.add(new Player(username, false, record.optInt(WINS), record.optInt(LOSSES)));
        }
        return result;
    }

    /** Reads the "players" object from the leaderboard account, creating the account if needed. */
    private JSONObject loadPlayers() throws DataAccessException {
        final Request request = new Request.Builder()
                .url(apiUrl + "/user?username=" + account)
                .build();
        final JSONObject body = call(request);
        final int status = body.getInt(STATUS_CODE);
        if (status == NOT_FOUND_CODE) {
            createAccount();
            return new JSONObject();
        }
        if (status != SUCCESS_CODE) {
            throw new DataAccessException(body.optString(MESSAGE, "status " + status));
        }
        final JSONObject info = body.getJSONObject("user").optJSONObject("info");
        if (info == null || info.optJSONObject(PLAYERS) == null) {
            return new JSONObject();
        }
        return info.getJSONObject(PLAYERS);
    }

    private void savePlayers(JSONObject players) throws DataAccessException {
        final JSONObject info = new JSONObject();
        info.put(PLAYERS, players);
        final JSONObject requestBody = new JSONObject();
        requestBody.put("username", account);
        requestBody.put("password", password);
        requestBody.put("info", info);
        final Request request = new Request.Builder()
                .url(apiUrl + "/modifyUserInfo")
                .put(RequestBody.create(requestBody.toString(), JSON))
                .build();
        expectSuccess(call(request));
    }

    private void createAccount() throws DataAccessException {
        final JSONObject requestBody = new JSONObject();
        requestBody.put("username", account);
        requestBody.put("password", password);
        final Request request = new Request.Builder()
                .url(apiUrl + "/user")
                .post(RequestBody.create(requestBody.toString(), JSON))
                .build();
        expectSuccess(call(request));
    }

    private JSONObject call(Request request) throws DataAccessException {
        try (Response response = client.newCall(request).execute()) {
            return new JSONObject(response.body().string());
        }
        catch (IOException | JSONException ex) {
            throw new DataAccessException("could not reach the account server", ex);
        }
    }

    private static void expectSuccess(JSONObject body) throws DataAccessException {
        if (body.getInt(STATUS_CODE) != SUCCESS_CODE) {
            throw new DataAccessException(body.optString(MESSAGE, "status " + body.getInt(STATUS_CODE)));
        }
    }
}
