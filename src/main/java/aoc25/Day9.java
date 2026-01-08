package aoc25;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Day9 {

    public static class Coordinate {
        long x, y;

        public Coordinate(long x, long y) {
            this.x = x;
            this.y = y;
        }

        public String toString(){
            return "["+x+", "+y+"]";
        }
    }

    List<Coordinate> coordinates = new ArrayList<>();

    public Day9() {
        System.out.println("Day 9");
        final long startTime = System.currentTimeMillis();

        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get("data/day9.txt"));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        coordinates = new ArrayList<>();
        for(String line : lines){
            String [] words = line.split(",");
            long x = Long.parseLong(words[0]);
            long y = Long.parseLong(words[1]);
            Coordinate xy = new Coordinate(x,y);
            coordinates.add(xy);
        }

        long maxArea = 0;
        for(int i = 0; i < coordinates.size(); i++){
            for(int j = i+1; j < coordinates.size(); j++){
                long dx = coordinates.get(i).x - coordinates.get(j).x;
                long dy = coordinates.get(i).y - coordinates.get(j).y;
                dx = 1 + Math.abs(dx);
                dy = 1 + Math.abs(dy);
                long area = dx * dy;
                //System.out.println("area "+area+coordinates.get(i).toString()+coordinates.get(j).toString());
                if(area > maxArea){
                    maxArea = area;
                    //System.out.println("new maxArea "+maxArea+coordinates.get(i).toString()+coordinates.get(j).toString());
                }

            }
        }
        System.out.println("Part 1: " + maxArea);

        maxArea = 0;
        for(int i = 0; i < coordinates.size(); i++){
            for(int j = i+1; j < coordinates.size(); j++){
                long dx = coordinates.get(i).x - coordinates.get(j).x;
                long dy = coordinates.get(i).y - coordinates.get(j).y;
                dx = 1 + Math.abs(dx);
                dy = 1 + Math.abs(dy);
                long area = dx * dy;
                //boolean valid = testRect(coordinates.get(i), coordinates.get(j));
                //System.out.println("area "+area+coordinates.get(i).toString()+coordinates.get(j).toString()+" "+valid);
                if( area > maxArea){
                    boolean valid = testRect(coordinates.get(i), coordinates.get(j));
                    if(valid) {
                        maxArea = area;
                        //System.out.println("new maxArea " + maxArea + coordinates.get(i).toString() + coordinates.get(j).toString());
                    }
                }

            }
        }
        System.out.println("Part 2: " + maxArea);


        final long endTime = System.currentTimeMillis();
        System.out.println("\nExecution time : " + (endTime - startTime) + " ms");

    }

    // if there is any coordinate (red tile) strictly inside the rectangle between a and b
    // then the rectangle is not valid
    private boolean testRect(Coordinate a, Coordinate b){
        long minX = Math.min(a.x, b.x);
        long maxX = Math.max(a.x, b.x);
        long minY = Math.min(a.y, b.y);
        long maxY = Math.max(a.y, b.y);

        for(int i = 0; i < coordinates.size(); i++){
            Coordinate c = coordinates.get(i);
            if(c == a || c == b)
                continue;
            if(c.x > minX && c.x < maxX && c.y > minY && c.y < maxY)
                return false;
        }
        // segments crossing rectangle?
        for(int i = 0; i < coordinates.size(); i++){
            Coordinate c1 = coordinates.get(i);
            Coordinate c2 = coordinates.get((i+1)%coordinates.size());
            if(c1.y == c2.y){
                if(c1.y <= minY || c1.y >= maxY)
                    continue;
                if(c1.x <= minX && c2.x >= maxX)
                    return false;
                if(c2.x <= minX && c1.x >= maxX)
                    return false;
            } else {
                if(c1.x <= minX || c1.x >= maxX)
                    continue;
                if(c1.y <= minY && c2.y >= maxY)
                    return false;
                if(c2.y <= minY && c1.y >= maxY)
                    return false;
            }
        }
        return true;
    }


}
