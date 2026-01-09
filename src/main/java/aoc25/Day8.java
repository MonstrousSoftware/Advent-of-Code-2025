package aoc25;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Day8 {

    final static int NON_CIRCUIT = 1000000; // starting circuit id value for junctions not in a circuit

    public static class Vec3{
        long x, y, z;
        public int circuitId;

        public Vec3(long x, long y, long z) {
            this.x = x;
            this.y = y;
            this.z = z;
            circuitId = circuitNr++;
        }

        public long distance2( Vec3 v ){
            long dx = x-v.x;
            long dy = y-v.y;
            long dz = z-v.z;
            return dx*dx + dy*dy + dz*dz;
        }

        public void print(){
            System.out.print(x+", "+y+", "+z);
        }
    }

    public static class Connection implements Comparable<Connection> {
        Vec3 a;
        Vec3 b;
        long distance;

        @Override
        public int compareTo(Connection a) {
            if(distance > a.distance)
                return 1;
            if(distance < a.distance)
                return -1;
            return 0;
        }
    }


    List<Connection> connections;
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


        // read list of 3d vectors (integer values): the positions of the junction boxes

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
        connections = new ArrayList<>();


        for(int part = 1; part <= 2; part++) {

            // reset circuit id's to be unique per junction
            // (needs to be reset after part1 because they will have been modified).
            int circuitId = NON_CIRCUIT;
            for (Vec3 v : vectors) {
                v.circuitId = circuitId++;  // each junction get its own unique circuit id
            }

            // calculate distances between each pair of vectors
            for (int x = 0; x < numVecs; x++) {
                for (int y = 0; y < numVecs; y++) {
                    if (y >= x)
                        break;
                    Vec3 va = vectors.get(x);
                    Vec3 vb = vectors.get(y);
                    long d = va.distance2(vb);

                    Connection conn = new Connection();
                    conn.a = va;
                    conn.b = vb;
                    conn.distance = d;
                    connections.add(conn);
                }
            }

            Collections.sort(connections);  // sort by increasing distance

            int newCircuit = 1;
            final int limit = 1000;     // 10 for the example data
            int[] circuitSizes = new int[limit];


            for (int iter = 0; iter < 1000000; iter++) {    // "forever"
                if(part == 1 && iter == limit)  // part 1 stops after 1000 connections
                    break;
                Connection match = connections.get(iter);
                if (match == null) {
                    System.out.println("No closest pair found");
                    break;
                }

                if (match.a.circuitId == match.b.circuitId) // already in same circuit, do nothing
                    continue;
                if (match.a.circuitId >= NON_CIRCUIT && match.b.circuitId >= NON_CIRCUIT) { // 2 unconnected junctions, create a new circuit
                    match.a.circuitId = newCircuit;
                    match.b.circuitId = newCircuit;
                    circuitSizes[newCircuit] = 2;
                    newCircuit++;
                } else if (match.a.circuitId >= NON_CIRCUIT) {  // b is already in a circuit, let a join
                    match.a.circuitId = match.b.circuitId;
                    circuitSizes[match.a.circuitId]++;
                    if(circuitSizes[match.a.circuitId] == vectors.size()){  // if this circuit contains all junctions, part 2 is done
                        printPart2(match);
                        break;
                    }
                } else if (match.b.circuitId >= NON_CIRCUIT) {  // adn vice versa
                    match.b.circuitId = match.a.circuitId;
                    circuitSizes[match.b.circuitId]++;
                    if(circuitSizes[match.b.circuitId] == vectors.size()){
                        printPart2(match);
                        break;
                    }
                } else { // 2 connected junctions, merge into once circuit (the circuit of a)
                    int old = match.b.circuitId;
                    int newValue = match.a.circuitId;

                    for (Vec3 v : vectors) { // find and replace
                        if (v.circuitId == old) {
                            v.circuitId = newValue;
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

                Arrays.sort(circuitSizes);  // in ascending order
                int n = circuitSizes.length;
                // get top 3 sizes (at the end of the array)
                long product = (long)circuitSizes[n-1]*(long)circuitSizes[n-2]*(long)circuitSizes[n-3];
                System.out.println("Part 1: " + product);
            }
        }
        final long endTime = System.currentTimeMillis();
        System.out.println("\nExecution time : " + (endTime - startTime) + " ms");

    }


    private void printPart2(Connection connection){
//        System.out.println("Part 2 : ");
//        match.a.print();
//        match.b.print();
        long product = connection.a.x * connection.b.x;
        System.out.println("Part 2 : "+product);
    }

}
