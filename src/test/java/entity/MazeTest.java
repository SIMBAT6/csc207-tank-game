package entity;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MazeTest {
    /** 500 x 500 maze with one vertical wall whose left face is at x = 200. */
    private final Maze maze = new Maze(10, 10, 50, List.of(new Wall(200, 0, 6, 500)));

    @Test
    void sizeComesFromGrid() {
        assertEquals(500, maze.getWidth());
        assertEquals(500, maze.getHeight());
    }

    @Test
    void openSpaceIsNotBlocked() {
        assertFalse(maze.blocksCircle(100, 100, 14));
    }

    @Test
    void circleOverlappingWallIsBlocked() {
        assertTrue(maze.blocksCircle(195, 100, 14));
    }

    @Test
    void onlyWallsBlockNotTheMazeEdge() {
        assertFalse(maze.blocksCircle(5, 100, 14));
    }

    @Test
    void outerWallsKeepCirclesInside() {
        Maze enclosed = new Maze(10, 10, 50, List.of(new Wall(0, 0, 6, 500)));

        assertTrue(enclosed.blocksCircle(15, 100, 14));
    }

    @Test
    void containsPointsInsideTheMazeArea() {
        assertTrue(maze.contains(0, 0));
        assertTrue(maze.contains(500, 500));
        assertFalse(maze.contains(-1, 100));
        assertFalse(maze.contains(100, 501));
    }
}
