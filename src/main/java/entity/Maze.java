package entity;

import java.util.ArrayList;
import java.util.List;

/**
 * A grid-based maze made of walls. The maze must be enclosed by outer walls: only walls
 * stop tanks and bullets, there is no invisible boundary.
 * TODO (UC2 lead): add getRandomEmptyCell() and getSpawnPoints().
 */
public class Maze {
    private final int rows;
    private final int cols;
    private final int cellSize;
    private final List<Wall> walls;

    public Maze(int rows, int cols, int cellSize, List<Wall> walls) {
        this.rows = rows;
        this.cols = cols;
        this.cellSize = cellSize;
        this.walls = new ArrayList<>(walls);
    }

    /** Whether a circle at this position would overlap any wall. */
    public boolean blocksCircle(double x, double y, double radius) {
        for (Wall wall : walls) {
            if (wall.intersectsCircle(x, y, radius)) {
                return true;
            }
        }
        return false;
    }

    /** Whether a point lies inside the maze area (e.g. a wall-piercing bullet has not left it). */
    public boolean contains(double x, double y) {
        return x >= 0 && y >= 0 && x <= getWidth() && y <= getHeight();
    }

    public int getWidth() {
        return cols * cellSize;
    }

    public int getHeight() {
        return rows * cellSize;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public int getCellSize() {
        return cellSize;
    }

    public List<Wall> getWalls() {
        return new ArrayList<>(walls);
    }
}
