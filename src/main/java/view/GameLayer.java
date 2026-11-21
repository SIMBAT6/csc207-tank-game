package view;

/**
 * The layers of the game screen, drawn back to front in the order listed here.
 * Painters registered on the same layer are drawn in the order they were added.
 */
public enum GameLayer {
    /** Maze walls (UC2). */
    MAZE,
    /** Things lying on the map, e.g. power-ups and aim lines (UC4). */
    ITEMS,
    /** Tanks, bullets and laser beams (UC3). */
    TANKS,
    /** Scores, power-up status and round banners drawn on top of everything (UC4, UC5). */
    OVERLAY
}
