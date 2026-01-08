package aoc25;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Day4 {

    private final int[] dx = { -1, 0, 1, -1, 1, -1, 0, 1 };
    private final int[] dy = { -1, -1, -1, 0, 0, 1, 1, 1 };


    public Day4() {
        System.out.println("Day 4");
        final long startTime = System.currentTimeMillis();

        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get("data/day4.txt"));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        int numCols = lines.get(0).length();
        int numRows = lines.size();
        char[][] grid = new char[numRows][numCols];



        for(int part = 1; part <= 2; part++) {
            int ty = 0;
            for(String line: lines){
                for(int x = 0; x < numCols; x++)
                    grid[ty][x] = line.charAt(x);
                ty++;
            }

            int count;
            int sum = 0;
            do {

                count = 0;
                for (int y = 0; y < numRows; y++) {
                    for (int x = 0; x < numCols; x++) {
                        if (grid[y][x] == '.')
                            continue;
                        int nbors = 0;
                        for (int dir = 0; dir < 8; dir++) {
                            int sx = x + dx[dir];
                            int sy = y + dy[dir];
                            if (sx >= 0 && sx < numCols && sy >= 0 && sy < numRows
                                    && grid[sy][sx] != '.')
                                nbors++;
                        }
                        if (nbors < 4) {
                            count++;
                            grid[y][x] = 'x';
                        }
                    }
                }

                sum += count;

                for (int y = 0; y < numRows; y++) {
                    for (int x = 0; x < numCols; x++) {
                        if (grid[y][x] == 'x')
                            grid[y][x] = '.';
                    }
                }
            } while (part == 2 && count > 0);


            System.out.println("Part "+part+": " + sum);

//            Part 1: 1560
//            Part 2: 9609

        }


        final long endTime = System.currentTimeMillis();
        System.out.println("\nExecution time : " + (endTime - startTime) + " ms");

    }

}
