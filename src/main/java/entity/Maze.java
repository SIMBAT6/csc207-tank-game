package entity;

import java.util.ArrayList;
import java.util.List;

/**
 * A grid-based maze made of walls.
 * TODO (UC2 lead): add collidesWithWall(...), getRandomEmptyCell() and getSpawnPoints().
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
