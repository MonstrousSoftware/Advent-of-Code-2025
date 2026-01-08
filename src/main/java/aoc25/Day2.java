package aoc25;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Day2 {


    public Day2() {
        System.out.println("Day 2");
        final long startTime = System.currentTimeMillis();

        String line;
        try {
            line = Files.readString(Paths.get("data/day2.txt"));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        long sum = 0;
        String[]  ranges = line.split(",");
        for(String range : ranges){
            String[] words = range.split("-");
            long start = Long.parseLong(words[0]);
            long end = Long.parseLong(words[1]);

            for(long iter = start; iter <= end; iter++){
                String num = String.valueOf(iter);
                int len = num.length();
                if(len % 2 != 0)
                    continue;
                int half = len/2;
                String s1 = num.substring(0, half);
                String s2 = num.substring(half);
                if(s1.contentEquals(s2))
                    sum += iter;
            }
        }

        System.out.println("Part 1: "+sum);

        long sum2 = 0;
        for(String range : ranges){
            String[] words = range.split("-");
            long start = Long.parseLong( words[0]);
            long end = Long.parseLong(words[1]);

            for(long iter = start; iter <= end; iter++){
                if(test(iter))
                    sum2 += iter;
            }
        }
        System.out.println("Part 2: "+sum2);



        final long endTime = System.currentTimeMillis();
        System.out.println("\nExecution time : " + (endTime - startTime) + " ms");

    }

    private boolean test(long number){
        String num = String.valueOf(number);
        int len = num.length();
        for(int div = len/2; div > 0; div--) {
            if (len % div != 0)
                continue;
            String ref = num.substring(0, div);
            boolean repeats = true;
            for(int i = 1; i < len/div; i++){
                String s = num.substring(i*div, (i+1)*div);
                if(!ref.contentEquals(s)) {
                    repeats = false;
                    break;
                }
            }
            if(repeats)
                return true;
        }
        return false;
    }
}
