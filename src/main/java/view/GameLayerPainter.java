package view;

import java.awt.Graphics2D;

/**
 * Draws one use case's part of the game screen, usually from that use case's view model state.
 * Each painter gets its own copy of the graphics context, so colours or strokes it sets
 * don't leak into other painters.
 */
@FunctionalInterface
public interface GameLayerPainter {
    void paint(Graphics2D g);
}
