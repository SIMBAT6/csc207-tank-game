# Ricochet Tanks *(working title)*

CSC207 Fall 2026 Final Project

## Team

| Member | GitHub | MVP Use Case Lead |
|---|---|---|
| TODO Member 1 | @TODO | UC1 – Log In / Sign Up |
| TODO Member 2 | @TODO | UC2 – Start Match |
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
rotate, and fire **bullets**. Bullets travel in a straight line and **bounce** off walls a limited number of times
before disappearing, so players can hit opponents around corners — including themselves. When a tank is hit,
it is destroyed and the **round** ends; the surviving player earns a point.

During a round, **power-ups** spawn at random empty cells of the maze. A tank that drives over a power-up
**collects** it and gains a special weapon or ability for a limited time (e.g. a shotgun spread, a laser,
or a shield). Before playing, each player can **sign up** or **log in** to an **account**; at the end of every
round, the result is **recorded** to their account through an external API, and players can view a
**leaderboard** ranking accounts by wins.

---

## User Stories

1. **Account** — As a player, I want to sign up or log in before a match so that my wins are saved under my name.
2. **Start match** — As a player, I want to pick a map and start a local two-player match so that my friend and I can play on one keyboard.
3. **Control tank** — As a player, I want to drive my tank and fire bullets that bounce off walls so that I can outmanoeuvre my opponent.
4. **Power-ups** — As a player, I want power-ups to appear in the maze so that I can grab a special weapon and turn the fight around.
5. **Leaderboard** — As a player, I want each round's result recorded and to see a leaderboard so that I can compare my record with other players.

---

## Use Cases

### MVP

#### UC1: Log In / Sign Up — *Lead: TODO Member 1*
**Main Flow:**
1. From the start screen, a player enters a username and password and clicks **Log In**.
2. The system checks the credentials with the account API.
3. The system marks the player as logged in for the chosen side (Player 1 or Player 2) and shows their name on the menu.

**Alternative Flows:**
- The player clicks **Sign Up** instead: the system creates a new account through the API, then logs the player in.
- The username does not exist or the password is wrong: the system shows an error and stays on the login form.
- The username is already taken during sign up: the system shows an error.
- The API cannot be reached: the system shows an error and offers to play as a guest (results will not be saved).

#### UC2: Start Match — *Lead: TODO Member 2*
**Main Flow:**
1. On the main menu, the player selects a map and clicks **Start**.
2. The system generates the maze for that map.
3. The system places both tanks at spawn points far apart from each other.
4. The system switches to the game screen and starts the round.

**Alternative Flows:**
- The player chooses **Random**: the system generates a new random maze.
- A player is not logged in: the system starts the match with that side as a guest.

#### UC3: Control Tank — *Lead: TODO Member 3*
**Main Flow:**
1. During a round, a player holds a movement key (Player 1: `W A S D`, Player 2: arrow keys).
2. On every game tick, the system moves/rotates that tank, stopping it at walls.
3. The player presses the fire key (Player 1: `Q`, Player 2: `M`).
4. The system spawns a bullet in front of the tank.
5. On every game tick, the system moves all bullets, bouncing them off walls.
6. The system redraws the game screen.

**Alternative Flows:**
- The tank already has the maximum number of bullets on screen: no bullet is fired.
- A bullet has bounced its maximum number of times or has expired: the system removes it.
- A bullet hits a tank: the system destroys that tank and the round ends (continues in UC5).

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
3. The system records the result (win/loss) to each logged-in player's account through the API.
4. After a short delay, the system starts a new round (back to UC2 step 2).
5. From the main menu, a player clicks **Leaderboard**; the system fetches all accounts' records and shows them ranked by wins.

**Alternative Flows:**
- Both tanks are destroyed at the same time: the round is a draw and no point is awarded.
- A player is a guest: their result is not recorded.
- The API cannot be reached: scores still update on screen, and the system shows a warning that results were not saved.

### Post-MVP (if time permits)

- **Play vs. AI** — a single player fights a computer-controlled tank.
- **Map Editor** — draw a custom maze and save/load it as a file.
- **Settings** — customise key bindings and volume; saved to a local file.
- **Match History** — view a player's past rounds.

---

## Entities

See [`docs/diagrams/entities.puml`](docs/diagrams/entities.puml) for the UML class diagram.

**`Tank`**
- Variables: `id`, `x`, `y`, `angle`, `speed`, `alive`, `maxBullets`, `powerUp` (nullable)
- Methods: `moveForward()`, `moveBackward()`, `rotate(direction)`, `fire(): List<Bullet>`, `destroy()`, `applyPowerUp(PowerUp)`, `getBounds()`

**`Bullet`**
- Variables: `owner` (Tank id), `x`, `y`, `velocityX`, `velocityY`, `bouncesLeft`, `lifetime`
- Methods: `move()`, `bounceHorizontal()`, `bounceVertical()`, `isExpired()`, `hits(Tank)`

**`Maze`**
- Variables: `rows`, `cols`, `cellSize`, `walls: List<Wall>`
- Methods: `collidesWithWall(bounds)`, `getRandomEmptyCell()`, `getSpawnPoints()`

**`Wall`**
- Variables: `x`, `y`, `width`, `height`
- Methods: `intersects(bounds)`

**`PowerUp`** *(abstract; e.g. `ShotgunPowerUp`, `LaserPowerUp`, `ShieldPowerUp`)*
- Variables: `x`, `y`, `type`, `duration`
- Methods: `fire(Tank): List<Bullet>`, `isExpired()`

**`Player`**
- Variables: `username`, `isGuest`, `wins`, `losses`
- Methods: `recordWin()`, `recordLoss()`

**`Match`**
- Variables: `player1`, `player2`, `tank1`, `tank2`, `maze`, `bullets`, `powerUps`, `score1`, `score2`, `roundOver`
- Methods: `tick()`, `startRound()`, `endRound(winner)`

---

## API Usage

| API | Documentation | Status |
|---|---|---|
| CSC207 course user/password + info API (used by the `DBUserDataAccessObject` example) | TODO: add link from Quercus | ❌ Not yet called |

**How we use it:**
- **Sign up / log in** (UC1): create a user with a username and password, and check credentials when logging in.
- **Save results** (UC5): store a JSON object with each player's `wins` and `losses` in their user info.
- **Leaderboard** (UC5): read players' stats and rank them by wins.

This API satisfies both the **external API** and the **data persistence** requirements.

---

## How to Run

> TODO once the project skeleton exists — expected: Java 17+, Maven, run the `Main` class from IntelliJ.

## Development Notes

- Diagrams are written in PlantUML and stored in [`docs/diagrams`](docs/diagrams).
- Package structure follows Clean Architecture: `entity`, `use_case`, `interface_adapter`, `data_access`, `view`, `app`.
