import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Main {
    private static final List<List<Long>> seeds = new ArrayList<>();

    private static List<List<Long>> soilMap;
    private static List<List<Long>> fertMap;
    private static List<List<Long>> waterMap;
    private static List<List<Long>> lightMap;
    private static List<List<Long>> tempMap;
    private static List<List<Long>> humidMap;
    private static List<List<Long>> locMap;

    public static void main(String[] args) {
        var alm = readAlm();
        parseSeeds(alm.removeFirst());
        parseMaps(alm);
        System.out.println(seeds);

        var location = seeds.stream()
                .parallel()
                .map(range -> runTask(range.getFirst(), range.getLast()))
                .min(Long::compare)
                .orElseThrow();

        System.out.println("RESULT: " + location);
    }

    private static Long runTask(Long start, Long end) {
        Long finalRes = start;
        for (Long k = start; k < end; k++) {
            var res = getLocationFromSeed(k);
            if (res < finalRes) {
                finalRes = res;
                System.out.println("Returned " + finalRes);
            }
        }
        return finalRes;
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
        var seedList = Arrays.stream(seedLine.split(" "))
                .filter(Predicate.not((str) -> str.startsWith("seeds")))
                .map(Long::parseLong)
                .toList();

        for (int i = 0; i < seedList.size(); i += 2) {
            var start = seedList.get(i);
            var end = start + seedList.get(i + 1);
            var mid = (long) Math.round((float) (start + end) / 2) - 1;

            var l = new ArrayList<Long>();
            l.add(start);
            l.add(mid);
            seeds.add(l);

            var k = new ArrayList<Long>();
            k.add(mid + 1);
            k.add(end);
            seeds.add(k);
        }
    }

    private static void parseMaps(Vector<String> alm) {
        var allMaps = Arrays.stream(alm.stream()
                        .collect(Collectors.joining(System.lineSeparator()))
                        .split("\n\n"))
                .map(Main::splitMap)
                .collect(Collectors.toMap(
                        key -> Arrays.stream(key.getFirst().split(" ")).toArray()[0],
                        v -> {
                            v.removeFirst();
                            return v.stream().map(el ->
                                    Arrays.stream(el.split(" "))
                                            .map(Long::parseLong)
                                            .collect(Collectors.toList())
                            ).collect(Collectors.toCollection(ArrayList::new));
                        }, (v1, v2) -> v1, HashMap::new));

        soilMap = allMaps.get("seed-to-soil");
        fertMap = allMaps.get("soil-to-fertilizer");
        waterMap = allMaps.get("fertilizer-to-water");
        lightMap = allMaps.get("water-to-light");
        tempMap = allMaps.get("light-to-temperature");
        humidMap = allMaps.get("temperature-to-humidity");
        locMap = allMaps.get("humidity-to-location");
    }

    private static List<String> splitMap(String s) {
        return Arrays.stream(s.split(System.lineSeparator()))
                .filter(Predicate.not(String::isEmpty))
                .collect(Collectors.toList());
    }

    private static Long getLocationFromSeed(Long seed) {
        var res = convertMapToVal(seed, soilMap);
        res = convertMapToVal(res, fertMap);
        res = convertMapToVal(res, waterMap);
        res = convertMapToVal(res, lightMap);
        res = convertMapToVal(res, tempMap);
        res = convertMapToVal(res, humidMap);
        res = convertMapToVal(res, locMap);
        return res;
    }


    private static Long convertMapToVal(Long seed, List<List<Long>> map) {
        for (List<Long> l : map) {
            if (l.get(1) <= seed && seed < l.get(1) + l.get(2)) {
                return seed + (l.getFirst() - l.get(1));
            }
        }
        return seed;
    }

}
