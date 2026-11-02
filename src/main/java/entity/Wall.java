package entity;

/**
 * An axis-aligned wall segment inside a maze.
 * TODO (UC2/UC3 leads): add intersects(...) for collision checks.
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
