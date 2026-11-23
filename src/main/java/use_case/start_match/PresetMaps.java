package use_case.start_match;

import java.util.List;
import java.util.Map;

public final class PresetMaps {
    public static final String RANDOM = "random";

    private static final List<String> MAP_IDS = List.of(
            "open-yard", "switchback", "broken-ring", "twin-bunkers", "crossroads");

    private static final Map<String, List<String>> LAYOUTS = Map.of(
            "open-yard", List.of(
                    "#################",
                    "#...............#",
                    "#.1........#....#",
                    "#....###...#....#",
                    "#..........#....#",
                    "#...#...#...#...#",
                    "#....#..........#",
                    "#....#...###....#",
                    "#....#........2.#",
                    "#...............#",
                    "#################"
            ),
            "switchback", List.of(
                    "#################",
                    "#....#..........#",
                    "#.1..#....###...#",
                    "#....##.#....#..#",
                    "#..#.#..#..#....#",
                    "#....#..#..#....#",
                    "#....#..#..#.#..#",
                    "#..#....#.##....#",
                    "#...###....#..2.#",
                    "#..........#....#",
                    "#################"
            ),
            "broken-ring", List.of(
                    "#################",
                    "#...............#",
                    "#.1.....#.......#",
                    "#...###...###...#",
                    "#...#.......#...#",
                    "#.....#...#.....#",
                    "#...#.......#...#",
                    "#...###...###...#",
                    "#.......#.....2.#",
                    "#...............#",
                    "#################"
            ),
            "twin-bunkers", List.of(
                    "#################",
                    "#...............#",
                    "#.1........##...#",
                    "#....####...#...#",
                    "#....#..#..#....#",
                    "#..#.#.....#.#..#",
                    "#....#..#..#....#",
                    "#...#...####....#",
                    "#...##........2.#",
                    "#...............#",
                    "#################"
            ),
            "crossroads", List.of(
                    "#################",
                    "#...............#",
                    "#.1..##...##....#",
                    "#....#..#.......#",
                    "#.......#....#..#",
                    "#..#..#####..#..#",
                    "#..#....#.......#",
                    "#.......#..#....#",
                    "#....##...##..2.#",
                    "#...............#",
                    "#################"
            )
    );

    private PresetMaps() {
    }

    public static List<String> getMapIds() {
        return MAP_IDS;
    }

    public static List<String> getLayout(String mapId) {
        List<String> layout = mapId == null ? null : LAYOUTS.get(mapId);
        if (layout == null) {
            throw new IllegalArgumentException("Unknown preset map: " + mapId);
        }
        return layout;
    }
}