# Ricochet Tanks *(working title)*

CSC207 Fall 2026 Final Project

## Team

| Member | GitHub | MVP Use Case Lead |
|---|---|---|
| TODO Member 1 | @TODO | UC1 – Log In / Sign Up |
| Weibo Yu | @asxyr | UC2 – Start Match |
| TODO Member 3 | @TODO | UC3 – Control Tank |
| TODO Member 4 | @TODO | UC4 – Collect Power-Up |
| TODO Member 5 | @TODO | UC5 – End Round & Leaderboard |

> **Workflow:** `main` is protected. All work happens on feature branches and is merged through Pull Requests
> with at least one review. Every PR should update this README if it changes the design or progress.

---

## Project Domain

**Domain:** Games / Entertainment

**Specification**

Ricochet Tanks is a top-down, two-player tank battle game built with Java Swing. Two **players** share one
keyboard; each controls a **tank** inside a **maze** made of **walls**. A tank can move forward and backward,
rotate, and fire **bullets**. Bullets travel in a straight line and **bounce** off walls until their lifetime
runs out, so players can hit opponents around corners — including themselves. When a tank is hit,
it is destroyed and the **round** ends; the surviving player earns a point.

During a round, **power-ups** spawn at random empty cells of the maze. A tank that drives over a power-up
**collects** it and gains a special weapon or ability for a limited time (e.g. a shotgun spread, a laser,
or a shield). Before playing, each player can **sign up** or **log in** to an **account**; at the end of every
round, the result is **recorded** to their account through an external API, and players can view a
**leaderboard** ranking accounts by wins.

---

## User Stories

1. **Account** — As a player, I want to sign up or log in before a match so that my wins are saved under my name.
2. **Start match** — As a player, I want to pick a map and a match format so that my friend and I can play a local two-player match on one keyboard.
3. **Control tank** — As a player, I want to drive my tank and fire bullets that bounce off walls so that I can outmanoeuvre my opponent.
4. **Power-ups** — As a player, I want power-ups to appear in the maze so that I can grab a special weapon and turn the fight around.
5. **Leaderboard** — As a player, I want each round's result recorded and to see a leaderboard so that I can compare my record with other players.

---

## Use Cases

### MVP

#### UC1: Log In / Sign Up — *Lead: TODO Member 1*
**Main Flow:**
1. From the start screen, a player enters a username and password and clicks **Log In**.
2. The login interactor checks the password against the saved account.
3. The system marks the player as logged in for the chosen side (Player 1 or Player 2) and shows their name on the menu.

**Alternative Flows:**
- The player clicks **Sign Up** instead: the system saves a new local account, then logs the player in.
- The username does not exist or the password is wrong: the system shows an error and stays on the login form.
- The username is already taken during sign up: the system shows an error.
- Local account storage is unavailable: the system shows an error and offers to play as a guest.

#### UC2: Start Match — *Lead: Weibo Yu*
**Main Flow:**
1. On the main menu, the player selects a map and a match format (BO1, BO3 or BO5) and clicks **Start**.
   There are 5 maps: BO1 and BO3 play maps chosen at random, and BO5 plays all five.
2. The system generates the maze for the selected map and places both tanks at valid spawn points.
3. The system creates a new match with the score at 0–0 and sets the round wins needed to win the match:
   1 for BO1, 2 for BO3, 3 for BO5.
4. The system switches to the game screen and starts the first round.

**Alternative Flows:**
- **Random map:** the player chooses **Random**: the system generates a new random maze.
- **Guest player:** each side that is logged in plays with its account; a side that is not logged in plays as a guest.

#### UC3: Control Tank — *Lead: TODO Member 3*
**Main Flow:**
1. During a round, a player holds a movement key (Player 1: `W A S D`, Player 2: arrow keys).
2. On every game tick, the system moves/rotates that tank, sliding it along walls instead of through them.
3. The player presses the fire key (Player 1: `Q`, Player 2: `M`).
4. The system fires the tank's current weapon; the bullet starts at the tank's centre.
5. On every game tick, the system moves all bullets, bouncing them off walls.
6. The system redraws the game screen.

**Alternative Flows:**
- The tank already has the maximum number of bullets on screen, or fired too recently: no bullet is fired.
- A bullet's lifetime runs out: the system removes it.
- A bullet hits a tank: the system destroys that tank and the round ends (continues in UC5).
- A bullet hits a tank with a shield: the shield is used up and the tank survives.
- A bullet bounces back to the tank that fired it: that tank is hit too.
- Both tanks are destroyed on the same tick: the round is a draw (continues in UC5).

#### UC4: Collect Power-Up — *Lead: TODO Member 4*
**Main Flow:**
1. During a round, the system periodically spawns a power-up at a random empty cell.
2. A player drives their tank over the power-up.
3. The system removes the power-up from the maze and gives its effect to that tank.
4. The system shows the active power-up next to that player's score.
5. The next time the player fires, the power-up's weapon is used.

**Alternative Flows:**
- The tank already holds a power-up: the new one replaces it.
- The power-up's duration runs out or its ammo is used: the tank returns to its normal weapon.
- The maximum number of power-ups is already on the map: no new one spawns.

#### UC5: End Round & View Leaderboard — *Lead: TODO Member 5*
**Main Flow:**
1. A tank is destroyed during a round.
2. The system awards a point to the surviving player and shows the updated scores.
3. The system records the result (win/loss) for each logged-in player through the API.
4. After a short delay, the system starts a new round (back to UC2 step 2).
5. From the main menu, a player clicks **Leaderboard**; the system fetches all accounts' records and shows them ranked by wins
   (ties: fewer losses first, then by username).

**Alternative Flows:**
- Both tanks are destroyed at the same time: the round is a draw and no point is awarded.
- A player is a guest: their result is not recorded.
- The API cannot be reached: scores still update on screen, and the system shows a warning that results were not saved.
- A player reaches the wins needed for the match format (UC2's BO1/BO3/BO5): the system shows the match winner instead
  of starting a new round; Esc returns to the main menu.
- The leaderboard cannot be loaded: the leaderboard screen shows an error and a **Refresh** button.

Design notes: [`docs/design/uc5-end-round-leaderboard.md`](docs/design/uc5-end-round-leaderboard.md) ·
class diagram: [`docs/diagrams/uc5-end-round-leaderboard-class.puml`](docs/diagrams/uc5-end-round-leaderboard-class.puml)

### Post-MVP (if time permits)

- **Play vs. AI** — a single player fights a computer-controlled tank.
- **Map Editor** — draw a custom maze and save/load it as a file.
- **Settings** — customise key bindings and volume; saved to a local file.
- **Match History** — view a player's past rounds.

---

## Entities

See [`docs/diagrams/entities.puml`](docs/diagrams/entities.puml) for the UML class diagram.

**`Tank`** *(collides as a circle of radius `getRadius()`)*
- Constants: `DEFAULT_RADIUS`, `SPEED`, `ROTATION_SPEED`, `FIRE_COOLDOWN`, `MAX_BULLETS`
- Variables: `id`, `x`, `y`, `angle`, `radius`, `alive`, `shielded`, `cooldown`, `weapon: Weapon`
- Methods: `rotate(direction)`, `drive(direction, Maze)`, `coolDown()`, `canFire(activeBullets)`, `fire(): List<Bullet>`, `takeHit(): boolean`, `destroy()`
- Power-up hooks: `setWeapon(Weapon)`, `setRadius(r)`, `tryResetRadius(Maze)`, `setShielded(boolean)` (a shield absorbs one hit)

**`Weapon`** *(interface; `StandardWeapon` fires one bullet, power-ups add more)*
- Methods: `createBullets(Tank): List<Bullet>`

**`Bullet`** *(collides as a circle of radius `getRadius()`; subclassed by power-ups)*
- Constants: `DEFAULT_RADIUS`, `SPEED`, `LIFETIME`, `ARMING_DISTANCE`
- Variables: `ownerId`, `x`, `y`, `velocityX`, `velocityY`, `lifetime`, `distanceTravelled`
- Methods: `update(Maze)` (one step: move + bounce), `age()` (once per tick), `hits(Tank)`, `isExpired()`, `expire()`
- Overridable hooks: `getRadius()`, `getStepsPerTick()`, `hitsWall(Maze)`, `onHitTank()`, `getPath()`

**`Maze`**
- Variables: `rows`, `cols`, `cellSize`, `walls: List<Wall>`
- Methods: `blocksCircle(x, y, radius)` (walls only — the maze must be enclosed by outer walls), `contains(x, y)`, `getWidth()`, `getHeight()`, `getRandomEmptyCell()`, `getSpawnPoints()`

**`Wall`**
- Variables: `x`, `y`, `width`, `height`
- Methods: `intersectsCircle(x, y, radius)`

**`PowerUp`** *(abstract; e.g. `ShotgunPowerUp`, `LaserPowerUp`, `ShieldPowerUp`)*
- Variables: `x`, `y`, `type`, `duration`
- Methods: `fire(Tank): List<Bullet>`, `isExpired()`

**`Player`**
- Variables: `username`, `isGuest`, `wins`, `losses`
- Methods: `recordWin()`, `recordLoss()`

**`Match`**
- Variables: `player1`, `player2`, `tank1`, `tank2`, `maze`, `bullets`, `powerUps`, `score1`, `score2`, `roundOver`, `winsToWin`
- Methods: `tick()`, `startRound()`, `endRound(winner)`, `isMatchOver()`, `getMatchWinner()`, `getPlayer(side)`

**`Leaderboard`**
- Variables: `ranking: List<Player>`
- Methods: `getRanking()` (wins high to low, then fewer losses, then username)

---

## API Usage

| API | Documentation | Status |
|---|---|---|
| CSC207 course user/password + info API (`https://csc207-20269-vm.teach.cs.toronto.edu`, used by the `DBUserDataAccessObject` example) | [Postman docs](https://www.postman.com/cloudy-astronaut-813156/csc207-apis-2026/collection/puzltp3/5-password-protected-user) | ✅ Called (UC5: `GET /user`, `POST /user`, `PUT /modifyUserInfo`) |

**How we use it:**
- **Sign up / log in** (UC1): accounts are saved locally in `~/.ricochet-tanks/accounts.properties` with salted password hashes. Player selections are held in memory for the current run. Course API integration is planned.
- **Save results** (UC5): the API cannot list all users, so every player's `wins` and `losses` are stored in the info
  JSON of one shared account, `ricochet-tanks-leaderboard`: `{"players": {"alice": {"wins": 3, "losses": 1}}}`.
- **Leaderboard** (UC5): read that one account's info and rank the players by wins.
- Results are counted **per round**: every round won adds 1 win, every round lost adds 1 loss (a BO3 won 2:1 gives the
  winner 2 wins, 1 loss). Draws and guests are not recorded.
- API calls for the leaderboard run on a background thread (`SwingWorker`) so the window doesn't freeze.

This API satisfies both the **external API** and the **data persistence** requirements.

---

## How to Run

1. Open the project in IntelliJ and load it as a Maven project.
2. Use a JDK of version 11 or newer, with the project language level set to 11.
3. Run `app.Main`.
4. From the main menu, click **Log In**, choose Player 1 or Player 2, and sign up, log in, or continue as a guest.

Run the tests with `mvn test`, or right-click `src/test/java` → **Run 'All Tests'** in IntelliJ.

Controls (driving and firing work once Control Tank is merged): Player 1 — `W A S D` to drive, `Q` to fire ·
Player 2 — arrow keys to drive, `M` to fire · `Esc` — back to the menu.

## Development Notes

- Diagrams are written in PlantUML and stored in [`docs/diagrams`](docs/diagrams).
- Package structure follows Clean Architecture: `entity`, `use_case`, `interface_adapter`, `data_access`, `view`, `app`.

### Plugging a use case into the game screen

`GameView` runs the game loop but contains no game rules. Each use case connects to it in its own
`addXxxUseCase()` method in `AppBuilder`. Nobody needs to edit `GameView`.

- **Every tick:** `gameView.addTickAction(() -> controller.execute(...))`. Actions run in the order they
  were added, so the order of the `addXxxUseCase()` calls in `Main` matters: Control Tank (UC3) comes before
  Collect Power-Up (UC4). Use `gameView.getPressedKeys()` and `gameView.getTickCount()` for input.
- **Drawing:** implement `GameLayerPainter` (usually reading your own view model's state), then call
  `gameView.addPainter(layer, painter)`. Layers are drawn back to front, whatever order they are added in:

  | Layer | Owner | What |
  |---|---|---|
  | `MAZE` | UC2 | walls |
  | `ITEMS` | UC4 | power-ups on the map, aim lines |
  | `TANKS` | UC3 | tanks, bullets, laser beams |
  | `OVERLAY` | UC4, UC5 | power-up status, scores, round banners |

  Each painter gets its own copy of the `Graphics2D`, so colours and strokes don't leak between painters.
