package entity;

import java.util.List;

/**
 * Decides which bullets a tank creates when it fires.
 * Power-ups (UC4) provide other implementations and swap them in with Tank.setWeapon.
 */
public interface Weapon {
    List<Bullet> createBullets(Tank tank);
}
