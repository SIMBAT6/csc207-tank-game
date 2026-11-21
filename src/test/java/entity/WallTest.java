package entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WallTest {
    /** Covers x 100..150, y 100..110. */
    private final Wall wall = new Wall(100, 100, 50, 10);

    @Test
    void circleWithCentreInsideWallIntersects() {
        assertTrue(wall.intersectsCircle(125, 105, 3));
    }

    @Test
    void circleOverlappingEdgeIntersects() {
        assertTrue(wall.intersectsCircle(125, 112.9, 3));
    }

    @Test
    void circleJustTouchingEdgeDoesNotIntersect() {
        assertFalse(wall.intersectsCircle(125, 113, 3));
    }

    @Test
    void cornerUsesDistanceToCorner() {
        assertTrue(wall.intersectsCircle(152, 112, 3));
        assertFalse(wall.intersectsCircle(153, 113, 3));
    }

    @Test
    void farCircleDoesNotIntersect() {
        assertFalse(wall.intersectsCircle(300, 300, 3));
    }
}
