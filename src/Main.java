import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Main {
    private static Vector<Long> seeds = new Vector<>();

    private static Vector<List<Long>> soilmap;
    private static Vector<List<Long>> fertMap;
    private static Vector<List<Long>> waterMap;
    private static Vector<List<Long>> lightMap;
    private static Vector<List<Long>> tempMap;
    private static Vector<List<Long>> humidMap;
    private static Vector<List<Long>> locMap;

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        var alm = readAlm();
        parseSeeds(alm.removeFirst());
        parseMaps(alm);
        var threads = Runtime.getRuntime().availableProcessors();
        System.out.println("THREADS: " + threads);

        try (ExecutorService ex = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Callable<Long>> callables = new ArrayList<>();

            for (int i = 0; i < seeds.size(); i += 2) {
                var start = seeds.get(i);
                var end = seeds.get(i + 1) + start;
                var mid = (long) (Math.round((float) (start + end) / 2) - 1);
                callables.add(() -> runTask(start, mid));
                callables.add(() -> runTask(mid + 1, end));
            }

            Long result = null;
            for (Future<Long> f : ex.invokeAll(callables)) {
                var res = f.get();
                if (result == null || res < result) {
                    result = res;
                }
            }
            System.out.println("RESULT: " + result);
        }


    }

    private static Long runTask(Long start, Long end) {
        Long finalRes = null;
        for (Long k = start; k < end; k++) {
            var res = getLocationFromSeed(k);
            if (finalRes == null || res < finalRes) {
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
        seeds = Arrays.stream(seedLine.split(" "))
                .filter(Predicate.not((str) -> str.startsWith("seeds")))
                .map(Long::parseLong)
                .collect(Collectors.toCollection(Vector::new));
    }

    private static void parseMaps(Vector<String> alm) {
        var allMaps = Arrays.stream(alm.stream()
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
        soilmap = allMaps.get("seed-to-soil");
        fertMap = allMaps.get("soil-to-fertilizer");
        waterMap = allMaps.get("fertilizer-to-water");
        lightMap = allMaps.get("water-to-light");
        tempMap = allMaps.get("light-to-temperature");
        humidMap = allMaps.get("temperature-to-humidity");
        locMap = allMaps.get("humidity-to-location");
    }

    private static Vector<String> splitMap(String s) {
        return Arrays.stream(s.split(System.lineSeparator()))
                .filter(Predicate.not(String::isEmpty))
                .collect(Collectors.toCollection(Vector::new));
    }

    private static Long getLocationFromSeed(Long seed) {
        var res = convertMapToVal(seed, soilmap);
        res = convertMapToVal(res, fertMap);
        res = convertMapToVal(res, waterMap);
        res = convertMapToVal(res, lightMap);
        res = convertMapToVal(res, tempMap);
        res = convertMapToVal(res, humidMap);
        res = convertMapToVal(res, locMap);
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
