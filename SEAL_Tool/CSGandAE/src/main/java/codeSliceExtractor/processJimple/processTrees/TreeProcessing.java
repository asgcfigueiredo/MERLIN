package codeSliceExtractor.processJimple.processTrees;

import codeSliceExtractor.cfg.graphTrees.Tree;
import codeSliceExtractor.processJimple.codeData.Instruction;
import codeSliceExtractor.processJimple.codeData.Var;
import codeSliceExtractor.processJimple.processFuncs.JimpFuncInfo;
import codeSliceExtractor.processJimple.processInstructions.ProcessCode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class TreeProcessing {
    private Tree tree;
    private ArrayList<String> instructions;
    private ArrayList<Boolean> processedInstructions;
    private ProcessCode processCode;
    private TreeInfo treeInfo;

    public TreeProcessing(Tree tree, ArrayList<String> instructions, ArrayList<JimpFuncInfo> funcInfo, HashMap<String, Var> memVars, String funcName,
                          ArrayList<Var> paramVars) {
        this.tree = tree;
        this.instructions = instructions;
        int size = instructions.size();
        this.processedInstructions = new ArrayList<Boolean>(Collections.nCopies(size,Boolean.FALSE));
        TreeInfo treeInfo = (memVars.isEmpty()) ? new TreeInfo(funcInfo, funcName, instructions, paramVars) : new TreeInfo(funcInfo, memVars, funcName, instructions, paramVars);
        this.treeInfo = treeInfo;
        this.processCode = new ProcessCode(treeInfo);
    }

    public HashMap<String, Var> processTree() {
        int root = tree.getRoot();
        try {
            this.processCode.processInstruction(this.instructions.get(root), null, root);
        }
        catch (Exception e) { /** ...do nothing... **/ }
        this.processedInstructions.set(root, Boolean.TRUE);
        int index = this.processNode(tree.getNextEdges(root));
        this.processBranches(index);
        return this.treeInfo.getVars();
    }

    public int processNode(ArrayList<Integer> edges) {
        if (edges.size() == 1) {
            if (!this.processedInstructions.get(edges.get(0))) {
                //System.out.println("Processing" + edges.get(0) + "....");
                //System.out.println(instructions.get(edges.get(0)));
                try {
                    this.processCode.processInstruction(instructions.get(edges.get(0)), null, edges.get(0));
                }
                catch (Exception e) { /** ...do nothing... **/}
                this.processedInstructions.set(edges.get(0), Boolean.TRUE);
            }
            return edges.get(0);
        }
        else {
            /**If it was already processed it means, that it is an infinite loop caused by for or while**/
            if (this.processedInstructions.get(edges.get(0))) {
                return -1;
            }
            ArrayList<Context> contextsNode = new ArrayList<Context>();
            //labelReturn is different than -1 because -1 is an impossible edge, which is perfect for the first iteration
            int labelReturn = -1;
            int outputEdge = -1;
            /**                     Debug
             * System.out.println("--------------------New Edges----------------");
            for (int i = 0; i < edges.size(); i++) {
                outputEdge = edges.get(i);
                System.out.println("OutputEdge: " + outputEdge);
            }
             **/
            for (int i = 0; i < edges.size(); i++) {
                outputEdge = edges.get(i);
                /**If there is next edge**/
                if (i != edges.size()-1) {
                    labelReturn = edges.get(i+1);
                }
                //simple_java example when we have a branch that ends in a return
                if (labelReturn != outputEdge) {
                    //System.out.println("------------------------------------\nLabel Return: " + labelReturn);
                    this.treeInfo.pushNewLabel(labelReturn);
                    this.treeInfo.pushNewContext();
                    //System.out.println("Processing" + outputEdge + "....");
                    try {
                        this.processCode.processInstruction(instructions.get(outputEdge), null, outputEdge);
                    }
                    catch (Exception e) { /** ...do nothing... **/}
                    this.processedInstructions.set(outputEdge, Boolean.TRUE);
                    labelReturn = processBranches(outputEdge);
                    if (labelReturn == -1) {
                        labelReturn = this.treeInfo.getLabelLastElement();
                        contextsNode.add(this.treeInfo.getContextLastElement());
                    }
                    else {
                        //It does not add contexts that end with a throw or a return since, they do not have any impact
                        //in the following instructions
                        contextsNode.add(this.treeInfo.getContextLastElement());
                    }
                    this.treeInfo.popContext();
                    this.treeInfo.popLabel();
                }
            }

            this.treeInfo.updateContexts(contextsNode);
            if (!this.processedInstructions.get(outputEdge)){
                //System.out.println("\n\nProcessing " +  outputEdge);
                try {
                    this.processCode.processInstruction(instructions.get(outputEdge), null, outputEdge);
                }
                catch (Exception e) { /** ...do nothing... **/}
                this.processedInstructions.set(outputEdge, Boolean.TRUE);
            }

            return labelReturn;
        }
    }

    public int processLabels(ArrayList<Integer> edges, int index) {
        int retIndex = -1;
        if(!this.treeInfo.isLabelReturnsEmpty()) {
            if(index == this.treeInfo.getLabelLastElement()) {
                return index;
            }
            for(Integer edge : edges) {
                //Only cares for the first which is the lowest. If there are more, new contexts will be pushed
                if (edge >= this.treeInfo.getLabelLastElement()) {
                    return edge;
                }
                else {
                    break;
                }
            }
        }
        return retIndex;
    }

    public int processBranches(int index) {
        ArrayList<Integer> edges = tree.getNextEdges(index);
        boolean flag = !edges.isEmpty();
        int retIndex = this.processLabels(edges, index);
        if(retIndex != -1) { return retIndex; }
        while (flag) {
            index = processNode(edges);
            //System.out.println("-----------------------\nIndex:: " + index);
            edges.clear();
            /**Avoid infinite loops caused by for and whiles**/
            if (index == -1) {
                return -1;
            }
            
            if (!this.processedInstructions.get(index)) {
                //Only processes the instruction after the contexts have been updated
                if (this.treeInfo.isLabelReturnsEmpty() || (this.treeInfo.getLabelLastElement() != index)) {
                    try {
                        this.processCode.processInstruction(instructions.get(index), null, index);
                    }
                    catch (Exception e) { /** ...do nothing... **/}
                    this.processedInstructions.set(index, Boolean.TRUE);
                }
            }
            edges = tree.getNextEdges(index);

            if (edges.isEmpty()) {
                flag = false;
                //sinalizes a return
                return -1;
            }

            retIndex = this.processLabels(edges, index);
            if(retIndex != -1) { return retIndex; }

        }
        return -1;
    }

}
