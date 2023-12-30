import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Main {
    private static Vector<Long> seeds = new Vector<>();
    private static HashMap<Object, Vector<List<Long>>> allMaps;

    public static void main(String[] args) {
        var alm = readAlm();
        parseSeeds(alm.removeFirst());
        parseMaps(alm);

        Long result = null;
        for (Long seed : seeds) {
            var res = getLocationFromSeed(seed);
            if (result == null || res < result) {
                result = res;
            }
        }

        System.out.println("Seeds" + seeds);
        System.out.println("Maps" + allMaps);
        System.out.println("Result " + result);
    }

    private static Vector<String> readAlm() {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/input.txt"))) {
            return reader.lines().collect(Collectors.toCollection(Vector::new));
        } catch (IOException e) {
            System.err.println("Error reading the input file");
            System.exit(1);
        }
        return null;
    }

    private static void parseSeeds(String seedLine) {
        seeds = Arrays.stream(seedLine.split(" "))
                .filter(Predicate.not((str) -> str.startsWith("seeds")))
                .map(Long::parseLong)
                .collect(Collectors.toCollection(Vector::new));
    }

    private static void parseMaps(Vector<String> alm) {
        allMaps = Arrays.stream(alm.stream()
                        .collect(Collectors.joining(System.lineSeparator()))
                        .split("\n\n"))
                .map(Main::splitMap)
                .collect(Collectors.toMap(key -> Arrays.stream(key.getFirst().split(" ")).toArray()[0],
                        v -> {
                            v.removeFirst();
                            return new Vector<List<Long>>(v.stream().map(el ->
                                            Arrays.stream(el.split(" "))
                                                    .map(Long::parseLong)
                                                    .collect(Collectors.toList()))
                                    .collect(Collectors.toCollection(Vector::new)));
                        }, (v1, v2) -> v1, HashMap::new));
    }

    private static Vector<String> splitMap(String s) {
        return Arrays.stream(s.split(System.lineSeparator()))
                .filter(Predicate.not(String::isEmpty))
                .collect(Collectors.toCollection(Vector::new));
    }

    private static Long getLocationFromSeed(Long seed) {
        var res = convertMapToVal(seed, allMaps.get("seed-to-soil"));
        res = convertMapToVal(res, allMaps.get("soil-to-fertilizer"));
        res = convertMapToVal(res, allMaps.get("fertilizer-to-water"));
        res = convertMapToVal(res, allMaps.get("water-to-light"));
        res = convertMapToVal(res, allMaps.get("light-to-temperature"));
        res = convertMapToVal(res, allMaps.get("temperature-to-humidity"));
        res = convertMapToVal(res, allMaps.get("humidity-to-location"));
        return res;
    }


    private static Long convertMapToVal(Long seed, Vector<List<Long>> map) {
        for (List<Long> l : map) {
            if (l.get(1) <= seed && seed < l.get(1) + l.get(2)) {
                return seed + (l.getFirst() - l.get(1));
            }
        }
        return seed;
    }

}
