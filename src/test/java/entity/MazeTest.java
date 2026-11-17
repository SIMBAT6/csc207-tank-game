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
    void circleStickingOutOfMazeIsBlocked() {
        assertTrue(maze.blocksCircle(10, 100, 14));
        assertTrue(maze.blocksCircle(490, 100, 14));
        assertTrue(maze.blocksCircle(100, 10, 14));
        assertTrue(maze.blocksCircle(100, 490, 14));
    }

    @Test
    void circleTouchingMazeEdgeIsNotBlocked() {
        assertFalse(maze.blocksCircle(14, 100, 14));
    }
}
