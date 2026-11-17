package entity;

import java.util.ArrayList;
import java.util.List;

/**
 * A grid-based maze made of walls.
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

    /**
     * Whether a circle at this position would overlap a wall or stick out of the maze.
     * The bounds check keeps tanks and bullets inside even without outer walls.
     */
    public boolean blocksCircle(double x, double y, double radius) {
        if (x - radius < 0 || y - radius < 0 || x + radius > getWidth() || y + radius > getHeight()) {
            return true;
        }
        for (Wall wall : walls) {
            if (wall.intersectsCircle(x, y, radius)) {
                return true;
            }
        }
        return false;
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
