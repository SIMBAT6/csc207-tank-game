package use_case.start_match;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class RandomMaze {
    private static final int ROWS = 11;
    private static final int COLUMNS = 17;
    private static final int SPAWN_ROW = 2;
    private static final int SPAWN_COLUMN = 2;
    private static final int WALL_PAIRS = 12;

    private RandomMaze() {
    }

    static List<String> generate() {
        char[][] grid = new char[ROWS][COLUMNS];

        for (int row = 0; row < ROWS; row++) {
            for (int column = 0; column < COLUMNS; column++) {
                boolean border = row == 0 || row == ROWS - 1
                        || column == 0 || column == COLUMNS - 1;
                grid[row][column] = border ? '#' : '.';
            }
        }

        // Walls on odd rows and columns leave the clear corridors connected.
        List<Integer> candidates = new ArrayList<>();
        for (int row = 1; row <= ROWS / 2; row += 2) {
            int lastColumn = row == ROWS / 2 ? COLUMNS / 2 : COLUMNS - 1;
            for (int column = 1; column < lastColumn; column += 2) {
                if (row > SPAWN_ROW + 1 || column > SPAWN_COLUMN + 1) {
                    candidates.add(row * COLUMNS + column);
                }
            }
        }

        Collections.shuffle(candidates);
        for (int cell : candidates.subList(0, WALL_PAIRS)) {
            int row = cell / COLUMNS;
            int column = cell % COLUMNS;
            grid[row][column] = '#';
            grid[ROWS - 1 - row][COLUMNS - 1 - column] = '#';
        }

        grid[SPAWN_ROW][SPAWN_COLUMN] = '1';
        grid[ROWS - 1 - SPAWN_ROW][COLUMNS - 1 - SPAWN_COLUMN] = '2';

        List<String> layout = new ArrayList<>();
        for (char[] row : grid) {
            layout.add(new String(row));
        }
        return List.copyOf(layout);
    }
}