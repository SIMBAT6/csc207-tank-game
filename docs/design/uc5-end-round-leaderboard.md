# UC5 End Round & View Leaderboard — Design Notes

Diagram: [class](../diagrams/uc5-end-round-leaderboard-class.puml) ([PNG](../diagrams/uc5-end-round-leaderboard-class.png))

## Scope

UC5 is two use cases, because they start in different places:

| Use case | Triggered by | Does |
|---|---|---|
| **End Round** (`use_case.end_round`) | the game screen, when Control Tank (UC3) reports the round is over | give the survivor a point → save each logged-in player's win/loss through the API → show the banner → after 2.5 s start the next round, or show the match winner if someone reached `winsToWin` |
| **View Leaderboard** (`use_case.view_leaderboard`) | the **Leaderboard** button on the main menu (and **Refresh**) | load every saved record from the API → rank → show the table |

Out of scope: deciding who survived (UC3), creating the match / next round and `winsToWin` (UC2), logging in (UC1).

## Workflow

**End Round**

1. `GameView` sees `roundOver` from UC3 and calls `EndRoundController.execute(winner)` (`Match.PLAYER_1`, `PLAYER_2` or `DRAW`).
2. `EndRoundInteractor` gets the current `Match` and calls `match.endRound(winner)` (score +1, or nothing on a draw).
3. For each side that is not a guest: `player.recordWin()` / `recordLoss()`, then `statsDataAccess.recordResult(username, won)`.
   If saving throws `DataAccessException`, the round still counts and `resultsSaved = false`.
4. `match.isMatchOver()`? → `prepareMatchOverView`, else `prepareRoundOverView`.
5. `EndRoundPresenter` writes score, banner and warning into `EndRoundState`; `RoundResultOverlay` repaints and,
   if the match is not over, starts the next round after `ROUND_DELAY_MS`.

**View Leaderboard**

1. `MainMenuView` → `ViewLeaderboardController.execute()`.
2. `ViewLeaderboardInteractor` calls `dataAccess.getAllPlayers()`, ranks them with the `Leaderboard` entity,
   and builds `ViewLeaderboardOutputData` rows (rank, username, wins, losses).
3. `ViewLeaderboardPresenter` fills `LeaderboardState` and switches the screen to `LeaderboardView`;
   on `DataAccessException` it shows the error on the same screen instead.

## Key decisions

| Decision | Why |
|---|---|
| **Two use cases, two interactors** | They have different triggers, inputs and outputs (SRP). They share one data access object. |
| **Winner comes in as input data from UC3** | UC3 already computes `winnerId` for the round; End Round doesn't re-check tanks, so it doesn't depend on `Tank`. |
| **Scoring and match-over rules live in `Match`** (`endRound`, `isMatchOver`, `getMatchWinner`) | Entity rules are easy to unit test; the interactor only orchestrates. |
| **Ranking rule lives in the `Leaderboard` entity**: wins ↓, then losses ↑, then username | One place to change the rule. |
| **All records are stored in one shared API account** (`ricochet-tanks-leaderboard`), info = `{"players": {"alice": {"wins": 3, "losses": 1}}}` | The course API has no "list all users" call, so per-player info can't be ranked. One account can be read with one GET. The account is created on first use. |
| **`recordResult` reads, increments and writes** instead of sending the local `Player` totals | The saved record stays right even if the local `Player` was created with old numbers. |
| **API failure never blocks the game** | Alternative flow: scores still update on screen; the banner shows a "not saved" warning. |
| **Guests are skipped** | Alternative flow: guest results are not recorded. |
| **`DataAccessException` is checked and lives in `use_case`** | Interactors must handle a failed save/load; any use case can reuse it. |
| **Output data and states hold plain values**, never entities | Outer layers can't change game state by accident. |

Known limits: the shared account's password is in the source (fine for a course project — anyone with the repo can edit
the leaderboard); two games saving at the exact same moment can overwrite each other's increment.

## Agreements needed from other leads

- **UC2:** call `match.setWinsToWin(1/2/3)` for BO1/BO3/BO5; give `RoundResultOverlay.setNextRoundAction(...)` the
  "start next round" call; `InMemoryMatchDataAccessObject` implements `EndRoundMatchDataAccessInterface`.
- **UC3:** when the round is over, `GameView` calls `EndRoundController.execute(winnerId)` once, using
  `Match.PLAYER_1` / `PLAYER_2` / `DRAW` (= −1, same as UC3's draw value), and calls `roundResultOverlay.paint(...)`
  at the end of `paintComponent`.
- **UC1:** logged-in players are `Player(username, false, …)`, guests `Player(…, true, …)`.

## Tests

- `MatchEndRoundTest` — point to the winner, draw, bad winner, BO1/BO3 match over, `getPlayer`.
- `LeaderboardTest` — order by wins, ties by losses then name, empty, copy.
- `EndRoundInteractorTest` (fake DAOs + recording presenter) — both results saved, P2 wins, draw saves nothing,
  guest not saved, server down still scores but warns, reaching `winsToWin` ends the match.
- `ViewLeaderboardInteractorTest` — ranked rows, empty, server down.
- `EndRoundPresenterTest`, `ViewLeaderboardPresenterTest` — banner / warning text, table rows, screen switch.
- `DBPlayerStatsDataAccessObject` was run by hand against the real API (create account, save 4 results, read back).
