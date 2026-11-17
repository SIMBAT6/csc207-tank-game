package entity;

/**
 * An axis-aligned wall segment inside a maze.
 */
public class Wall {
    private final int x;
    private final int y;
    private final int width;
    private final int height;

    public Wall(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Whether a circle overlaps this wall. Finds the point of the wall closest to the
     * circle's centre and checks if it lies strictly inside the circle.
     */
    public boolean intersectsCircle(double centerX, double centerY, double radius) {
        double nearestX = Math.max(x, Math.min(centerX, x + width));
        double nearestY = Math.max(y, Math.min(centerY, y + height));
        double dx = centerX - nearestX;
        double dy = centerY - nearestY;
        return dx * dx + dy * dy < radius * radius;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
