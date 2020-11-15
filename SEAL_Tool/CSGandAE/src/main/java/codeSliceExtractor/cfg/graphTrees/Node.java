package codeSliceExtractor.cfg.graphTrees;

import java.util.ArrayList;

/**
 * Node from the trees with information regarding its roots or if it is root
 **/
public class Node {
    private ArrayList<Integer> roots = new ArrayList<Integer>();
    private RootState isRoot;

    public Node() {
        this.isRoot = RootState.UNDEFINED;
    }

    public RootState getIsRoot() {
        return this.isRoot;
    }

    public ArrayList<Integer> getRoots() {
        return this.roots;
    }

    public void setIsRoot(RootState isRoot) {
        this.isRoot = isRoot;
    }

    public void addRoot(int root) {
        this.roots.add(root);
    }
}
