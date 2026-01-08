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
        public Node parent;
        public boolean visited;
        public int numRoutes;

        public Node(String id) {
            this.id = id;
            nbors = new ArrayList<>();
        }

        public void addNbor(Node node){
            nbors.add(node);
        }
    }

    Map<String, Node> map;

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

        int sum = countPaths(start, goal);

        System.out.println("Part 1: " + sum);

        start = getNode("svr");
        goal = getNode("out");
        Node stop1 = getNode("fft");
        Node stop2 = getNode("dac");

        Node choke = getNode("afb");

//        int sumA = countPaths(start, stop1);
//        System.out.println("Part 2:A " + sumA);
//
//        int sumB = countPaths(start, stop2);
//        System.out.println("Part 2:B " + sumB);

        //int sum2 = countPaths(start, goal);

//        long p1 = countPaths(stop2, stop1, 0, stop1, stop2, 0, 20);
//        System.out.println("p1 = "+p1);

        long p1 = countPaths(start, stop1, 0, stop1, stop2, 0, 10);
        System.out.println("p1 = "+p1);
        long p2 = countPaths(stop1, stop2, 0, stop1, stop2, 0, 18);
        System.out.println("p2 = "+p2);
        long p3 = countPaths(stop2, goal, 0, stop1, stop2, 0, 10);
        System.out.println("p3 = "+p3);
        long sum2 = p1*p2*p3;

        //correct: 367579641755680


        //int sum2 = countPaths(start, stop1, 0, stop1, stop2, 0, 20);
        System.out.println("Part 2: " + sum2);

        final long endTime = System.currentTimeMillis();
        System.out.println("\nExecution time : " + (endTime - startTime) + " ms");
    }

    int countPaths(Node node, Node goal){
        if(node == goal){
            //System.out.println("found path");
            return 1;
        }
        int count = 0;
        for(Node child : node.nbors){
            count += countPaths(child, goal);
        }
        return count;
    }

    int countPaths(Node node, Node goal, int visitBits, Node stop1, Node stop2, int steps, int maxSteps){
        if(node.visited) {
            //System.out.println("Node already visited, visitBits = "+visitBits);
            //return 0;
        }
        if(inLoop(node)){
            System.out.println("Loop!");
        }
        if(steps > maxSteps)
            return 0;

        //System.out.print(steps+" ");
        if(steps > 100){
            System.out.println("Loop?");
        }
        node.visited = true;

        if(node == stop1)
            visitBits |= 0x01;
        if(node == stop2)
            visitBits |= 0x02;

        if(node == goal){
//            System.out.print("found path, visitBits = "+visitBits+" ");
//            int len = 0;
//            for(Node n = node; n != null; n = n.parent){
//                System.out.print(n.id+" ");
//                len++;
//            }
//            System.out.println(" len = "+len);
            return 1; //visitBits == 0x3 ? 1 : 0;
        }
        int count = 0;
        for(Node child : node.nbors){
//            if(child.visited)
//                continue;
            child.parent = node;
            count += countPaths(child, goal, visitBits, stop1, stop2, steps+1, maxSteps);
        }
        return count;
    }

    void annotateRoutes(Node goal){
        goal.numRoutes = 1;
        for(Node node : map.values()){
            for(Node child: node.nbors){
                if(child == goal)
                    node.numRoutes = 1;
            }

        }
    }



    private boolean inLoop(Node node){
        for(Node n = node.parent; n != null; n = n.parent){
            if(n == node)
                return true;
        }
        return false;
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
