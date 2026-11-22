# UC4 Collect Power-Up — Design Notes

Diagram: [class](../diagrams/uc4-collect-power-up-class.puml) ([PNG](../diagrams/uc4-collect-power-up-class.png))

## Scope

Collect Power-Up runs on the game screen every tick, right after Control Tank (UC3). It spawns power-ups
on the map, gives one to a tank that drives over it, takes it away again when it runs out, and shows
what each player holds.

Out of scope: moving tanks, firing and bullet collisions (UC3 — UC4 only swaps the tank's `Weapon` and
shield), creating the match and starting rounds (UC2), scores (UC5).

## Power-ups

| Power-up | Effect | Lasts | How it plugs into the tank |
|---|---|---|---|
| **Shotgun** | 5 bullets in a fan (−30°, −15°, 0°, 15°, 30°) | 3 shots or 10 s | `ShotgunPowerUp` is a `Weapon`; `tank.setWeapon(this)` |
| **Cannon** | one big shot that flies through walls | 1 shot or 10 s | `CannonPowerUp` is a `Weapon` that fires a `CannonBullet` (overrides `hitsWall` → `false`) |
| **Shield** | survives one hit | until hit, or 5 s | `tank.setShielded(true)`; `Tank.takeHit()` uses it up |

## Workflow

1. `GameView` runs the tick action registered in `AppBuilder.addCollectPowerUpUseCase()`:
   `CollectPowerUpController.execute(gameView.getTickCount())`.
2. `CollectPowerUpInteractor` gets the current `Match` from `CollectPowerUpDataAccessInterface`
   (does nothing before a match starts) and, for both tanks:
   1. **Expire:** `held.tick()`; if `held.isExpired(tank)` (time up, ammo used, or shield used up),
      `held.removeFrom(tank)` puts back the `StandardWeapon` / removes the shield. *(alt flow 2)*
   2. **Spawn:** every `SPAWN_INTERVAL` (300) ticks, if fewer than `MAX_POWER_UPS_ON_MAP` (3) are on the map,
      pick a random maze cell that is not a wall and is at least `MIN_SPAWN_DISTANCE` from tanks and other
      power-ups, and add `PowerUpFactory.createRandom(x, y)` to `match.getPowerUps()`. *(main 1, alt flow 3)*
   3. **Collect:** if a living tank touches a power-up, remove it from the map, `removeFrom` the old one,
      `applyTo` the new one and remember it with `setActivePowerUp`. *(main 2–3, alt flow 1)*
3. The interactor sends `CollectPowerUpOutputData` (map items, each player's power-up and ticks left).
4. `CollectPowerUpPresenter` turns it into `PowerUpState` (icons, `"Shotgun (8s)"` labels) and fires
   `PowerUpViewModel`. *(main 4)*
5. `PowerUpPainter.paintItems` draws the icons on the `ITEMS` layer; `paintStatus` draws "P1: …" / "P2: …"
   on the `OVERLAY` layer.
6. When the player fires, UC3's `tank.fire()` calls the installed power-up weapon. *(main 5)*

## Key decisions

| Decision | Why |
|---|---|
| Power-ups change the tank only through `setWeapon` / `setShielded` | These are the hooks UC3 built for UC4, so UC4 never edits `Tank` or UC3's code. |
| Shotgun and Cannon **are** `Weapon`s (Strategy pattern) | The weapon counts its own ammo when the tank fires, so the interactor doesn't need to know about firing. |
| `CannonBullet` overrides `Bullet.hitsWall` | Uses the hook `Bullet` already provides; normal bullets are unchanged. |
| `PowerUpFactory` (Factory pattern) | The only class that knows the concrete types. A new power-up = one subclass + one `case` (Open/Closed). |
| Active power-ups are kept by the data access object, not on `Tank` | `Tank` stays UC3's; the map is keyed by tank object, so a new round's tanks start with nothing. |
| The interactor finds empty cells itself (cell centre not inside a wall) | `Maze.getRandomEmptyCell()` is still a UC2 TODO; this keeps UC4 independent of it. |

## Hand-off to other use cases

| Who | What |
|---|---|
| UC2 (Start Match) | When a match starts, call `powerUpDataAccessObject.setCurrentMatch(match)` in `AppBuilder` — or let `InMemoryMatchDataAccessObject` implement `CollectPowerUpDataAccessInterface` once it exists. Clear `match.getPowerUps()` when a new round starts. |
| UC3 (Control Tank) | Nothing new: keep calling `tank.fire()` and `tank.takeHit()`; `addControlTankUseCase()` must come before `addCollectPowerUpUseCase()` in `Main`. |
| UC5 (scores) | Power-up status is drawn at the top-left / top-right corners; keep scores clear of those spots. |

## Tests

- `entity/PowerUpTest` — shotgun spread and ammo, standard weapon restored, cannon ball passes a wall that a
  normal bullet bounces off, shield absorbs one hit, duration expiry, factory.
- `use_case/collect_power_up/CollectPowerUpInteractorTest` — spawn tick, no spawn on other ticks, full map,
  no spawn next to a tank, collect, replace, used-up power-up gives the standard weapon back, no match yet.
