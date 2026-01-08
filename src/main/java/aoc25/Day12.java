package aoc25;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day12 {

    public static class Shape {
        public int id;
        public int size;    // assumes square
        char[][][] pixels;
        public int orientation;
        public char letter;
        public int area;    // nr of #'s

        public Shape(int id, int size) {
            this.id = id;
            this.size = size;
            pixels = new char[8][size][size];
            orientation = 0;
            letter = '#';
        }

        public Shape(Shape sh){
            this.id = sh.id;
            this.size = sh.size;
            this.pixels = sh.pixels;
            this.orientation = sh.orientation;
            this.area = sh.area;
            this.letter = sh.letter;
        }
    }

    List<Shape> shapes = new ArrayList<>();
    char shapeLetter;
    private final Map<Long, Integer> cache = new HashMap<>();;

    public Day12() {
        System.out.println("Day 12");
        final long startTime = System.currentTimeMillis();

        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get("data/day12.txt"));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }


        long sum = 0;
        boolean shapeMode = true;
        int lineNr = 0;
        while(lineNr < lines.size()){

            if(shapeMode) {
                //0:
                //.##
                //##.
                //#..
                // blank line
                String line = lines.get(lineNr);
                String[] words = line.split(":");
                if(words[0].length() > 1) {
                    shapeMode = false;
                    continue;
                }
                lineNr++;
                int id = Integer.parseInt(words[0]);
                line = lines.get(lineNr++);
                int sz = line.length();
                Shape shape = new Shape(id, sz);
                int area = 0;
                for(int y = 0; y < sz; y++){
                    for(int x = 0; x < sz; x++) {
                        shape.pixels[0][y][x] = line.charAt(x);
                        if(shape.pixels[0][y][x] == '#')
                            area++;
                    }
                    line = lines.get(lineNr++);
                }
                shape.area = area;
                buildOrientations(shape);
                //printShape(shape);
                shapes.add(shape);
                if(!line.isEmpty())
                    System.out.println("Expected empty line: "+lineNr);
            } else {
                // 4x4: 0 0 0 0 2 0
                String line = lines.get(lineNr++);
                String[] words = line.split(":");
                String[] dims = words[0].split("x");
                int w = Integer.parseInt(dims[0]);
                int h = Integer.parseInt(dims[1]);
                String[] counts = words[1].trim().split(" ");
                int[] numShapes = new int[counts.length];
                for(int s = 0; s < counts.length; s++)
                    numShapes[s] = Integer.parseInt(counts[s]);

                cache.clear();
                boolean fits = fitBlocks(w, h, numShapes);
                if(fits)
                    sum ++;
            }
        }


        System.out.println("Part 1: " + sum);

        final long endTime = System.currentTimeMillis();
        System.out.println("\nExecution time : " + (endTime - startTime) + " ms");
    }

    private boolean fitBlocks(int w, int h, int[] numShapes){

        char[][] grid = new char[h][w];
        for(int y = 0; y < h; y++)
            for(int x = 0; x < w; x++)
                grid[y][x] = '.';

        shapeLetter = 'A';
        int totalArea = 0;
        List<Shape> shapesToPlace = new ArrayList<>();
        for(int sh = 0; sh < numShapes.length; sh++){
            int count = numShapes[sh];
            Shape shape = shapes.get(sh);

            for(int n = 0; n < count; n++){
                Shape s = new Shape(shape);
                s.letter = shapeLetter;
                shapeLetter = nextLetter(shapeLetter);
                shapesToPlace.add(s);        // new shape because orientation is not fixed
                totalArea += s.area;
            }
        }

        //System.out.println("blocks area= "+totalArea+" grid area="+(w*h));
        if(totalArea > w*h)
            return false;

        boolean fits = tryFit(grid, 0, shapesToPlace);
        if(!fits) {
            return false;
        }
//        System.out.println("Fits:");
//        printGrid(grid, w, h);
        return true;
    }



    private boolean tryFit(char[][]grid, int shapeIndex, List<Shape> shapeList){

        if(shapeIndex == shapeList.size())
            return true;

        int h = grid.length;
        int w = grid[0].length;
        long hash = hashCode(grid, w, h, shapeIndex);
        Integer test = cache.get(hash);
        if(test != null)
            return false;
        Shape shape = shapeList.get(shapeIndex);
        for(int ori = 0; ori < 8; ori++) {
            shape.orientation = ori;
            for (int y = 0; y <= h - shape.size; y++) {
                for (int x = 0; x <= w - shape.size; x++) {
                    if (testOrientedShape(grid, x, y, shape)) {
                        placeShape(grid, x, y, shape);
                        //printGrid(grid, w, h);
                        boolean fits = tryFit(grid, shapeIndex+1, shapeList);
                        if(fits)
                            return true;
                        removeShape(grid, x, y, shape);
                    }
                }
            }
        }
        cache.put(hash, 1);
        return false;
    }


    private boolean testOrientedShape(char[][]grid, int px, int py, Shape shape){
        for (int ly = 0; ly < shape.size; ly++) {
            for (int lx = 0; lx < shape.size; lx++) {
                if (shape.pixels[shape.orientation][ly][lx] == '#' && grid[py + ly][px + lx] != '.'){
                    return false;
                }
            }
        }
        return true;
    }

    private boolean testShape(char[][]grid, int px, int py, Shape shape){
        for(int ori = 0; ori < 8; ori++) {
            shape.orientation = ori;
            boolean fits = true;
            for (int ly = 0; ly < shape.size; ly++) {
                for (int lx = 0; lx < shape.size; lx++) {
                    if (shape.pixels[ori][ly][lx] == '#' && grid[py + ly][px + lx] != '.'){
                        fits = false;
                        break;
                    }
                }
                if(!fits)
                    break;
            }
            if(fits)
                return true;
        }
        return false;
    }

    private void placeShape(char[][]grid, int px, int py, Shape shape){
        for(int ly = 0; ly < shape.size; ly++){
            for(int lx = 0; lx < shape.size; lx++){
                if(shape.pixels[shape.orientation][ly][lx] == '#')
                    grid[py+ly][px+lx] = shape.letter;
            }
        }
    }

    private void removeShape(char[][]grid, int px, int py, Shape shape) {
        for (int ly = 0; ly < shape.size; ly++) {
            for (int lx = 0; lx < shape.size; lx++) {
                if (shape.pixels[shape.orientation][ly][lx] == '#')
                    grid[py + ly][px + lx] = '.';
            }
        }
    }

    private char nextLetter(char letter){
        if(letter == 'Z')
            letter = 'a';
        else if(letter == 'z')
            letter = 'A';
        else
            letter++;
        return letter;
    }

    private void printGrid(char[][]grid, int w, int h){

        for(int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                System.out.print(grid[y][x]);
            }
            System.out.println();
        }
        System.out.println();
    }

    private long hashCode(char[][]grid, int w, int h, int shapeIndex){
        long hash = 1+shapeIndex;
        for(int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                hash *= 31;
                if(grid[y][x] != '.')
                    hash++;
            }
        }
        return hash;
    }

    private void buildOrientations(Shape shape){
        int w = shape.size-1;
        for(int ori = 1; ori < 8; ori++) {
            for (int ly = 0; ly < shape.size; ly++) {
                for (int lx = 0; lx < shape.size; lx++) {
                    switch(ori){
                        case 1: shape.pixels[ori][ly][lx] = shape.pixels[0][lx][w-ly]; break;
                        case 2: shape.pixels[ori][ly][lx] = shape.pixels[0][w-ly][w-lx]; break;
                        case 3: shape.pixels[ori][ly][lx] = shape.pixels[0][w-lx][ly]; break;

                        case 4: shape.pixels[ori][ly][lx] = shape.pixels[0][ly][w-lx]; break;
                        case 5: shape.pixels[ori][ly][lx] = shape.pixels[0][w-lx][w-ly]; break;
                        case 6: shape.pixels[ori][ly][lx] = shape.pixels[0][w-ly][lx]; break;
                        case 7: shape.pixels[ori][ly][lx] = shape.pixels[0][lx][ly]; break;
                    }
                }
            }
        }
    }

    private void printShape(Shape shape){
        for(int ori = 0; ori < 8; ori++) {
            System.out.println("Orientation "+ori+":");
            for (int y = 0; y < shape.size; y++) {
                for (int x = 0; x < shape.size; x++) {
                    System.out.print(shape.pixels[ori][y][x]);
                }
                System.out.println();
            }
            System.out.println();
        }
    }


}
