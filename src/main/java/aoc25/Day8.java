package aoc25;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Day8 {

    final static int NON_CIRCUIT = 1000000;

    public static class Vec3{
        long x, y, z;
        public int circuit;

        public Vec3(long x, long y, long z) {
            this.x = x;
            this.y = y;
            this.z = z;
            circuit = circuitNr++;
        }

        public float distance( Vec3 v ){
            float dx = x-v.x;
            float dy = y-v.y;
            float dz = z-v.z;
            float d2 = dx*dx + dy*dy + dz*dz;
            return (float)Math.sqrt(d2);
        }

        public void print(){
            System.out.print(x+", "+y+", "+z);
        }
    }

    public static class Match {
        Vec3 a;
        Vec3 b;
        float distance;
    }

    float[][] distances;
    List<Vec3> vectors;
    int numVecs;
    static int circuitNr = NON_CIRCUIT;

    public Day8() {
        System.out.println("Day 8");
        final long startTime = System.currentTimeMillis();

        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get("data/day8.txt"));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        vectors = new ArrayList<>();
        for(String line : lines){
            String [] words = line.split(",");
            long x = Long.parseLong(words[0]);
            long y = Long.parseLong(words[1]);
            long z = Long.parseLong(words[2]);
            Vec3 v = new Vec3(x,y,z);
            vectors.add(v);
        }

        numVecs = vectors.size();
        distances = new float[numVecs][numVecs];

        for(int part = 1; part <= 2; part++) {

            for (int x = 0; x < numVecs; x++) {
                vectors.get(x).circuit = NON_CIRCUIT+x;
                for (int y = 0; y < numVecs; y++) {
                    if (y >= x)
                        break;
                    Vec3 va = vectors.get(x);
                    Vec3 vb = vectors.get(y);
                    float d = va.distance(vb);
                    distances[x][y] = d;
                }
            }

            int newCircuit = 1;
            final int limit = 1000;
            int[] circuitSizes = new int[limit];


            for (int iter = 0; iter < 1000000; iter++) {
                if(part == 1 && iter == limit)
                    break;
                Match match = findClosest();
                if (match == null) {
                    System.out.println("No closest pair found");
                    break;
                }

//                match.a.print();
//                System.out.print(" to ");
//                match.b.print();
//                System.out.println(" Min distance = " + match.distance);

                if (match.a.circuit == match.b.circuit)
                    continue;
                if (match.a.circuit >= NON_CIRCUIT && match.b.circuit >= NON_CIRCUIT) {
                    match.a.circuit = newCircuit;
                    match.b.circuit = newCircuit;
                    circuitSizes[newCircuit] = 2;
                    newCircuit++;
                } else if (match.a.circuit >= NON_CIRCUIT) {
                    match.a.circuit = match.b.circuit;
                    circuitSizes[match.a.circuit]++;
                    if(circuitSizes[match.a.circuit] == vectors.size()){
                        printPart2(match);
                        break;
                    }
                } else if (match.b.circuit >= NON_CIRCUIT) {
                    match.b.circuit = match.a.circuit;
                    circuitSizes[match.b.circuit]++;
                    if(circuitSizes[match.b.circuit] == vectors.size()){
                        printPart2(match);
                        break;
                    }
                } else {
                    int old = match.b.circuit;
                    int newValue = match.a.circuit;
//                System.out.println("Linking circuits " + match.a.circuit + "["+circuitSizes[match.a.circuit]+
//                                "] and " + match.b.circuit+ "["+circuitSizes[match.b.circuit]+"]");
                    for (Vec3 v : vectors) {
                        if (v.circuit == old) {
                            v.circuit = newValue;
                        }
                    }
                    circuitSizes[newValue] += circuitSizes[old];
                    circuitSizes[old] = 0;
                    if(circuitSizes[newValue] == vectors.size()){
                        printPart2(match);
                        break;
                    }
                }
            }

            if(part == 1) {
                long product = 1;
                for (int n = 0; n < 3; n++) {
                    int max = -1;
                    int maxIndex = 0;
                    for (int i = 0; i < limit; i++) {
                        if (circuitSizes[i] > max) {
                            max = circuitSizes[i];
                            maxIndex = i;
                        }
                    }
                    circuitSizes[maxIndex] = 0;
                    product *= max;
                }
                System.out.println("Part 1: " + product);
            }
        }
        final long endTime = System.currentTimeMillis();
        System.out.println("\nExecution time : " + (endTime - startTime) + " ms");

    }

    public Match findClosest(){
        Match match = new Match();

        float minDist = Float.MAX_VALUE;
        int mx = -1;
        int my = -1;
        for(int x = 0; x < numVecs; x++) {
            for (int y = 0; y < numVecs; y++) {
                if (y >= x)
                    break;
                if(distances[x][y]<minDist){
                    match.distance = distances[x][y];
                    match.a = vectors.get(x);
                    match.b = vectors.get(y);
                    mx = x;
                    my = y;
                    minDist = match.distance;
                }

            }
        }
        if(minDist == Float.MAX_VALUE)
            return null;
        distances[mx][my] = Float.MAX_VALUE;
        return match;
    }

    private void printPart2(Match match){
//        System.out.println("Part 2 : ");
//        match.a.print();
//        match.b.print();
        long product = match.a.x * match.b.x;
        System.out.println("Part 2 : "+product);
    }

}
