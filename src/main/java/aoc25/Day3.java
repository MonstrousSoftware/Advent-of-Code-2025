package aoc25;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Day3 {

    public Day3() {
        System.out.println("Day 3");
        final long startTime = System.currentTimeMillis();

        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get("data/day3.txt"));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        for(int part = 1; part <= 2; part++) {
            long sum = 0;

            for (String line : lines) {
                sum += getJoltage(line, part == 1 ? 2 : 12);
            }

            System.out.println("Part "+part+": " + sum);
        }
//        Part 1: 17316
//        Part 2: 171741365473332

        final long endTime = System.currentTimeMillis();
        System.out.println("\nExecution time : " + (endTime - startTime) + " ms");

    }

    private long getJoltage(String bank, int digits){
        long joltage = 0;
        int maxIndex = -1;
        for(int digit = 0; digit < digits; digit++) {
            char max = '0';

            for (int i = maxIndex+1; i <= (bank.length() - digits)+digit; i++) {
                if (bank.charAt(i) > max) {
                    max = bank.charAt(i);
                    maxIndex = i;
                }
            }
            joltage = joltage * 10 + (max - '0');
        }
        //System.out.println(joltage);
        return joltage;
    }
}
