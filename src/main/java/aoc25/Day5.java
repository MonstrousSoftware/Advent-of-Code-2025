package aoc25;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Day5 {

    public static class Range {
        long start;
        long end;

        public Range(long start, long end) {
            this.start = start;
            this.end = end;
        }

        public boolean overlaps(Range r){
            if(r.end < start || r.start > end)
                return false;
            return true;
        }

        public static Range merge(Range a, Range b){
            long s = Math.min(a.start, b.start);
            long e = Math.max(a.end, b.end);
            return new Range(s,e);
        }
    }

    public Day5() {
        System.out.println("Day 5");
        final long startTime = System.currentTimeMillis();

        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get("data/day5.txt"));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        List<Long> rangeStarts = new ArrayList<Long>();
        List<Long> rangeEnds = new ArrayList<Long>();

        long sum = 0;
        int mode = 1;
        for(String line : lines ){
            if(line.isEmpty()){
                mode = 2;
                continue;
            }
            if(mode == 1){
                String[] words = line.split("-");
                long start = Long.parseLong(words[0]);
                long end = Long.parseLong(words[1]);
                rangeStarts.add(start);
                rangeEnds.add(end);

            } else {

                long id = Long.parseLong(line);
                boolean fresh = false;
                for(int i = 0; i < rangeStarts.size(); i++){
                    if(id >= rangeStarts.get(i) && id <= rangeEnds.get(i)) {
                        fresh = true;
                        break;
                    }
                }
                if(fresh)
                    sum++;
                //System.out.println("Id "+id+" "+fresh);

            }

        }

        int part = 1;
        System.out.println("Part "+part+": " + sum);

        part = 2;

        List<Range> ranges = new ArrayList<>();
        List<Range> toDelete = new ArrayList<>();
        for(String line : lines ) {
            if (line.isEmpty())
                break;
            String[] words = line.split("-");
            long start = Long.parseLong(words[0]);
            long end = Long.parseLong(words[1]);
            Range range = new Range(start, end);

            for(Range r : ranges) {
                if (range.overlaps(r)) {
                    Range merged = Range.merge(range, r);
                    toDelete.add(r);
                    toDelete.add(range);
                    range = merged;
                }
            }
            ranges.add(range);
            ranges.removeAll(toDelete);


        }
        sum = 0;
        for(Range r : ranges) {
            //System.out.println(r.start+" - "+r.end);
            sum += 1 + r.end - r.start;
        }

        System.out.println("Part "+part+": " + sum);



        final long endTime = System.currentTimeMillis();
        System.out.println("\nExecution time : " + (endTime - startTime) + " ms");

    }

}
