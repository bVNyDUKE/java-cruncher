import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Main {
    private static Vector<Long> seeds = new Vector<>();
    private static HashMap<String, Vector<Long>> maps = new HashMap<>();
    public static void main(String[] args){
        System.out.println("Hello world");
        Vector<String> alm = readAlm();

        parseSeeds(alm.removeFirst());

        System.out.println("Object " + alm);
        System.out.println("Length " + alm.size());
        System.out.println("Length " + alm.getFirst());
        System.out.println("Seeds" + seeds);
    }

    private static Vector<String> readAlm(){
        try(BufferedReader reader = new BufferedReader(new FileReader("src/input.txt"))){
            return reader.lines().collect(Collectors.toCollection(Vector::new));
        } catch (IOException e){
            System.err.println("Error reading the input file");
            System.exit(1);
        }
        return null;
    }

    private static void parseSeeds(String seedLine){
        seeds = Arrays.stream(seedLine.split(" "))
                .filter(Predicate.not((str) -> str.startsWith("seeds")))
                .map(Long::parseLong)
                .collect(Collectors.toCollection(Vector::new));
    }
}
