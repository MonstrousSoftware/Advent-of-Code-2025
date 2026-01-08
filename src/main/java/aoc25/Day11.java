package aoc25;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day11 {

    public static class Node {
        String id;
        List<Node> nbors;

        public Node(String id) {
            this.id = id;
            nbors = new ArrayList<>();
        }

        public void addNbor(Node node){
            nbors.add(node);
        }
    }

    Map<String, Node> map;
    Map<String, Long> cache;

    public Day11() {
        System.out.println("Day 11");
        final long startTime = System.currentTimeMillis();

        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get("data/day11.txt"));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // aaa: you hhh

        map = new HashMap<>();
        cache = new HashMap<>();

        for(String line : lines){
            String[] parts = line.split(":");
            String parent = parts[0];
            String[] children = parts[1].trim().split(" ");
            Node node = getNode(parent);
            for(String child: children) {
                Node childNode = getNode(child);
                node.addNbor(childNode);
            }
        }

        Node start = getNode("you");
        Node goal = getNode("out");

        long sum = countPaths(start, goal);

        System.out.println("Part 1: " + sum);

        start = getNode("svr");
        goal = getNode("out");
        Node fft = getNode("fft");
        Node dac = getNode("dac");

        // do in 3 steps: src -> fft -> dac -> out and then multiply the # of paths
        cache.clear();
        long p1 = countPaths(start, fft);
        cache.clear();
        long p2 = countPaths(fft, dac);
        cache.clear();
        long p3 = countPaths(dac, goal);
        long sum2a = p1*p2*p3;

        // for completeness, we also try the other order: src -> dac -> fft -> out
        // although in practice this is zero.
        cache.clear();
        p1 = countPaths(start, dac);
        cache.clear();
        p2 = countPaths(dac, fft);
        cache.clear();
        p3 = countPaths(fft, goal);
        long sum2b = p1*p2*p3;

        long sum2 = sum2a + sum2b;

        //correct: 367579641755680

        System.out.println("Part 2: " + sum2);

        final long endTime = System.currentTimeMillis();
        System.out.println("\nExecution time : " + (endTime - startTime) + " ms");
    }

    long countPaths(Node node, Node goal){
        if(cache.containsKey(node.id))
            return cache.get(node.id);

        if(node == goal){
            return 1;
        }
        long count = 0;
        for(Node child : node.nbors){
            count += countPaths(child, goal);
        }
        cache.put(node.id, count);
        return count;
    }

    private Node getNode(String id){
        Node nd = map.get(id);
        if(nd == null){
            nd = new Node(id);
            map.put(id, nd);
        }
        return nd;
    }


}
