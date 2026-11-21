# UC4 – Collect Power-Up (design)

## Workflow (runs once per game tick, called from the game loop)

1. **Game loop** (UC3's Swing `Timer`) calls `CollectPowerUpController.execute(tick)`.
2. Controller wraps the tick in `CollectPowerUpInputData` and calls the interactor through `CollectPowerUpInputBoundary`.
3. `CollectPowerUpInteractor.execute`:
   1. `updateActivePowerUps()` counts down each held power-up; if expired (time or ammo), `tank.clearPowerUp()` → normal weapon. *(alt flow 2)*
   2. Every `SPAWN_INTERVAL_TICKS`, `trySpawnPowerUp()` picks a random empty cell (not near a tank or another power-up) and asks `PowerUpFactory` for a random power-up. Skipped if `MAX_POWERUPS_ON_MAP` already reached. *(main step 1, alt flow 3)*
   3. `checkCollection()` checks whether a tank is within `PICKUP_RADIUS` of a power-up: if so, removes it from the map and gives it to the tank with `tank.applyPowerUp(p)`, replacing any held one. *(main steps 2–3, alt flow 1)*
   4. Builds `CollectPowerUpOutputData` and calls `presenter.prepareSuccessView`.
4. `CollectPowerUpPresenter` formats HUD text (e.g. `Shotgun (8s)`) and icon positions into `PowerUpState`, then fires `PowerUpViewModel`. *(main step 4)*
5. The game panel redraws, calling `PowerUpPainter.paint(g, state)` and showing the HUD labels next to the scores.
6. When the player fires, UC3 asks the tank's `PowerUp` for `getShotAngleOffsets()` / `getBulletSpeedMultiplier()` and calls `consumeAmmo()`. *(main step 5)*

## Contract with other use cases

| Who | Needs to |
|-----|----------|
| UC3 – `Tank` | UC4 added `applyPowerUp`, `clearPowerUp`, `hasShield` (already in this PR) |
| UC3 – fire logic | If `tank.getPowerUp() != null`: one bullet per angle in `getShotAngleOffsets()`, speed × `getBulletSpeedMultiplier()`, then `consumeAmmo()` |
| UC3 – hit logic | If `tank.hasShield()`: ignore the hit |
| UC3 – game loop | Call `collectPowerUpController.execute(tick)` every tick |
| UC2 / shared DAO | Implement `CollectPowerUpDataAccessInterface`; clear power-ups when a new round starts |

## Design choices

- **Clean Architecture**: entities ← use case ← interface adapters ← view; the interactor only knows interfaces.
- **Open/Closed + Strategy**: a new power-up = one new `PowerUp` subclass and one line in `PowerUpFactory`.
- **Factory**: `PowerUpFactory` is the only class that knows the concrete power-up types.
- **Dependency Inversion**: the interactor depends on `CollectPowerUpDataAccessInterface`, not on a concrete DAO.
- **Observer**: `PowerUpViewModel` extends the team `ViewModel<T>` and notifies `GameView` on change.

## Class diagram

```mermaid
classDiagram
    direction TB

    %% ===== Entities =====
    class PowerUpType {
        <<enumeration>>
        SHOTGUN
        LASER
        SHIELD
    }
    class PowerUp {
        <<abstract>>
        +UNLIMITED_AMMO : int$
        -x : double
        -y : double
        -duration : int
        -type : PowerUpType
        -ammo : int
        +getX() double
        +getY() double
        +getDuration() int
        +getType() PowerUpType
        +getAmmo() int
        +tick() void
        +consumeAmmo() void
        +isExpired() boolean
        +getShotAngleOffsets()* double[]
        +getBulletSpeedMultiplier() double
        +providesShield() boolean
    }
    class ShotgunPowerUp {
        +getShotAngleOffsets() double[]
    }
    class LaserPowerUp {
        +getShotAngleOffsets() double[]
        +getBulletSpeedMultiplier() double
    }
    class ShieldPowerUp {
        +getShotAngleOffsets() double[]
        +providesShield() boolean
    }
    class PowerUpFactory {
        -random : Random
        +create(type, x, y) PowerUp
        +createRandom(x, y) PowerUp
    }
    class Tank {
        -id : int
        -x : double
        -y : double
        -powerUp : PowerUp
        ...UC3 fields
        +getX() double
        +getY() double
        +getPowerUp() PowerUp
        +applyPowerUp(PowerUp) void
        +clearPowerUp() void
        +hasShield() boolean
    }

    %% ===== Use case =====
    class CollectPowerUpInputBoundary {
        <<interface>>
        +execute(CollectPowerUpInputData) void
    }
    class CollectPowerUpInputData {
        -tick : long
        +getTick() long
    }
    class CollectPowerUpInteractor {
        +SPAWN_INTERVAL_TICKS : int$
        +MAX_POWERUPS_ON_MAP : int$
        +PICKUP_RADIUS : double$
        -gameState : CollectPowerUpDataAccessInterface
        -presenter : CollectPowerUpOutputBoundary
        -powerUpFactory : PowerUpFactory
        -random : Random
        +execute(CollectPowerUpInputData) void
        -updateActivePowerUps() void
        -trySpawnPowerUp() void
        -checkCollection() int
        -buildOutputData(int) CollectPowerUpOutputData
    }
    class CollectPowerUpDataAccessInterface {
        <<interface>>
        +getTanks() List~Tank~
        +getPowerUpsOnMap() List~PowerUp~
        +addPowerUp(PowerUp) void
        +removePowerUp(PowerUp) void
        +getEmptyCellCentres() List~double[]~
    }
    class CollectPowerUpOutputBoundary {
        <<interface>>
        +prepareSuccessView(CollectPowerUpOutputData) void
    }
    class CollectPowerUpOutputData {
        -mapItems : List~MapItem~
        -activePowerUps : List~String~
        -remainingTicks : List~Integer~
        -collectedByPlayer : int
    }

    %% ===== Interface adapters + view =====
    class CollectPowerUpController {
        -interactor : CollectPowerUpInputBoundary
        +execute(long tick) void
    }
    class CollectPowerUpPresenter {
        -viewModel : PowerUpViewModel
        +prepareSuccessView(CollectPowerUpOutputData) void
    }
    class ViewModel~T~ {
        team base class
        +getState() T
        +setState(T) void
        +firePropertyChanged() void
    }
    class PowerUpViewModel {
        +VIEW_NAME : String$
        +PowerUpViewModel()
    }
    class PowerUpState {
        -icons : List~Icon~
        -hudLabels : List~String~
    }
    class PowerUpPainter {
        +paint(Graphics2D, PowerUpState) void
    }
    class GameStateDataAccessObject {
        shared in-memory game state
    }

    PowerUp <|-- ShotgunPowerUp
    PowerUp <|-- LaserPowerUp
    PowerUp <|-- ShieldPowerUp
    PowerUp --> PowerUpType
    PowerUpFactory ..> PowerUp : creates
    Tank o-- PowerUp : holds 0..1

    CollectPowerUpInputBoundary <|.. CollectPowerUpInteractor
    CollectPowerUpOutputBoundary <|.. CollectPowerUpPresenter
    CollectPowerUpDataAccessInterface <|.. GameStateDataAccessObject
    CollectPowerUpInteractor --> CollectPowerUpDataAccessInterface
    CollectPowerUpInteractor --> CollectPowerUpOutputBoundary
    CollectPowerUpInteractor --> PowerUpFactory
    CollectPowerUpInteractor ..> CollectPowerUpInputData
    CollectPowerUpInteractor ..> CollectPowerUpOutputData
    CollectPowerUpInteractor ..> Tank

    CollectPowerUpController --> CollectPowerUpInputBoundary
    CollectPowerUpPresenter --> PowerUpViewModel
    ViewModel <|-- PowerUpViewModel
    PowerUpViewModel --> PowerUpState
    PowerUpPainter ..> PowerUpState
```
