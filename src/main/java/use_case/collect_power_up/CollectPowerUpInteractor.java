package use_case.collect_power_up;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import entity.PowerUp;
import entity.PowerUpFactory;
import entity.Tank;

/**
 * UC4 – Collect Power-Up. Runs once per game tick:
 * <ol>
 *   <li>count down the power-up each tank holds and remove it when it expires,</li>
 *   <li>periodically spawn a new power-up at a random empty cell,</li>
 *   <li>give a power-up to any tank that drives over it,</li>
 *   <li>send the new state to the presenter.</li>
 * </ol>
 */
public class CollectPowerUpInteractor implements CollectPowerUpInputBoundary {

    /** Spawn attempt every 5 seconds at 60 ticks/second. */
    public static final int SPAWN_INTERVAL_TICKS = 300;
    public static final int MAX_POWERUPS_ON_MAP = 3;
    /** A tank collects a power-up when their centres are closer than this (pixels). */
    public static final double PICKUP_RADIUS = 20.0;
    /** Don't spawn a power-up right on top of a tank or another power-up (pixels). */
    public static final double MIN_SPAWN_DISTANCE = 40.0;

    private final CollectPowerUpDataAccessInterface gameState;
    private final CollectPowerUpOutputBoundary presenter;
    private final PowerUpFactory powerUpFactory;
    private final Random random;

    public CollectPowerUpInteractor(CollectPowerUpDataAccessInterface gameState,
                                    CollectPowerUpOutputBoundary presenter,
                                    PowerUpFactory powerUpFactory,
                                    Random random) {
        this.gameState = gameState;
        this.presenter = presenter;
        this.powerUpFactory = powerUpFactory;
        this.random = random;
    }

    @Override
    public void execute(CollectPowerUpInputData inputData) {
        updateActivePowerUps();
        if (inputData.getTick() > 0 && inputData.getTick() % SPAWN_INTERVAL_TICKS == 0) {
            trySpawnPowerUp();
        }
        final int collectedBy = checkCollection();
        presenter.prepareSuccessView(buildOutputData(collectedBy));
    }

    /** Alt flow: duration runs out or ammo is used -> tank returns to its normal weapon. */
    private void updateActivePowerUps() {
        for (Tank tank : gameState.getTanks()) {
            final PowerUp held = tank.getPowerUp();
            if (held != null) {
                held.tick();
                if (held.isExpired()) {
                    tank.clearPowerUp();
                }
            }
        }
    }

    /** Main flow step 1. Alt flow: max power-ups already on the map -> no spawn. */
    private void trySpawnPowerUp() {
        if (gameState.getPowerUpsOnMap().size() >= MAX_POWERUPS_ON_MAP) {
            return;
        }
        final List<double[]> candidates = new ArrayList<>();
        for (double[] cell : gameState.getEmptyCellCentres()) {
            if (isFreeSpot(cell[0], cell[1])) {
                candidates.add(cell);
            }
        }
        if (candidates.isEmpty()) {
            return;
        }
        final double[] spot = candidates.get(random.nextInt(candidates.size()));
        gameState.addPowerUp(powerUpFactory.createRandom(spot[0], spot[1]));
    }

    private boolean isFreeSpot(double x, double y) {
        for (Tank tank : gameState.getTanks()) {
            if (distance(x, y, tank.getX(), tank.getY()) < MIN_SPAWN_DISTANCE) {
                return false;
            }
        }
        for (PowerUp powerUp : gameState.getPowerUpsOnMap()) {
            if (distance(x, y, powerUp.getX(), powerUp.getY()) < MIN_SPAWN_DISTANCE) {
                return false;
            }
        }
        return true;
    }

    /**
     * Main flow steps 2-3. Alt flow: tank already holds a power-up -> the new one replaces it.
     *
     * @return index of the last player who collected a power-up this tick, or -1
     */
    private int checkCollection() {
        int collectedBy = -1;
        final List<Tank> tanks = gameState.getTanks();
        for (int i = 0; i < tanks.size(); i++) {
            final Tank tank = tanks.get(i);
            for (PowerUp powerUp : new ArrayList<>(gameState.getPowerUpsOnMap())) {
                if (distance(tank.getX(), tank.getY(), powerUp.getX(), powerUp.getY())
                        < PICKUP_RADIUS) {
                    gameState.removePowerUp(powerUp);
                    tank.applyPowerUp(powerUp);
                    collectedBy = i;
                    break;
                }
            }
        }
        return collectedBy;
    }

    /** Main flow step 4: data for the HUD (active power-up next to each score). */
    private CollectPowerUpOutputData buildOutputData(int collectedBy) {
        final List<CollectPowerUpOutputData.MapItem> items = new ArrayList<>();
        for (PowerUp powerUp : gameState.getPowerUpsOnMap()) {
            items.add(new CollectPowerUpOutputData.MapItem(
                    powerUp.getType().name(), powerUp.getX(), powerUp.getY()));
        }
        final List<String> active = new ArrayList<>();
        final List<Integer> remaining = new ArrayList<>();
        for (Tank tank : gameState.getTanks()) {
            final PowerUp held = tank.getPowerUp();
            active.add(held == null ? "" : held.getType().name());
            remaining.add(held == null ? 0 : held.getDuration());
        }
        return new CollectPowerUpOutputData(items, active, remaining, collectedBy);
    }

    private static double distance(double x1, double y1, double x2, double y2) {
        return Math.hypot(x1 - x2, y1 - y2);
    }
}
