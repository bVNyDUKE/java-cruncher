import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class NumberCruncher {
    private static final List<LongStream> seeds = new ArrayList<>();

    private static List<long[]> soilMap;
    private static List<long[]> fertMap;
    private static List<long[]> waterMap;
    private static List<long[]> lightMap;
    private static List<long[]> tempMap;
    private static List<long[]> humidMap;
    private static List<long[]> locMap;

    public static void main(String[] args) {
        var alm = readAlm();
        parseSeeds(alm.removeFirst());
        parseMaps(alm);

        var loc = seeds.stream()
                .parallel()
                .map(s -> s.parallel()
                        .map(NumberCruncher::getLocationFromSeed)
                        .min()
                        .orElseThrow()
                )
                .min(Long::compare)
                .orElseThrow();
        System.out.println("RESULT: " + loc);
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
                .filter(Predicate.not(String::isEmpty))
                .filter(Predicate.not(String::isBlank))
                .map(Long::parseLong)
                .toList();


        for (int i = 0; i < seedList.size(); i += 2) {
            var start = seedList.get(i);
            var end = start + seedList.get(i + 1);
            var mid = (start + end) / 2;

            seeds.add(LongStream.range(start, mid));
            seeds.add(LongStream.range(mid, end + 1));
        }
    }

    private static void parseMaps(Vector<String> alm) {
        for (String s : alm.stream()
                .collect(Collectors.joining(System.lineSeparator()))
                .split("\n\n")) {
            List<String> row = splitMap(s);
            String key = row.removeFirst();
            var res = row.stream()
                    .map(line -> Arrays.stream(line.split(" "))
                            .mapToLong(Long::parseLong)
                            .toArray())
                    .toList();

            switch (key) {
                case String k when k.startsWith("seed-to-soil") -> soilMap = res;
                case String k when k.startsWith("soil-to-fertilizer") -> fertMap = res;
                case String k when k.startsWith("fertilizer-to-water") -> waterMap = res;
                case String k when k.startsWith("water-to-light") -> lightMap = res;
                case String k when k.startsWith("light-to-temperature") -> tempMap = res;
                case String k when k.startsWith("temperature-to-humidity") -> humidMap = res;
                case String k when k.startsWith("humidity-to-location") -> locMap = res;
                default -> throw new RuntimeException("SHIT");
            }
        }
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


    private static Long convertMapToVal(Long seed, List<long[]> map) {
        for (long[] l : map) {
            if (l[1] <= seed && seed < l[1] + l[2]) {
                return seed + (l[0] - l[1]);
            }
        }
        return seed;
    }

}
