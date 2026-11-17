package entity;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TankTest {
    private static final double EPS = 1e-9;
    private final Maze openMaze = new Maze(10, 10, 50, List.of());
    /** A vertical wall whose left face is at x = 120. */
    private final Maze mazeWithWall = new Maze(10, 10, 50, List.of(new Wall(120, 0, 6, 500)));

    @Test
    void rotateTurnsByRotationSpeed() {
        Tank tank = new Tank(1, 100, 100, 0);

        tank.rotate(1);
        assertEquals(Tank.ROTATION_SPEED, tank.getAngle(), EPS);

        tank.rotate(-1);
        tank.rotate(-1);
        assertEquals(-Tank.ROTATION_SPEED, tank.getAngle(), EPS);
    }

    @Test
    void driveForwardMovesAlongAngle() {
        Tank tank = new Tank(1, 100, 100, 0);

        tank.drive(1, openMaze);

        assertEquals(100 + Tank.SPEED, tank.getX(), EPS);
        assertEquals(100, tank.getY(), EPS);
    }

    @Test
    void driveBackwardMovesOppositeToAngle() {
        Tank tank = new Tank(1, 100, 100, Math.PI / 2);

        tank.drive(-1, openMaze);

        assertEquals(100, tank.getX(), EPS);
        assertEquals(100 - Tank.SPEED, tank.getY(), EPS);
    }

    @Test
    void driveHeadOnIntoWallDoesNotMove() {
        Tank tank = new Tank(1, 105, 100, 0);

        tank.drive(1, mazeWithWall);

        assertEquals(105, tank.getX(), EPS);
        assertEquals(100, tank.getY(), EPS);
    }

    @Test
    void driveDiagonallyIntoWallSlidesAlongIt() {
        Tank tank = new Tank(1, 105, 100, Math.PI / 4);

        tank.drive(1, mazeWithWall);

        assertEquals(105, tank.getX(), EPS);
        assertEquals(100 + Math.sin(Math.PI / 4) * Tank.SPEED, tank.getY(), EPS);
    }

    @Test
    void destroyedTankCannotMoveTurnOrFire() {
        Tank tank = new Tank(1, 100, 100, 0);
        tank.destroy();

        tank.drive(1, openMaze);
        tank.rotate(1);

        assertFalse(tank.isAlive());
        assertEquals(100, tank.getX(), EPS);
        assertEquals(0, tank.getAngle(), EPS);
        assertFalse(tank.canFire(0));
    }

    @Test
    void cannotFireAgainUntilCooldownEnds() {
        Tank tank = new Tank(1, 100, 100, 0);
        assertTrue(tank.canFire(0));

        tank.fire();
        for (int i = 0; i < Tank.FIRE_COOLDOWN - 1; i++) {
            tank.coolDown();
        }
        assertFalse(tank.canFire(0));

        tank.coolDown();
        assertTrue(tank.canFire(0));
    }

    @Test
    void cannotFireWithMaxBulletsActive() {
        Tank tank = new Tank(1, 100, 100, 0);

        assertTrue(tank.canFire(Tank.MAX_BULLETS - 1));
        assertFalse(tank.canFire(Tank.MAX_BULLETS));
    }

    @Test
    void standardWeaponFiresOneBulletFromCentreAlongAngle() {
        Tank tank = new Tank(7, 100, 200, Math.PI / 2);

        List<Bullet> bullets = tank.fire();

        assertEquals(1, bullets.size());
        Bullet bullet = bullets.get(0);
        assertEquals(7, bullet.getOwnerId());
        assertEquals(100, bullet.getX(), EPS);
        assertEquals(200, bullet.getY(), EPS);
        assertEquals(0, bullet.getVelocityX(), EPS);
        assertEquals(Bullet.SPEED, bullet.getVelocityY(), EPS);
    }

    @Test
    void setWeaponReplacesStandardWeapon() {
        Tank tank = new Tank(1, 100, 100, 0);
        Weapon tripleShot = t -> List.of(
                new Bullet(t.getId(), t.getX(), t.getY(), 1, 0),
                new Bullet(t.getId(), t.getX(), t.getY(), 0, 1),
                new Bullet(t.getId(), t.getX(), t.getY(), -1, 0));

        tank.setWeapon(tripleShot);

        assertSame(tripleShot, tank.getWeapon());
        assertEquals(3, tank.fire().size());
    }
}
