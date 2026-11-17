package entity;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BulletTest {
    private static final double EPS = 1e-9;
    private final Maze openMaze = new Maze(10, 10, 50, List.of());
    /** Left face at x = 120. */
    private final Wall verticalWall = new Wall(120, 0, 6, 500);
    /** Top face at y = 120. */
    private final Wall horizontalWall = new Wall(0, 120, 500, 6);

    @Test
    void updateMovesByVelocity() {
        Bullet bullet = new Bullet(1, 100, 100, 4, -2);

        bullet.update(openMaze);

        assertEquals(104, bullet.getX(), EPS);
        assertEquals(98, bullet.getY(), EPS);
    }

    @Test
    void bouncesOffVerticalWall() {
        Maze maze = new Maze(10, 10, 50, List.of(verticalWall));
        Bullet bullet = new Bullet(1, 114, 100, 4, 0);

        bullet.update(maze);

        assertEquals(114, bullet.getX(), EPS);
        assertEquals(-4, bullet.getVelocityX(), EPS);
        assertEquals(0, bullet.getVelocityY(), EPS);
    }

    @Test
    void bouncesOffHorizontalWall() {
        Maze maze = new Maze(10, 10, 50, List.of(horizontalWall));
        Bullet bullet = new Bullet(1, 100, 114, 0, 4);

        bullet.update(maze);

        assertEquals(114, bullet.getY(), EPS);
        assertEquals(0, bullet.getVelocityX(), EPS);
        assertEquals(-4, bullet.getVelocityY(), EPS);
    }

    @Test
    void bouncesOffCornerOnBothAxes() {
        Maze maze = new Maze(10, 10, 50, List.of(verticalWall, horizontalWall));
        Bullet bullet = new Bullet(1, 114, 114, 4, 4);

        bullet.update(maze);

        assertEquals(114, bullet.getX(), EPS);
        assertEquals(114, bullet.getY(), EPS);
        assertEquals(-4, bullet.getVelocityX(), EPS);
        assertEquals(-4, bullet.getVelocityY(), EPS);
    }

    @Test
    void expiresAfterLifetime() {
        Bullet bullet = new Bullet(1, 100, 100, 0, 0);

        for (int i = 0; i < Bullet.LIFETIME - 1; i++) {
            bullet.update(openMaze);
        }
        assertFalse(bullet.isExpired());

        bullet.update(openMaze);
        assertTrue(bullet.isExpired());
    }

    @Test
    void expireRemovesBulletImmediately() {
        Bullet bullet = new Bullet(1, 100, 100, 0, 0);

        bullet.expire();

        assertTrue(bullet.isExpired());
        assertFalse(bullet.hits(new Tank(2, 100, 100, 0)));
    }

    @Test
    void hitsOtherTankWithinReach() {
        Bullet bullet = new Bullet(1, 100, 100, 0, 0);

        assertTrue(bullet.hits(new Tank(2, 115, 100, 0)));
    }

    @Test
    void missesTankOutOfReach() {
        Bullet bullet = new Bullet(1, 100, 100, 0, 0);

        assertFalse(bullet.hits(new Tank(2, 118, 100, 0)));
    }

    @Test
    void cannotHitOwnerUntilArmed() {
        Tank owner = new Tank(1, 100, 100, 0);
        Bullet bullet = new Bullet(1, 100, 100, 0, 0);

        for (int i = 0; i < Bullet.ARMING_TICKS - 1; i++) {
            bullet.update(openMaze);
        }
        assertFalse(bullet.hits(owner));

        bullet.update(openMaze);
        assertTrue(bullet.hits(owner));
    }

    @Test
    void cannotHitDestroyedTank() {
        Tank tank = new Tank(2, 100, 100, 0);
        tank.destroy();

        assertFalse(new Bullet(1, 100, 100, 0, 0).hits(tank));
    }
}
