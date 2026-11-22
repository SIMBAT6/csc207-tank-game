package use_case.collect_power_up;

import data_access.InMemoryPowerUpDataAccessObject;
import entity.CannonPowerUp;
import entity.LaserPowerUp;
import entity.Match;
import entity.Maze;
import entity.Player;
import entity.PowerUp;
import entity.PowerUpFactory;
import entity.ShieldPowerUp;
import entity.ShotgunPowerUp;
import entity.ShrinkPowerUp;
import entity.StandardWeapon;
import entity.Tank;
import entity.Wall;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CollectPowerUpInteractorTest {
    private InMemoryPowerUpDataAccessObject gameState;
    private Match match;
    private Tank tank1;
    private Tank tank2;
    private CollectPowerUpOutputData lastOutput;
    private CollectPowerUpInteractor interactor;

    @BeforeEach
    void setUp() {
        tank1 = new Tank(1, 25, 25, 0);
        tank2 = new Tank(2, 475, 475, 0);
        match = new Match(new Player("a", false, 0, 0), new Player("b", false, 0, 0), new Maze(10, 10, 50, List.of()), tank1, tank2);
        gameState = new InMemoryPowerUpDataAccessObject();
        gameState.setCurrentMatch(match);
        Random random = new Random(42);
        interactor = new CollectPowerUpInteractor(gameState, output -> lastOutput = output,
                new PowerUpFactory(random), random);
    }

    private void tick(long tick) {
        interactor.execute(new CollectPowerUpInputData(tick));
    }

    @Test
    void spawnsOnePowerUpOnSpawnTick() {
        tick(CollectPowerUpInteractor.SPAWN_INTERVAL);
        assertEquals(1, match.getPowerUps().size());
        assertEquals(1, lastOutput.getMapItems().size());
    }

    @Test
    void doesNotSpawnOnOtherTicks() {
        tick(1);
        assertTrue(match.getPowerUps().isEmpty());
    }

    @Test
    void doesNotSpawnWhenMapIsFull() {
        for (int i = 0; i < CollectPowerUpInteractor.MAX_POWER_UPS_ON_MAP; i++) {
            match.getPowerUps().add(new ShieldPowerUp(125 + i * 100, 275));
        }
        tick(CollectPowerUpInteractor.SPAWN_INTERVAL);
        assertEquals(CollectPowerUpInteractor.MAX_POWER_UPS_ON_MAP, match.getPowerUps().size());
    }

    @Test
    void neverSpawnsNextToATank() {
        for (int i = 1; i <= 3; i++) {
            tick((long) i * CollectPowerUpInteractor.SPAWN_INTERVAL);
        }
        for (PowerUp p : match.getPowerUps()) {
            assertTrue(Math.hypot(p.getX() - tank1.getX(), p.getY() - tank1.getY())
                    >= CollectPowerUpInteractor.MIN_SPAWN_DISTANCE);
        }
    }

    @Test
    void tankDrivingOverPowerUpCollectsIt() {
        PowerUp shotgun = new ShotgunPowerUp(30, 30);
        match.getPowerUps().add(shotgun);

        tick(1);

        assertTrue(match.getPowerUps().isEmpty());
        assertSame(shotgun, gameState.getActivePowerUp(tank1));
        assertSame(shotgun, tank1.getWeapon());
        assertEquals(0, lastOutput.getCollectedByPlayer());
        assertEquals("SHOTGUN", lastOutput.getActivePowerUps().get(0));
        assertEquals("", lastOutput.getActivePowerUps().get(1));
    }

    @Test
    void newPowerUpReplacesHeldOne() {
        match.getPowerUps().add(new ShieldPowerUp(30, 30));
        tick(1);
        assertTrue(tank1.isShielded());

        PowerUp cannon = new CannonPowerUp(30, 30);
        match.getPowerUps().add(cannon);
        tick(2);

        assertSame(cannon, gameState.getActivePowerUp(tank1));
        assertSame(cannon, tank1.getWeapon());
        assertTrue(!tank1.isShielded(), "the old shield is removed");
    }

    @Test
    void usedUpPowerUpGivesBackStandardWeapon() {
        match.getPowerUps().add(new CannonPowerUp(30, 30));
        tick(1);
        tank1.fire();
        tick(2);

        assertNull(gameState.getActivePowerUp(tank1));
        assertInstanceOf(StandardWeapon.class, tank1.getWeapon());
        assertEquals("", lastOutput.getActivePowerUps().get(0));
    }

    @Test
    void doesNothingBeforeAMatchStarts() {
        gameState.setCurrentMatch(null);
        tick(CollectPowerUpInteractor.SPAWN_INTERVAL);
        assertTrue(lastOutput.getMapItems().isEmpty());
    }

    @Test
    void expiredShrinkGrowsBackInOpenSpace() {
        match.getPowerUps().add(new ShrinkPowerUp(30, 30));
        tick(1);
        assertEquals(ShrinkPowerUp.SHRUNK_RADIUS, tank1.getRadius());

        for (int t = 2; t <= ShrinkPowerUp.DURATION + 1; t++) {
            tick(t);
        }

        assertNull(gameState.getActivePowerUp(tank1));
        assertEquals(Tank.DEFAULT_RADIUS, tank1.getRadius());
    }

    @Test
    void expiredShrinkWaitsWhileAWallIsTooClose() {
        // Wall 12 px to the right of tank 1: room for the shrunk tank, not the full-size one.
        Tank small = new Tank(1, 108, 100, 0);
        Match tight = new Match(new Player("a", false, 0, 0), new Player("b", false, 0, 0),
                new Maze(10, 10, 50, List.of(new Wall(120, 0, 6, 500))), small, tank2);
        gameState.setCurrentMatch(tight);
        ShrinkPowerUp shrink = new ShrinkPowerUp(0, 0);
        shrink.applyTo(small);
        gameState.setActivePowerUp(small, shrink);

        for (int t = 1; t <= ShrinkPowerUp.DURATION + 10; t++) {
            tick(t);
        }

        assertSame(shrink, gameState.getActivePowerUp(small), "kept until there is room");
        assertEquals(ShrinkPowerUp.SHRUNK_RADIUS, small.getRadius());
        assertEquals("SHRINK", lastOutput.getActivePowerUps().get(0));
    }

    @Test
    void holdingALaserSendsAnAimLine() {
        match.getPowerUps().add(new LaserPowerUp(30, 30));
        tick(1);

        assertFalse(lastOutput.getAimPaths().get(0).isEmpty(), "player 1 has an aim line");
        assertTrue(lastOutput.getAimPaths().get(1).isEmpty(), "player 2 has none");
    }
}
