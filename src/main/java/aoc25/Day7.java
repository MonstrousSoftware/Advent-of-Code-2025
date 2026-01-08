package aoc25;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Day7 {


    public Day7() {
        System.out.println("Day 7");
        final long startTime = System.currentTimeMillis();

        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get("data/day7.txt"));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        long sum = 0;

        String line  = lines.get(0);
        int numCols = line.length();
        boolean[] beamCols = new boolean[numCols];

        for(int i = 0; i < line.length(); i++){
            char k = line.charAt(i);
            if(k == 'S') {
                beamCols[i] = true;
                break;
            }
        }
        int splits = 0;
        for(int i = 1; i < lines.size(); i++){
            line = lines.get(i);
            for(int b = 0; b < line.length(); b++){
                if(beamCols[b] && line.charAt(b)== '^'){
                    splits++;
                    beamCols[b-1] = true;
                    beamCols[b+1] = true;
                    beamCols[b] = false;
                }
            }

        }

        sum = splits;
        System.out.println("Part 1: " + sum);

        long[] beamNums = new long[numCols];
        line  = lines.get(0);
        for(int i = 0; i < line.length(); i++){
            char k = line.charAt(i);
            if(k == 'S') {
                beamNums[i] = 1;
                break;
            }
        }

        for(int i = 1; i < lines.size(); i++){
            line = lines.get(i);
            for(int b = 0; b < line.length(); b++){
                if(beamNums[b] > 0 && line.charAt(b)== '^'){
                    beamNums[b-1] += beamNums[b];
                    beamNums[b+1] += beamNums[b];
                    beamNums[b] = 0;
                }
            }

        }

        sum = 0;
        for(int b = 0; b < line.length(); b++)
            sum += beamNums[b];
        System.out.println("Part 2: " + sum);



        final long endTime = System.currentTimeMillis();
        System.out.println("\nExecution time : " + (endTime - startTime) + " ms");

    }

}
