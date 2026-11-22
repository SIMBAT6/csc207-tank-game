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

        shotgun.removeFrom(tank);
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
    void factoryCreatesEachType() {
        PowerUpFactory factory = new PowerUpFactory(new java.util.Random(1));
        for (PowerUpType type : PowerUpType.values()) {
            PowerUp powerUp = factory.create(type, 5, 6);
            assertSame(type, powerUp.getType());
            assertEquals(5, powerUp.getX(), EPS);
        }
    }
}
