package com.directedgraphbuilder;

import java.io.IOException;
import java.text.MessageFormat;

import com.directedgraphbuilder.Graph.Node;

public class GMLFormatter extends Formatter {

	private static final String OPEN_GRAPH = "graph \n[\n";
	private static final String NODE = "node \n  [\n  id {0}\n  label \"{1}\"\n  ]\n";
	private static final String EDGE = "edge \n  [\n  source {0}\n  target {1} \n  label \"{2}\"\n]\n";
	private static final String CLOSE_GRAPH = "]";

    private Graph graph; 
    
    public GMLFormatter(Graph graph) { this.graph = graph; }
    
    @Override
    public String format() {
        StringBuilder sb = new StringBuilder();
        try {
            format(sb);
        } catch(IOException ex) {
            System.out.println(ex.getLocalizedMessage());
        }
        return sb.toString();
    }
    
    @Override
    public void format(Appendable sb) throws IOException {
    	sb.append(OPEN_GRAPH);

    	//Render nodes
        for (Node node : graph.getNodes().values()) {
            sb.append(MessageFormat.format(NODE, node.getId(), node.getName()));
        	sb.append(q(node.getId())).append(";\n");

        	//Render edges for node
            for (String targetEdge : node.getEdges()) {
                sb.append(MessageFormat.format(EDGE, node.getId(), targetEdge, ""));
            }
        }
        
    	sb.append(CLOSE_GRAPH);
    }
    
}
