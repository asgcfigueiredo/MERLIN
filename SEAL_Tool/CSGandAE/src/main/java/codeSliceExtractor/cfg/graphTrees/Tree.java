package codeSliceExtractor.cfg.graphTrees;

import java.util.ArrayList;

public class Tree {
    private int root;
    private ArrayList<Integer[]> edges = new ArrayList<Integer[]>();

    public Tree (int root) {
        this.root = root;
    }

    public Tree (int root, ArrayList<Integer[]> edges) {
        this.root = root;
        this.edges = edges;
    }

    public int getRoot() {
        return this.root;
    }

    public ArrayList<Integer[]> getEdges() {
        return this.edges;
    }

    public void addEdge(int node1, int node2) {
        Integer[] edge = {node1, node2};
        edges.add(edge);
    }

    public boolean containsEdge(int node1, int node2) {
        Integer[] edge = {node1, node2};
        return edges.contains(edge);
    }

    public ArrayList<Integer> getNextEdges(int index) {
        ArrayList<Integer> nextEdges =  new ArrayList<Integer>();
        for (Integer[] edges : this.edges) {
            if (edges[0] == index)  {
                nextEdges.add(edges[1]);
            }
        }
        return nextEdges;
    }

    public void printTrees() {
        System.out.println("Root: " + this.root);
        for(Integer[] edge : this.edges) {
            System.out.println("Edge: [" + edge[0] + ", " + edge[1] + "]");
        }
    }
}
