package entity;

/**
 * A bullet that travels in a straight line and bounces off walls.
 * TODO (UC3 lead): add move(), bouncing, expiry and hit detection.
 */
public class Bullet {
    private final int ownerId;
    private double x;
    private double y;
    private double velocityX;
    private double velocityY;
    private int bouncesLeft;
    private int lifetime;

    public Bullet(int ownerId, double x, double y, double velocityX, double velocityY,
                  int bouncesLeft, int lifetime) {
        this.ownerId = ownerId;
        this.x = x;
        this.y = y;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.bouncesLeft = bouncesLeft;
        this.lifetime = lifetime;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getVelocityX() {
        return velocityX;
    }

    public double getVelocityY() {
        return velocityY;
    }

    public int getBouncesLeft() {
        return bouncesLeft;
    }

    public int getLifetime() {
        return lifetime;
    }
}
