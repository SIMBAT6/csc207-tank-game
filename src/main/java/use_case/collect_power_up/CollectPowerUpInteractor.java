package use_case.collect_power_up;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import entity.Match;
import entity.Maze;
import entity.PowerUp;
import entity.PowerUpFactory;
import entity.Tank;

/**
 * UC4 Collect Power-Up. Runs once per game tick, after Control Tank (UC3):
 * <ol>
 *   <li>count down the power-up each tank holds and take it away when it expires
 *       (a shrunk tank keeps trying to grow back until there is room),</li>
 *   <li>every {@link #SPAWN_INTERVAL} ticks, spawn a random power-up at a random empty cell,</li>
 *   <li>give a power-up to any tank that drives over it (replacing the one it held),</li>
 *   <li>send the new state to the presenter, including the laser aim line.</li>
 * </ol>
 */
public class CollectPowerUpInteractor implements CollectPowerUpInputBoundary {
    /** Spawn attempt every 5 seconds at 60 ticks per second. */
    public static final int SPAWN_INTERVAL = 300;
    public static final int MAX_POWER_UPS_ON_MAP = 3;
    /** Radius of a power-up on the map, for pick-up collisions (pixels). */
    public static final double POWER_UP_RADIUS = 10;
    /** Don't spawn a power-up this close to a tank or another power-up (pixels). */
    public static final double MIN_SPAWN_DISTANCE = 40;

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
        final Match match = gameState.getCurrentMatch();
        if (match == null) {
            presenter.prepareSuccessView(new CollectPowerUpOutputData(
                    List.of(), List.of("", ""), List.of(0, 0), List.of(List.of(), List.of()), -1));
            return;
        }
        final List<Tank> tanks = List.of(match.getTank1(), match.getTank2());

        updateActivePowerUps(tanks, match.getMaze());
        int collectedBy = -1;
        if (!match.isRoundOver()) {
            if (inputData.getTick() > 0 && inputData.getTick() % SPAWN_INTERVAL == 0) {
                trySpawnPowerUp(match, tanks);
            }
            collectedBy = checkCollection(match, tanks);
        }
        presenter.prepareSuccessView(buildOutputData(match, tanks, collectedBy));
    }

    /**
     * Alt flow: the duration runs out or the ammo is used -> the tank goes back to normal.
     * If it can't yet (a shrunk tank next to a wall), it stays held and is tried again next tick.
     */
    private void updateActivePowerUps(List<Tank> tanks, Maze maze) {
        for (Tank tank : tanks) {
            final PowerUp held = gameState.getActivePowerUp(tank);
            if (held != null) {
                held.tick();
                if (held.isExpired(tank) && held.removeFrom(tank, maze)) {
                    gameState.setActivePowerUp(tank, null);
                }
            }
        }
    }

    /** Main flow step 1. Alt flow: the map already has the maximum number -> no spawn. */
    private void trySpawnPowerUp(Match match, List<Tank> tanks) {
        if (match.getPowerUps().size() >= MAX_POWER_UPS_ON_MAP) {
            return;
        }
        final List<double[]> candidates = new ArrayList<>();
        for (double[] cell : emptyCellCentres(match.getMaze())) {
            if (isFreeSpot(cell[0], cell[1], match, tanks)) {
                candidates.add(cell);
            }
        }
        if (!candidates.isEmpty()) {
            final double[] spot = candidates.get(random.nextInt(candidates.size()));
            match.getPowerUps().add(powerUpFactory.createRandom(spot[0], spot[1]));
        }
    }

    /** Centres of all maze cells where a power-up would not overlap a wall. */
    private static List<double[]> emptyCellCentres(Maze maze) {
        final List<double[]> cells = new ArrayList<>();
        final int size = maze.getCellSize();
        for (int row = 0; row < maze.getRows(); row++) {
            for (int col = 0; col < maze.getCols(); col++) {
                final double x = col * size + size / 2.0;
                final double y = row * size + size / 2.0;
                if (!maze.blocksCircle(x, y, POWER_UP_RADIUS)) {
                    cells.add(new double[] {x, y});
                }
            }
        }
        return cells;
    }

    private static boolean isFreeSpot(double x, double y, Match match, List<Tank> tanks) {
        for (Tank tank : tanks) {
            if (distance(x, y, tank.getX(), tank.getY()) < MIN_SPAWN_DISTANCE) {
                return false;
            }
        }
        for (PowerUp powerUp : match.getPowerUps()) {
            if (distance(x, y, powerUp.getX(), powerUp.getY()) < MIN_SPAWN_DISTANCE) {
                return false;
            }
        }
        return true;
    }

    /**
     * Main flow steps 2-3. Alt flow: the tank already holds a power-up -> the new one replaces it.
     *
     * @return index (0 = Player 1, 1 = Player 2) of the last player who collected one, or -1
     */
    private int checkCollection(Match match, List<Tank> tanks) {
        int collectedBy = -1;
        for (int i = 0; i < tanks.size(); i++) {
            final Tank tank = tanks.get(i);
            if (!tank.isAlive()) {
                continue;
            }
            for (PowerUp powerUp : new ArrayList<>(match.getPowerUps())) {
                final double reach = tank.getRadius() + POWER_UP_RADIUS;
                if (distance(tank.getX(), tank.getY(), powerUp.getX(), powerUp.getY()) < reach) {
                    final PowerUp old = gameState.getActivePowerUp(tank);
                    if (old != null && !old.removeFrom(tank, match.getMaze())) {
                        // A shrunk tank with no room to grow back keeps its power-up for now.
                        break;
                    }
                    match.getPowerUps().remove(powerUp);
                    powerUp.applyTo(tank);
                    gameState.setActivePowerUp(tank, powerUp);
                    collectedBy = i;
                    break;
                }
            }
        }
        return collectedBy;
    }

    /** Main flow step 4: what the map and the HUD should show. */
    private CollectPowerUpOutputData buildOutputData(Match match, List<Tank> tanks, int collectedBy) {
        final List<CollectPowerUpOutputData.MapItem> items = new ArrayList<>();
        for (PowerUp powerUp : match.getPowerUps()) {
            items.add(new CollectPowerUpOutputData.MapItem(
                    powerUp.getType().name(), powerUp.getX(), powerUp.getY()));
        }
        final List<String> active = new ArrayList<>();
        final List<Integer> remaining = new ArrayList<>();
        final List<List<double[]>> aimPaths = new ArrayList<>();
        for (Tank tank : tanks) {
            final PowerUp held = gameState.getActivePowerUp(tank);
            active.add(held == null ? "" : held.getType().name());
            remaining.add(held == null ? 0 : held.getDuration());
            if (held != null && tank.isAlive() && !match.isRoundOver()) {
                aimPaths.add(held.getAimPath(tank, match.getMaze()));
            }
            else {
                aimPaths.add(List.of());
            }
        }
        return new CollectPowerUpOutputData(items, active, remaining, aimPaths, collectedBy);
    }

    private static double distance(double x1, double y1, double x2, double y2) {
        return Math.hypot(x1 - x2, y1 - y2);
    }
}
