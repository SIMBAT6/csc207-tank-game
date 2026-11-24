package view;

import entity.Maze;
import entity.Wall;

import java.awt.Color;
import java.awt.Graphics2D;

public final class MazePainter {
    private static final Color FLOOR = new Color(0x273650);
    private static final Color WALL = new Color(0x7187A6);

    public void paint(Graphics2D graphics, Maze maze) {
        graphics.setColor(FLOOR);
        graphics.fillRect(0, 0, maze.getWidth(), maze.getHeight());

        graphics.setColor(WALL);
        for (Wall wall : maze.getWalls()) {
            graphics.fillRect(wall.getX(), wall.getY(),
                    wall.getWidth(), wall.getHeight());
        }
    }
}