package entity;

import java.util.List;

/**
 * The default weapon: one bullet in the direction the tank faces. It is created at the tank's
 * centre, and Control Tank moves it to the barrel tip.
 */
public class StandardWeapon implements Weapon {

    @Override
    public List<Bullet> createBullets(Tank tank) {
        double velocityX = Math.cos(tank.getAngle()) * Bullet.SPEED;
        double velocityY = Math.sin(tank.getAngle()) * Bullet.SPEED;
        return List.of(new Bullet(tank.getId(), tank.getX(), tank.getY(), velocityX, velocityY));
    }
}
