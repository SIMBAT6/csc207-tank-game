package entity;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PowerUpTest {
    private static final double EPS = 1e-9;
    /** A vertical wall whose left face is at x = 120. */
    private final Maze mazeWithWall = new Maze(10, 10, 50, List.of(new Wall(120, 0, 6, 500)));
    /** A closed box, like a real map: outer walls, so beams keep bouncing inside. */
    private final Maze closedBox = new Maze(10, 10, 50, List.of(
            new Wall(0, 0, 500, 6), new Wall(0, 494, 500, 6),
            new Wall(0, 0, 6, 500), new Wall(494, 0, 6, 500)));

    @Test
    void shotgunFiresFiveBulletsInASpread() {
        Tank tank = new Tank(1, 100, 100, 0);
        ShotgunPowerUp shotgun = new ShotgunPowerUp(0, 0);
        shotgun.applyTo(tank);

        List<Bullet> bullets = tank.fire();

        assertEquals(5, bullets.size());
        double firstAngle = Math.atan2(bullets.get(0).getVelocityY(), bullets.get(0).getVelocityX());
        double lastAngle = Math.atan2(bullets.get(4).getVelocityY(), bullets.get(4).getVelocityX());
        assertEquals(-Math.toRadians(30), firstAngle, EPS);
        assertEquals(Math.toRadians(30), lastAngle, EPS);
        assertEquals(ShotgunPowerUp.AMMO - 1, shotgun.getAmmo());
    }

    @Test
    void shotgunExpiresAfterThreeShotsAndRemoveRestoresStandardWeapon() {
        Tank tank = new Tank(1, 100, 100, 0);
        ShotgunPowerUp shotgun = new ShotgunPowerUp(0, 0);
        shotgun.applyTo(tank);
        for (int i = 0; i < ShotgunPowerUp.AMMO; i++) {
            tank.fire();
        }
        assertTrue(shotgun.isExpired(tank));

        assertTrue(shotgun.removeFrom(tank, mazeWithWall));
        assertInstanceOf(StandardWeapon.class, tank.getWeapon());
    }

    @Test
    void cannonBallGoesThroughWalls() {
        Tank tank = new Tank(1, 100, 100, 0);
        new CannonPowerUp(0, 0).applyTo(tank);
        Bullet ball = tank.fire().get(0);
        assertInstanceOf(CannonBullet.class, ball);

        for (int i = 0; i < 10; i++) {
            ball.update(mazeWithWall);
        }

        assertTrue(ball.getX() > 126, "the cannon ball should be past the wall, not bounced back");
        assertTrue(ball.getVelocityX() > 0);
    }

    @Test
    void normalBulletStillBouncesOffTheSameWall() {
        Bullet bullet = new Bullet(1, 100, 100, Bullet.SPEED, 0);
        for (int i = 0; i < 10; i++) {
            bullet.update(mazeWithWall);
        }
        assertTrue(bullet.getVelocityX() < 0);
    }

    @Test
    void shieldAbsorbsOneHitThenExpires() {
        Tank tank = new Tank(1, 100, 100, 0);
        ShieldPowerUp shield = new ShieldPowerUp(0, 0);
        shield.applyTo(tank);
        assertTrue(tank.isShielded());
        assertFalse(shield.isExpired(tank));

        assertFalse(tank.takeHit());
        assertTrue(tank.isAlive());
        assertTrue(shield.isExpired(tank));
    }

    @Test
    void powerUpExpiresWhenDurationRunsOut() {
        Tank tank = new Tank(1, 100, 100, 0);
        ShieldPowerUp shield = new ShieldPowerUp(0, 0);
        shield.applyTo(tank);
        for (int i = 0; i < ShieldPowerUp.DURATION; i++) {
            shield.tick();
        }
        assertTrue(shield.isExpired(tank));
    }

    @Test
    void cannonApplyAndRemoveSwapTheWeapon() {
        Tank tank = new Tank(1, 100, 100, 0);
        CannonPowerUp cannon = new CannonPowerUp(0, 0);
        cannon.applyTo(tank);
        assertSame(cannon, tank.getWeapon());
        assertTrue(cannon.removeFrom(tank, mazeWithWall));
        assertInstanceOf(StandardWeapon.class, tank.getWeapon());
    }

    @Test
    void shieldRemoveTakesTheShieldAway() {
        Tank tank = new Tank(1, 100, 100, 0);
        ShieldPowerUp shield = new ShieldPowerUp(0, 0);
        shield.applyTo(tank);
        assertTrue(shield.removeFrom(tank, mazeWithWall));
        assertFalse(tank.isShielded());
    }

    @Test
    void shrinkMakesTheTankSmallerAndItGrowsBackInOpenSpace() {
        Tank tank = new Tank(1, 50, 50, 0);
        ShrinkPowerUp shrink = new ShrinkPowerUp(0, 0);
        shrink.applyTo(tank);
        assertEquals(ShrinkPowerUp.SHRUNK_RADIUS, tank.getRadius(), EPS);

        assertTrue(shrink.removeFrom(tank, mazeWithWall));
        assertEquals(Tank.DEFAULT_RADIUS, tank.getRadius(), EPS);
    }

    @Test
    void shrunkTankNextToAWallWaitsToGrowBack() {
        // 12 px from the wall: fits when shrunk (8) but not at full size (14).
        Tank tank = new Tank(1, 108, 100, 0);
        ShrinkPowerUp shrink = new ShrinkPowerUp(0, 0);
        shrink.applyTo(tank);

        assertFalse(shrink.removeFrom(tank, mazeWithWall));
        assertEquals(ShrinkPowerUp.SHRUNK_RADIUS, tank.getRadius(), EPS);
    }

    /** Runs one game tick for a bullet the way the game loop does: all steps, then age(). */
    private static void runTick(Bullet bullet, Maze maze) {
        for (int i = 0; i < bullet.getStepsPerTick() && !bullet.isExpired(); i++) {
            bullet.update(maze);
        }
        bullet.age();
    }

    @Test
    void laserBeamCrossesTheMazeInOneTickAndRecordsBounces() {
        Tank tank = new Tank(1, 100, 100, 0);
        LaserPowerUp laser = new LaserPowerUp(0, 0);
        laser.applyTo(tank);
        Bullet beam = tank.fire().get(0);
        assertInstanceOf(LaserBeam.class, beam);

        runTick(beam, mazeWithWall);

        List<double[]> path = beam.getPath();
        assertEquals(3, path.size(), "start, one bounce off the wall, end");
        assertEquals(100, path.get(0)[0], EPS);
        assertTrue(path.get(1)[0] < 120, "bounced at the wall");
        assertTrue(path.get(2)[0] < path.get(1)[0], "then travelled back");
        assertEquals(0, beam.getStepsPerTick(), "it stops after the first tick");
    }

    @Test
    void laserBeamStaysVisibleBrieflyThenExpires() {
        LaserBeam beam = new LaserBeam(1, 100, 100, Bullet.SPEED, 0);
        for (int i = 0; i < LaserBeam.VISIBLE_TICKS - 1; i++) {
            runTick(beam, closedBox);
        }
        assertFalse(beam.isExpired());
        runTick(beam, closedBox);
        assertTrue(beam.isExpired());
    }

    @Test
    void laserBeamStopsOnTheTankItHits() {
        LaserBeam beam = new LaserBeam(1, 100, 100, Bullet.SPEED, 0);
        Tank target = new Tank(2, 100, 100, 0);
        assertTrue(beam.hits(target));
        beam.onHitTank();
        assertFalse(beam.hits(target), "a stopped beam doesn't hit again");
        assertFalse(beam.isExpired(), "but it is still drawn");
        assertEquals(0, beam.getStepsPerTick());
    }

    @Test
    void laserAimPathMatchesTheRealShot() {
        Tank tank = new Tank(1, 100, 100, 0);
        LaserPowerUp laser = new LaserPowerUp(0, 0);
        laser.applyTo(tank);

        List<double[]> aim = laser.getAimPath(tank, mazeWithWall);
        Bullet beam = tank.fire().get(0);
        runTick(beam, mazeWithWall);
        List<double[]> shot = beam.getPath();

        assertEquals(shot.size(), aim.size());
        for (int i = 0; i < aim.size(); i++) {
            assertEquals(shot.get(i)[0], aim.get(i)[0], EPS);
            assertEquals(shot.get(i)[1], aim.get(i)[1], EPS);
        }
        assertEquals(LaserPowerUp.AMMO - 1, laser.getAmmo(), "aiming does not use ammo");
    }

    @Test
    void otherPowerUpsHaveNoAimLine() {
        Tank tank = new Tank(1, 100, 100, 0);
        assertTrue(new ShotgunPowerUp(0, 0).getAimPath(tank, mazeWithWall).isEmpty());
    }

    @Test
    void factoryCreatesEachType() {
        PowerUpFactory factory = new PowerUpFactory(new java.util.Random(1));
        for (PowerUpType type : PowerUpType.values()) {
            PowerUp powerUp = factory.create(type, 5, 6);
            assertSame(type, powerUp.getType());
            assertEquals(5, powerUp.getX(), EPS);
        }
    }
}
