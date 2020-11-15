package codeSliceExtractor.cfg.graphTrees;

import java.util.ArrayList;
import java.util.HashMap;

public class TreeExtraction {
    private ArrayList<ArrayList<Integer>> edges;
    private HashMap<Integer, Node> nodes = new HashMap<Integer, Node>();
    private HashMap<Integer, Tree> trees = new HashMap<Integer, Tree>();

    public TreeExtraction(ArrayList<ArrayList<Integer>> edges, int size) {
        this.edges = edges;
        for(int i = 0; i < size; i++) {
            Node node = new Node();
            nodes.put(i, node);
        }
    }

    public HashMap<Integer, Tree> getTrees() {
        return this.trees;
    }

    /**
     *******    Algorithm to extract the trees from the Digraph   ******
     */
    public void processing() {
        System.out.println("\nExtracting the trees");
        HashMap<Integer, Tree> treesAux = new HashMap<Integer, Tree>();
        ArrayList<Integer> roots = new ArrayList<Integer>();
        Node outputNode;
        for(int i = 0; i < nodes.size(); i++) {
            treesAux.clear();
            Node node = nodes.get(i);
            if(node.getIsRoot() == RootState.UNDEFINED) {
                node.setIsRoot(RootState.TRUE);
                Tree tree = new Tree(i);
                nodes.put(i, node);
                treesAux.put(tree.getRoot(), tree);
            }
            else if (node.getIsRoot() == RootState.FALSE) {
                roots.clear();
                roots = node.getRoots();
                for (Integer root : roots) {
                    treesAux.put(root, trees.get(root));
                }
            }
            else if (node.getIsRoot() == RootState.TRUE) {
                treesAux.put(i, trees.get(i));
            }
            int outputEdge;
            for(int j = 0; j < edges.get(i).size(); j++) {
                //if output edge bigger than index??
                outputEdge = edges.get(i).get(j);
                outputNode = nodes.get(outputEdge);
                if(outputNode.getIsRoot() == RootState.TRUE) {
                    HashMap<Integer, Tree> newTrees = new HashMap<Integer, Tree>();
                    Tree removeTree = treesAux.get(outputEdge);
                    for(Integer l : treesAux.keySet()) {
                        if(l != outputEdge) {
                            Tree concatTree = treesAux.get(l);
                            for(Integer[] edge : removeTree.getEdges()) {
                                if(!concatTree.containsEdge(edge[0], edge[1])) {
                                    concatTree.addEdge(edge[0], edge[1]);
                                }
                            }
                            newTrees.put(concatTree.getRoot(), concatTree);
                            trees.put(concatTree.getRoot(), concatTree);
                        }
                    }
                    treesAux = newTrees;
                    trees.remove(outputEdge);
                }
                for (Tree tree_aux : treesAux.values()) {
                    if(tree_aux != null) {
                        if (!outputNode.getRoots().contains(tree_aux.getRoot())) {
                            outputNode.addRoot(tree_aux.getRoot());
                        }
                        outputNode.setIsRoot(RootState.FALSE);
                        if (!tree_aux.containsEdge(i, outputEdge)) {
                            tree_aux.addEdge(i, outputEdge);
                            trees.put(tree_aux.getRoot(), tree_aux);
                        }
                    }
                }
                nodes.put(outputEdge, outputNode);
            }
        }
        System.out.println("Tree Extraction concluded. Found " + this.trees.size() + " trees.");
    }

    public void printTrees() {
        for (Integer key : trees.keySet()) {
            System.out.println("Tree Root: " + trees.get(key).getRoot());
            System.out.println("Edges: " );
            for( Integer[] edge : trees.get(key).getEdges()) {
                if (edge.length == 2) {
                    System.out.println("[" + edge[0] + ", " + edge[1] + "]");
                }
            }
        }
    }
}
