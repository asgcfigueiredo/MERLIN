package codeSliceExtractor.cfg;

import codeSliceExtractor.cfg.graphTransformation.Digraph;
import codeSliceExtractor.cfg.graphTrees.Tree;
import codeSliceExtractor.cfg.graphTrees.TreeExtraction;
import codeSliceExtractor.processJimple.codeData.Var;
import codeSliceExtractor.processJimple.processFuncs.JimpFuncInfo;
import codeSliceExtractor.processJimple.processTrees.TreeProcessing;
import com.paypal.digraph.parser.GraphEdge;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class GraphProcessing {
    private String filename;
    private String funcId;
    private ArrayList<JimpFuncInfo> funcInfo;
    private HashMap<String, Var> memVars = new HashMap<String, Var>();
    private ArrayList<Var> paramVars = new ArrayList<Var>();

    public GraphProcessing(String filename, ArrayList<JimpFuncInfo> funcNames, String funcId) {
        this.filename = filename;
        this.funcInfo = funcNames;
        this.funcId = funcId;
    }

    public GraphProcessing(String filename, ArrayList<JimpFuncInfo> funcNames, String funcId, ArrayList<Var> paramVars) {
        this(filename, funcNames, funcId);
        this.paramVars = paramVars;
    }

    public GraphProcessing(String filename, ArrayList<JimpFuncInfo> funcNames, HashMap<String, Var> memVars, String funcId, ArrayList<Var> paramVars) {
        this(filename, funcNames, funcId, paramVars);
        this.memVars = memVars;
    }

    public HashMap<String, Var> processing() {
        Digraph digraph = new Digraph(filename);
        int sizeNodes = digraph.getSizeNodes();
        ArrayList<ArrayList<Integer>> edges = new ArrayList<ArrayList<Integer>>(sizeNodes);
        ArrayList<Integer> edge = new ArrayList<Integer>();
        for(int i = 0; i < sizeNodes; i++) {
            edges.add(edge);
        }

        //Sort Edges, important for the tree extraction and tree processing
        String node_aux_1, node_aux_2;
        for (GraphEdge graphEdge : digraph.getEdges().values()) {
            node_aux_1 = graphEdge.getNode1().getId().replace("\"", "");
            node_aux_2 = graphEdge.getNode2().getId().replace("\"", "");
            edge = edges.get(Integer.parseInt(node_aux_1));
            edge.add(Integer.parseInt(node_aux_2));
            Collections.sort(edge);
            edges.set(Integer.parseInt(node_aux_1), (ArrayList<Integer>) edge.clone());
            edge.clear();
        }


        TreeExtraction treeExtraction = new TreeExtraction(edges, sizeNodes);
        treeExtraction.processing();
        HashMap<Integer, Tree> trees = treeExtraction.getTrees();
        /**Debug
         * for(Integer rootT : trees.keySet()) {
            trees.get(rootT).printTrees();
        }**/
        ArrayList<String> instructions = digraph.getListNodes();
        HashMap<String, Var> vars = new HashMap<String, Var>();
        //Processing each tree
        if(trees != null) {
            for (Integer key : trees.keySet()) {
                TreeProcessing treeProcessing;
                if(trees.get(key).getEdges().size() > 880) {
                    ArrayList<Integer[]> smallerTree = new ArrayList<Integer[]>(trees.get(key).getEdges().subList(0, 880));
                    Integer[] lastEdge = smallerTree.get(879);
                    int lastIndex = lastEdge[1];
                    ArrayList<String> instructionsClone = (ArrayList<String>) instructions.clone();
                    instructionsClone.set(lastIndex, "return");
                    Tree auxTree = new Tree(trees.get(key).getRoot(), smallerTree);
                    treeProcessing = new TreeProcessing(auxTree, instructionsClone, this.funcInfo, this.memVars, this.funcId, this.paramVars);
                }
                else { treeProcessing = new TreeProcessing(trees.get(key), instructions, this.funcInfo, this.memVars, this.funcId, this.paramVars); }
                vars = treeProcessing.processTree();
                /**
                    System.out.println("\n\nTHIS ARE THE VARS FROM THE FUNCTION " + this.funcId);
                    for(String v : vars.keySet()) {
                        System.out.println("Var: "+ v);
                        vars.get(v).printInstructions();
                    }
                **/
                System.out.println("Finished processing the tree with root: "+ key);
            }
        }
        return vars;
    }
}
