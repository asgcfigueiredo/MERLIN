package codeSliceExtractor.cfg.graphTransformation;

import com.paypal.digraph.parser.GraphEdge;
import com.paypal.digraph.parser.GraphNode;
import com.paypal.digraph.parser.GraphParser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * Transforms the graph into json format
 **/
public class Digraph {
    private GraphParser parser;

    public Digraph(String filename) {
        try {
            this.parser = new GraphParser(new FileInputStream(filename));
        } catch (FileNotFoundException e) {
            this.parser = null;
        }
    }

    public Map<String, GraphNode> getNodes() {
        return this.parser.getNodes();
    }

    public ArrayList<String> getListNodes() {
        int size = this.parser.getNodes().size();
        ArrayList<String> listNodes = new ArrayList<String>(Collections.nCopies(size, "ola"));
        String number;
        int index;
        for(GraphNode node : this.getNodes().values()) {
            number = node.getId().replace("\"", "");
            index = Integer.parseInt(number);
            listNodes.set(index, (String)node.getAttribute("label"));
        }
        return listNodes;
    }

    public Map<String, GraphEdge> getEdges() {
        return this.parser.getEdges();
    }

    public int getSizeNodes() {
        return this.getNodes().size();
    }
}
