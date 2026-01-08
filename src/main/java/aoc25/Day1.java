package aoc25;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Day1 {
    private List<String> lines = null;
    private int dialPos;

    public Day1() {
        System.out.println("Day 1");
        final long startTime = System.currentTimeMillis();

        try {
            lines = Files.readAllLines(Paths.get("data/day1.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialPos = 50;
        int countZeroes = 0;
        for(String line : lines){
            char direction = line.charAt(0);
            int count = Integer.parseInt(line.substring(1));

            if(direction == 'L'){
                dialPos -= count;
            } else {
                dialPos += count;
            }
            dialPos = Math.floorMod(dialPos, 100);
            if(dialPos == 0)
                countZeroes++;
        }
        System.out.println("Part 1: "+countZeroes);

        dialPos = 50;
        countZeroes = 0;
        for(String line : lines){
            char direction = line.charAt(0);
            int count = Integer.parseInt(line.substring(1));

            if(direction == 'L'){
                int cycles = count / 100;
                countZeroes += cycles;
                count -= 100 * cycles;
                if(dialPos != 0 && count >= dialPos)
                    countZeroes++;
                dialPos -= count;
            } else {
                int cycles = count / 100;
                countZeroes += cycles;
                count -= 100 * cycles;
                if(dialPos != 0 && count >= 100 - dialPos)
                    countZeroes++;
                dialPos += count;
            }
            dialPos = Math.floorMod(dialPos, 100);
            //System.out.println("Dial at "+dialPos+" : "+countZeroes);
        }
        System.out.println("Part 2: "+countZeroes);



        final long endTime = System.currentTimeMillis();
        System.out.println("\nExecution time : " + (endTime - startTime) + " ms");

    }
}
