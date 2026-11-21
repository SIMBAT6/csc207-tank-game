package use_case.collect_power_up;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import entity.LaserPowerUp;
import entity.PowerUp;
import entity.PowerUpFactory;
import entity.ShieldPowerUp;
import entity.ShotgunPowerUp;
import entity.Tank;

class CollectPowerUpInteractorTest {

    /** In-memory game state. */
    private static class FakeGameState implements CollectPowerUpDataAccessInterface {
        final List<Tank> tanks = new ArrayList<>();
        final List<PowerUp> onMap = new ArrayList<>();
        final List<double[]> emptyCells = new ArrayList<>();

        public List<Tank> getTanks() {
            return tanks;
        }

        public List<PowerUp> getPowerUpsOnMap() {
            return new ArrayList<>(onMap);
        }

        public void addPowerUp(PowerUp p) {
            onMap.add(p);
        }

        public void removePowerUp(PowerUp p) {
            onMap.remove(p);
        }

        public List<double[]> getEmptyCellCentres() {
            return emptyCells;
        }
    }

    private static Tank newTank(int id, double x, double y) {
        return new Tank(id, x, y, 0, 2.0, 5);
    }

    private FakeGameState state;
    private CollectPowerUpOutputData lastOutput;
    private CollectPowerUpInteractor interactor;

    @BeforeEach
    void setUp() {
        state = new FakeGameState();
        state.tanks.add(newTank(0, 0, 0));
        state.tanks.add(newTank(1, 500, 500));
        final Random random = new Random(42);
        CollectPowerUpOutputBoundary presenter = output -> lastOutput = output;
        interactor = new CollectPowerUpInteractor(state, presenter, new PowerUpFactory(random), random);
    }

    @Test
    void spawnsPowerUpOnSpawnTickAtEmptyCell() {
        state.emptyCells.add(new double[] {200, 200});
        interactor.execute(new CollectPowerUpInputData(CollectPowerUpInteractor.SPAWN_INTERVAL_TICKS));
        assertEquals(1, state.onMap.size());
        assertEquals(200, state.onMap.get(0).getX());
        assertEquals(1, lastOutput.getMapItems().size());
    }

    @Test
    void doesNotSpawnOnOtherTicks() {
        state.emptyCells.add(new double[] {200, 200});
        interactor.execute(new CollectPowerUpInputData(1));
        assertTrue(state.onMap.isEmpty());
    }

    @Test
    void doesNotSpawnWhenMapIsFull() {
        for (int i = 0; i < CollectPowerUpInteractor.MAX_POWERUPS_ON_MAP; i++) {
            state.onMap.add(new ShieldPowerUp(100 + i * 100, 300));
        }
        state.emptyCells.add(new double[] {200, 200});
        interactor.execute(new CollectPowerUpInputData(CollectPowerUpInteractor.SPAWN_INTERVAL_TICKS));
        assertEquals(CollectPowerUpInteractor.MAX_POWERUPS_ON_MAP, state.onMap.size());
    }

    @Test
    void doesNotSpawnOnTopOfTank() {
        state.emptyCells.add(new double[] {0, 0});
        interactor.execute(new CollectPowerUpInputData(CollectPowerUpInteractor.SPAWN_INTERVAL_TICKS));
        assertTrue(state.onMap.isEmpty());
    }

    @Test
    void tankDrivingOverPowerUpCollectsIt() {
        PowerUp shotgun = new ShotgunPowerUp(5, 5);
        state.onMap.add(shotgun);
        interactor.execute(new CollectPowerUpInputData(1));
        assertSame(shotgun, state.tanks.get(0).getPowerUp());
        assertTrue(state.onMap.isEmpty());
        assertEquals(0, lastOutput.getCollectedByPlayer());
        assertEquals("SHOTGUN", lastOutput.getActivePowerUps().get(0));
        assertEquals("", lastOutput.getActivePowerUps().get(1));
    }

    @Test
    void newPowerUpReplacesHeldOne() {
        state.tanks.get(0).applyPowerUp(new ShieldPowerUp(0, 0));
        PowerUp laser = new LaserPowerUp(3, 3);
        state.onMap.add(laser);
        interactor.execute(new CollectPowerUpInputData(1));
        assertSame(laser, state.tanks.get(0).getPowerUp());
    }

    @Test
    void powerUpExpiresWhenDurationRunsOut() {
        state.tanks.get(0).applyPowerUp(new ShieldPowerUp(0, 0));
        for (int t = 1; t <= ShieldPowerUp.DURATION_TICKS; t++) {
            interactor.execute(new CollectPowerUpInputData(t));
        }
        assertNull(state.tanks.get(0).getPowerUp());
    }

    @Test
    void powerUpExpiresWhenAmmoIsUsed() {
        PowerUp laser = new LaserPowerUp(0, 0);
        state.tanks.get(0).applyPowerUp(laser);
        laser.consumeAmmo();
        interactor.execute(new CollectPowerUpInputData(1));
        assertNull(state.tanks.get(0).getPowerUp());
    }
}
