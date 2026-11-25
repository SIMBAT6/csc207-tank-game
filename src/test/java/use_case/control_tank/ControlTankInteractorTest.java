package use_case.control_tank;

import entity.Bullet;
import entity.CannonBullet;
import entity.LaserBeam;
import entity.Match;
import entity.Maze;
import entity.Player;
import entity.Tank;
import entity.Wall;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ControlTankInteractorTest {
    private static final TankCommand NONE = TankCommand.NONE;
    private static final TankCommand FIRE = new TankCommand(0, 0, true);

    /** A 400 x 320 box with 10 px outer walls. Tank 1 at (100, 100) faces right, tank 2 at (300, 100) faces left. */
    private final Maze maze = new Maze(8, 10, 40, List.of(
            new Wall(0, 0, 400, 10), new Wall(0, 310, 400, 10),
            new Wall(0, 0, 10, 320), new Wall(390, 0, 10, 320)));
    private final Tank tank1 = new Tank(Match.PLAYER_1, 100, 100, 0);
    private final Tank tank2 = new Tank(Match.PLAYER_2, 300, 100, Math.PI);
    private final Match match = new Match(new Player("alice", false, 0, 0), new Player("Guest 2", true, 0, 0),
            maze, tank1, tank2);
    private final RecordingPresenter presenter = new RecordingPresenter();
    private final ControlTankInteractor interactor = new ControlTankInteractor(() -> match, presenter);

    private void tick(TankCommand player1, TankCommand player2) {
        interactor.execute(new ControlTankInputData(player1, player2));
    }

    @Test
    void tankDrivesOnCommand() {
        tick(new TankCommand(1, 0, false), new TankCommand(-1, 0, false));

        assertEquals(100 + Tank.SPEED, tank1.getX(), 1e-9);
        assertEquals(300 + Tank.SPEED, tank2.getX(), 1e-9);
    }

    @Test
    void tankTurnsOnCommand() {
        tick(new TankCommand(0, 1, false), NONE);

        assertEquals(Tank.ROTATION_SPEED, tank1.getAngle(), 1e-9);
    }

    @Test
    void firingAddsTheWeaponsBulletsAtTheTankCentre() {
        tick(FIRE, NONE);

        assertEquals(1, match.getBullets().size());
        Bullet bullet = match.getBullets().get(0);
        assertEquals(Match.PLAYER_1, bullet.getOwnerId());
        assertEquals(100 + Bullet.SPEED, bullet.getX(), 1e-9);
    }

    @Test
    void holdingFireWaitsForTheCooldown() {
        tick(FIRE, NONE);
        for (int i = 1; i < Tank.FIRE_COOLDOWN; i++) {
            tick(FIRE, NONE);
        }
        assertEquals(1, match.getBullets().size());

        tick(FIRE, NONE);

        assertEquals(2, match.getBullets().size());
    }

    @Test
    void noBulletIsFiredOverTheLimit() {
        for (int i = 0; i < Tank.MAX_BULLETS; i++) {
            match.getBullets().add(new Bullet(Match.PLAYER_1, 200, 200, 0, 0));
        }

        tick(FIRE, NONE);

        assertEquals(Tank.MAX_BULLETS, match.getBullets().size());
    }

    @Test
    void bulletsMoveOneStepPerTick() {
        Bullet bullet = new Bullet(Match.PLAYER_1, 200, 200, 0, Bullet.SPEED);
        match.getBullets().add(bullet);

        tick(NONE, NONE);

        assertEquals(200 + Bullet.SPEED, bullet.getY(), 1e-9);
    }

    @Test
    void expiredBulletsAreRemoved() {
        Bullet bullet = new Bullet(Match.PLAYER_1, 200, 200, 0, Bullet.SPEED);
        bullet.expire();
        match.getBullets().add(bullet);

        tick(NONE, NONE);

        assertTrue(match.getBullets().isEmpty());
    }

    @Test
    void aHitDestroysTheTankAndEndsTheRound() {
        match.getBullets().add(new Bullet(Match.PLAYER_1, 280, 100, Bullet.SPEED, 0));

        tick(NONE, NONE);

        assertFalse(tank2.isAlive());
        assertTrue(match.getBullets().isEmpty());
        assertTrue(presenter.roundOver);
        assertEquals(Match.PLAYER_1, presenter.output.getWinner());
    }

    @Test
    void aShieldAbsorbsOneHit() {
        tank2.setShielded(true);
        match.getBullets().add(new Bullet(Match.PLAYER_1, 280, 100, Bullet.SPEED, 0));

        tick(NONE, NONE);

        assertTrue(tank2.isAlive());
        assertFalse(tank2.isShielded());
        assertTrue(match.getBullets().isEmpty());
        assertFalse(presenter.roundOver);
    }

    @Test
    void bothTanksHitOnTheSameTickIsADraw() {
        match.getBullets().add(new Bullet(Match.PLAYER_1, 280, 100, Bullet.SPEED, 0));
        match.getBullets().add(new Bullet(Match.PLAYER_2, 120, 100, -Bullet.SPEED, 0));

        tick(NONE, NONE);

        assertTrue(presenter.roundOver);
        assertEquals(Match.DRAW, presenter.output.getWinner());
    }

    @Test
    void aFastBulletIsCheckedForHitsAfterEveryStep() {
        // 20 steps would carry it from x = 240 to x = 320, past tank 2 at x = 300.
        match.getBullets().add(new FastBullet(Match.PLAYER_1, 240, 100, Bullet.SPEED, 0, 20));

        tick(NONE, NONE);

        assertFalse(tank2.isAlive());
    }

    @Test
    void aCannonBallFliesThroughAWallAndHitsTheTankBehindIt() {
        Maze withInnerWall = new Maze(8, 10, 40, List.of(
                new Wall(0, 0, 400, 10), new Wall(0, 310, 400, 10),
                new Wall(0, 0, 10, 320), new Wall(390, 0, 10, 320), new Wall(200, 60, 10, 80)));
        Match walled = new Match(match.getPlayer1(), match.getPlayer2(), withInnerWall, tank1, tank2);
        walled.getBullets().add(new CannonBullet(Match.PLAYER_1, 150, 100, Bullet.SPEED, 0));
        ControlTankInteractor walledInteractor = new ControlTankInteractor(() -> walled, presenter);

        for (int i = 0; i < 40 && tank2.isAlive(); i++) {
            walledInteractor.execute(new ControlTankInputData(NONE, NONE));
        }

        assertFalse(tank2.isAlive());
    }

    @Test
    void aLaserBeamHitsInOneTickAndStopsAtTheTank() {
        match.getBullets().add(new LaserBeam(Match.PLAYER_1, 100, 100, Bullet.SPEED, 0));

        tick(NONE, NONE);

        assertFalse(tank2.isAlive());
        assertEquals(1, match.getBullets().size(), "the beam stays visible");
        List<double[]> path = presenter.output.getBullets().get(0).getPath();
        assertTrue(path.get(path.size() - 1)[0] < 300, "the beam ends at tank 2, not beyond it");
    }

    @Test
    void outputHasBothTanksAndEveryBullet() {
        match.getBullets().add(new Bullet(Match.PLAYER_2, 200, 200, 0, Bullet.SPEED));

        tick(NONE, NONE);

        assertFalse(presenter.roundOver);
        assertEquals(Match.NO_WINNER, presenter.output.getWinner());
        ControlTankOutputData.TankData first = presenter.output.getTanks().get(0);
        assertEquals(Match.PLAYER_1, first.getId());
        assertEquals(100, first.getX(), 1e-9);
        assertEquals(Tank.DEFAULT_RADIUS, first.getRadius(), 1e-9);
        assertEquals(Match.PLAYER_2, presenter.output.getTanks().get(1).getId());
        ControlTankOutputData.BulletData bullet = presenter.output.getBullets().get(0);
        assertEquals(Match.PLAYER_2, bullet.getOwnerId());
        assertEquals(200 + Bullet.SPEED, bullet.getPath().get(0)[1], 1e-9);
    }

    @Test
    void nothingHappensAfterTheRoundIsOver() {
        match.endRound(Match.PLAYER_1);

        tick(new TankCommand(1, 0, true), NONE);

        assertEquals(100, tank1.getX(), 1e-9);
        assertTrue(match.getBullets().isEmpty());
        assertNull(presenter.output);
    }

    @Test
    void nothingHappensBeforeAMatchStarts() {
        ControlTankInteractor noMatch = new ControlTankInteractor(() -> null, presenter);

        noMatch.execute(new ControlTankInputData(FIRE, FIRE));

        assertNull(presenter.output);
    }

    @Test
    void commandsAreClampedToOneStep() {
        TankCommand command = new TankCommand(5, -3, false);

        assertEquals(1, command.getDrive());
        assertEquals(-1, command.getTurn());
    }

    private static final class FastBullet extends Bullet {
        private final int steps;

        FastBullet(int ownerId, double x, double y, double velocityX, double velocityY, int steps) {
            super(ownerId, x, y, velocityX, velocityY);
            this.steps = steps;
        }

        @Override
        public int getStepsPerTick() {
            return steps;
        }
    }

    private static final class RecordingPresenter implements ControlTankOutputBoundary {
        private ControlTankOutputData output;
        private boolean roundOver;

        @Override
        public void prepareTickView(ControlTankOutputData outputData) {
            output = outputData;
            roundOver = false;
        }

        @Override
        public void prepareRoundOverView(ControlTankOutputData outputData) {
            output = outputData;
            roundOver = true;
        }
    }
}
