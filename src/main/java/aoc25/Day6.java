package aoc25;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Day6 {


    public Day6() {
        System.out.println("Day 6");
        final long startTime = System.currentTimeMillis();

        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get("data/day6.txt"));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        String[] words = lines.get(0).split("\s+");
        int cols = words.length;
        long[] products = new long[cols];
        long[] sums = new long[cols];
        for(int i = 0; i < cols; i++){
            products[i] = 1;
            sums[i] = 0;
        }
        long sum = 0;
        for(String line : lines){
            line = line.trim();
            words = line.split("\s+");
            if(words[0].contentEquals("+")|| words[0].contentEquals("*")){
                for (int i = 0; i < cols; i++) {
                    if(words[i].contentEquals("+"))
                        sum += sums[i];
                    else
                        sum += products[i];
                }
            } else {
                for (int i = 0; i < cols; i++) {
                    long value =  Long.parseLong(words[i]);
                    products[i] *= value;
                    sums[i] += value;
                }
            }
        }
        System.out.println("Part 1: " + sum);

        int numRows = lines.size();
        int numChars = 0;
        for(int i = 0; i < numRows-1; i++){
            int n = lines.get(i).length();
            numChars = Math.max(n, numChars);
        }
        //int numChars = lines.getFirst().length();
        String[] numbers = new String[numChars];
        for(int j = 0; j < numChars; j++)
            numbers[j] = "";

        for(int i = 0; i < numRows-1; i++){
            String line = lines.get(i);
            for(int j = 0; j < numChars && j < line.length(); j++){
                char k = line.charAt(j);
                numbers[j] = numbers[j] + k;
            }
        }
        String line = lines.get(numRows-1);
        line = line.trim();
        words = line.split("\s+");
        sum = 0;
        int index = 0;
        for(String word : words){
            long total;
            if(word.contentEquals("+")){
                total = 0;
            } else {
                total = 1;
            }
            for(;;) {
                if(index >= numbers.length){
                    sum += total;
                    break;
                }
                String number = numbers[index].trim();
                if (number.isEmpty()) {
                    sum += total;
                    index++;
                    break;
                } else {
                    long value = Long.parseLong(number);
                    if (word.contentEquals("+"))
                        total += value;
                    else
                        total *= value;
                }
                index++;
            }


        }
        System.out.println("Part 2: " + sum);



        final long endTime = System.currentTimeMillis();
        System.out.println("\nExecution time : " + (endTime - startTime) + " ms");

    }

}
