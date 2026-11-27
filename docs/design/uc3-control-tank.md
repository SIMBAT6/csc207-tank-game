# UC3 Control Tank — Design Notes

Diagrams: [class](../diagrams/uc3-control-tank-class.puml) · [sequence](../diagrams/uc3-control-tank-sequence.puml)

## Scope

Every game tick: read both players' keys → rotate/drive tanks (sliding along walls) → fire →
move bullets (bouncing off walls, checking hits after every step) → apply hits → report the tanks and
bullets to draw, and the winner once the round is over.

Out of scope (other use cases): creating the maze, tanks and match (UC2), power-ups (UC4),
scoring, saving results and starting the next round (UC5), drawing walls (UC2).

## Status

| Part | PR | Status |
|---|---|---|
| Entity rules and power-up hooks: `Tank`, `Bullet`, `Weapon`, `StandardWeapon`, `Maze`/`Wall` collision | #8 | ✅ merged |
| Game screen framework: `GameView` tick actions and layered painters | #9 | ✅ merged |
| Use case: input/output data, boundaries, data access interface, `ControlTankInteractor` | PR 2 | ✅ this PR |
| Adapters and view: `KeyBindings`, controller, presenter, view model, `ControlTankPainter`, wiring in `AppBuilder` | PR 3 | ✅ this PR |

## Key decisions

| Decision | Why |
|---|---|
| **The Swing `Timer` lives in `GameView`**, which holds no game rules. UC3 plugs in with `gameView.addTickAction(...)` and `gameView.addPainter(GameLayer.TANKS, ...)` | The timer is a UI detail, so it stays in the outer layer. Every use case plugs in the same way without editing `GameView`. |
| **The controller maps key codes to a `TankCommand`** with `KeyBindings` (`drive` and `turn` are -1/0/1, `fire` is a boolean) | The use case never sees key codes. Custom key bindings (post-MVP Settings) would only change the controller. |
| **Game rules live in the entities** (`Tank.drive`, `Bullet.update`, `Maze.blocksCircle`). The interactor only runs the steps of a tick in order | The rules are easy to unit test, and the interactor stays short. |
| **Tanks and bullets collide as circles** (closest point on the wall rectangle) | Rotating never pushes a tank into a wall, and the check takes only a few lines. |
| **Only walls block movement**: every maze is enclosed by outer walls. A bullet that somehow ends up outside the maze expires | One rule for blocking. The edge of the map is not a special case. |
| **A tank that drives into a wall slides along it** (it tries the full step, then x only, then y only) | Driving feels smooth instead of sticking at corners. |
| **Bullets bounce axis by axis**: move in x and, if blocked, flip `velocityX`; then the same for y | Simple, and corners are handled correctly. |
| **Bullets leave from the barrel tip.** Weapons create them at the tank's centre, and in the same tick the interactor moves them along the barrel (`tank.getBarrelLength()`) with normal steps and hit checks. A bullet cannot hit its own tank until it has travelled `ARMING_DISTANCE` | Starting at the centre, a bullet stayed under a tank driving after it, so the tank seemed to catch it. Spawning straight at the tip could put a bullet inside a wall the barrel touches; stepping out bounces it off the wall instead. After arming, a bullet can bounce back and hit its owner, as in the genre. |
| **Bullets expire by `lifetime` only**: no bounce counter | One rule instead of two. |
| **Each bullet step is shorter than 2 × its radius.** Faster projectiles take more steps per tick (`getStepsPerTick()`), and hits are checked after every step | A bigger velocity would let a bullet skip over a thin wall or a tank. |
| **Firing has a cooldown (15 ticks) and a limit of 5 active bullets per tank** | Holding the fire key doesn't flood the maze. |
| **The tank fires through a `Weapon`** (strategy pattern). UC3 provides `StandardWeapon` | UC4 adds power-up weapons with `tank.setWeapon(...)` and no UC3 code changes (OCP). |
| **A hit calls `tank.takeHit()` and `bullet.onHitTank()`** | A shield absorbs one hit and is used up. A special bullet (e.g. a laser beam) decides for itself whether it disappears. |
| **UC3 detects the end of a round but does not score it**: fewer than 2 tanks alive means the round is over. The winner is `Match.PLAYER_1`, `Match.PLAYER_2` or `Match.DRAW` (both tanks destroyed on the same tick) | UC5 owns `Match.endRound(...)` and the scores, so there is one owner for each rule. |
| **Each use case has its own view model and painter** instead of one shared `GameState` | Leads don't edit each other's classes. The layers decide the drawing order (`MAZE` → `ITEMS` → `TANKS` → `OVERLAY`). |
| **Output data holds plain values**, never entities | Outer layers can't change the game state by accident. |

## Power-up hooks (for UC4)

UC4 changes a tank or bullet only through these hooks. No UC3 class needs editing.

| Effect | Hook |
|---|---|
| Different weapon (scatter shot, cannon, laser) | `tank.setWeapon(Weapon)`. `Weapon.createBullets(Tank)` returns the new bullets. |
| Shrink | `tank.setRadius(r)`. On expiry, call `tank.tryResetRadius(maze)` every tick until it returns `true`, so the tank never grows into a wall. |
| Shield | `tank.setShielded(true)`. `takeHit()` uses it up. |
| Special bullet | Subclass `Bullet` (the protected constructor sets `lifetime`) and override `getRadius()`, `getStepsPerTick()`, `hitsWall(Maze)` (e.g. to pierce walls), `onHitTank()` and `getPath()` (to draw a beam). A bullet that doesn't expire on a hit must make sure it doesn't hit the same tank on every step. |
| Aim line (dashed predicted path) | Drawn by UC4 on the `ITEMS` layer. |

## Tick order

`GameView` runs the tick actions in the order they were added, so `AppBuilder` adds Control Tank (UC3)
before Collect Power-Up (UC4). In one tick:

1. Get the match from `ControlTankDataAccessInterface`. Skip the tick if there is none or `match.isRoundOver()`.
2. For each tank and its command: `rotate(turn)`, `drive(drive, maze)`, `coolDown()`, then fire if
   `fire && canFire(activeBullets)`: move each new bullet along the barrel to its tip (normal steps with
   hit checks, see step 3) and add it to `match.getBullets()`.
3. For each bullet, `getStepsPerTick()` times (stop early if it expires): `update(maze)`, then for each tank
   `if (bullet.hits(tank)) { tank.takeHit(); bullet.onHitTank(); }`. Then `age()` once.
4. Remove expired bullets.
5. Present the frame (`prepareTickView`). If fewer than 2 tanks are alive, present the end of the round instead
   (`prepareRoundOverView`) with the winner.

## Handing the round to UC5

`ControlTankPresenter` puts `roundOver` and `winner` into `ControlTankState`. Until UC5 scores the round the
interactor reports it on every tick, so the presenter fires `ControlTankViewModel.ROUND_OVER` only on the first
of those ticks. In `AppBuilder`, a listener on that event calls `roundOverAction.accept(winner)`; End Round (UC5)
sets `roundOverAction = endRoundController::execute`, which calls `Match.endRound(...)`. From then on the
interactor skips its ticks.

The game loop keeps running: UC5's overlay needs repaints to show its banner and to start the next round,
which goes back to UC2.

## Tuning constants (in the entities)

| Constant | Value |
|---|---|
| `Tank.DEFAULT_RADIUS` | 14 px |
| `Tank.SPEED` | 3 px/tick |
| `Tank.ROTATION_SPEED` | 0.08 rad/tick |
| `Tank.BARREL_LENGTH_RATIO` | 1.4 × radius (bullets leave from the tip) |
| `Tank.FIRE_COOLDOWN` | 15 ticks |
| `Tank.MAX_BULLETS` | 5 |
| `Bullet.DEFAULT_RADIUS` | 3 px |
| `Bullet.SPEED` | 5.0 px/step (must stay below 2 × radius = 6, and above `Tank.SPEED`) |
| `Bullet.LIFETIME` | 375 ticks (6 s) |
| `Bullet.ARMING_DISTANCE` | 40 px |
| Tick length (`GameView`) | 16 ms (~60 ticks/s) |

## Agreements with other leads

- **UC2:**
  - `InMemoryMatchDataAccessObject` implements `ControlTankDataAccessInterface.getCurrentMatch()`. It already
    implements the same method for UC5.
  - Tank ids are 1 and 2 (`Match.PLAYER_1`, `Match.PLAYER_2`).
  - Every maze has outer walls.
  - UC2 draws the walls on the `MAZE` layer.
- **UC4:** uses only the hooks above, and adds its tick action after UC3's.
- **UC5:** `EndRoundController.execute(winner)` is called once per round, with the `Match` winner constants.
  UC5 calls `Match.endRound(...)`. UC3 does not.

## Tests

- ✅ `TankTest` (14): rotating, driving, wall blocking and sliding, cooldown, bullet limit, weapon, radius, shield.
- ✅ `BulletTest` (16): moving, bounces (vertical, horizontal, corner), lifetime, leaving the maze, hit reach,
  arming distance, subclass hooks.
- ✅ `WallTest` (5), `MazeTest` (6), `GameViewTest` (5).
- ✅ `ControlTankInteractorTest` (18), with a fake data access object and a recording presenter:
  - a tank moves and turns on its command;
  - a fired bullet leaves from the barrel tip, and bounces off a wall the barrel touches;
  - firing respects the cooldown and the bullet limit;
  - a bullet with several steps per tick is checked for hits after every step;
  - a hit destroys a tank and removes the bullet;
  - a shield absorbs one hit;
  - one tank destroyed means the other wins, both destroyed on the same tick means a draw;
  - UC4's cannon ball flies through a wall, and UC4's laser beam hits in one tick and stops at the tank;
  - nothing happens before a match starts or after the round is over.
- ✅ `KeyBindingsTest` (5): pressed keys become the right command, opposite keys cancel out, the controller
  builds both players' commands from the same keys.
- ✅ `ControlTankPresenterTest` (3): output data becomes sprites; the end of a round is announced once per round.
- ✅ `ControlTankPainterTest` (3): player colours, wrecks, bullets as dots and beams as lines.
