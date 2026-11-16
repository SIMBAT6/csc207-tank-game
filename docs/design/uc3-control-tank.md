# UC3 Control Tank — Design Notes

Diagrams: [class](../diagrams/uc3-control-tank-class.puml) · [sequence](../diagrams/uc3-control-tank-sequence.puml)

## Scope

Every game tick: read both players' keys → rotate/drive tanks (stopping at walls) → fire bullets →
move bullets (bouncing off walls) → destroy hit tanks → report whether the round is over.

Out of scope (other use cases): creating the maze and tanks (UC2), power-up weapons (UC4),
scoring and saving results (UC5).

## Key decisions

| Decision | Why |
|---|---|
| **Swing `Timer` lives in `GameView`** and calls `ControlTankController` every 16 ms | The timer is a UI-framework detail, so it stays in the outermost layer. |
| **Controller maps key codes → `TankCommand`** via `KeyBindings` | The use case never sees key codes. Custom key bindings (post-MVP Settings) only change the controller. |
| **Game rules live in entities** (`Tank.drive`, `Bullet.update`, `Maze.blocksCircle`); the interactor only orchestrates the tick | Rules are easy to unit test; the interactor stays short. |
| **Tanks and bullets collide as circles** | Rotating never pushes a tank into a wall, and circle–rectangle checks are a few lines. |
| **Bullets bounce axis by axis**: move in x, if blocked flip `velocityX`; then the same for y | Simple and handles corners correctly. |
| **Bullets expire by `lifetime` only** (no bounce counter) | One rule instead of two. *Changes the README entity list: remove `bouncesLeft`.* |
| **Tank fires through a `Weapon`** (strategy); UC3 provides `StandardWeapon` | UC4 adds power-up weapons with `tank.setWeapon(...)` — no UC3 code changes (OCP). |
| **Firing has a cooldown and a max of 5 active bullets per tank** | Holding the fire key doesn't flood the maze. |
| **`Match.isRoundOver()` is derived**: fewer than 2 tanks alive | No extra state to keep in sync. Both destroyed on the same tick ⇒ draw (`winnerId = -1`). |
| **Output data and `GameState` hold plain values**, never entities | Outer layers can't change game state by accident. |

## Tuning constants (in the entities)

| Constant | Start value |
|---|---|
| `Tank.RADIUS` | 14 px |
| `Tank.SPEED` | 2.0 px/tick |
| `Tank.ROTATION_SPEED` | 0.06 rad/tick |
| `Tank.FIRE_COOLDOWN` | 15 ticks |
| `Tank.MAX_BULLETS` | 5 |
| `Bullet.RADIUS` | 3 px |
| `Bullet.SPEED` | 4.0 px/tick |
| `Bullet.LIFETIME` | 600 ticks (~10 s) |

## Agreements needed from other leads

- **UC2:** `InMemoryMatchDataAccessObject` implements `ControlTankDataAccessInterface.getCurrentMatch()`;
  `Maze.blocksCircle(x, y, r)` and `Wall.intersectsCircle(x, y, r)` signatures.
- **UC4:** the `Weapon` interface (`createBullets(Tank)`) and `Tank.setWeapon(Weapon)`.
- **UC5:** `GameView` stops the timer and calls the End Round controller when `GameState.isRoundOver()`.
- **Everyone drawing on the game screen:** the fields of the shared `GameState`.

## Tests

- `TankTest` — rotation, driving, blocked by a wall, cooldown, bullet limit.
- `BulletTest` — moves each tick, bounces off vertical/horizontal walls, expires, hits a tank.
- `ControlTankInteractorTest` — uses a fake data access object and a recording presenter:
  tank moves on input, bullet fired, hit ends the round, simultaneous hits ⇒ draw.

## Implementation order (one small PR each)

1. Entities: `Weapon`, `StandardWeapon`, `Tank`, `Bullet`, `Wall`/`Maze` collision + tests.
2. Use case: input/output data, boundaries, data access interface, interactor + tests.
3. Adapters & view: `KeyBindings`, controller, presenter, `GameState`/`GameViewModel`, drawing in `GameView`,
   wiring in `AppBuilder`.
