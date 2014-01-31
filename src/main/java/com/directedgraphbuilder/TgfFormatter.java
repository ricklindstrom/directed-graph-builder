package com.directedgraphbuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.directedgraphbuilder.Graph.Node;

/**
 * 1 First node
 * 2 Second node
 * #
 * 1 2 Edge between the two
 */
public class TgfFormatter extends Formatter {

    private Graph graph; 
    
    public TgfFormatter(Graph graph) { this.graph = graph; }

    private Map<Graph.Node, Integer> nodeToId = new HashMap<Graph.Node, Integer>();
    
    @Override
    public String format() {
        StringBuilder sb = new StringBuilder();
        try {
            format(sb);
        } catch(Exception ex) {
            System.out.println(ex.getLocalizedMessage());
        }
        return sb.toString();
    }
    
    @Override
    public void format(Appendable sb) throws IOException {

        int i = 1;
        for (final Node node : graph.getNodes().values()) {
            nodeToId.put(node, i);
            sb.append(Integer.toString(i)).append(" ").append(node.getSimpleName()).append("\n");
            i++;
        }
        
        for (final Node source : graph.getNodes().values()) {
            for (final String edgeId : source.getEdges()) {
                Node target = graph.getNode(edgeId);
                if (!nodeToId.containsKey(target)) {
                    nodeToId.put(target, i++);
                }
            }
        }
        
        sb.append("#\n");
        
        for (final Node source : graph.getNodes().values()) {
            //System.out.println("N=" + source.getSimpleName());
            for (final String edgeId : source.getEdges()) {
                //System.out.println("E=" + edgeId);
                Node target = graph.getNode(edgeId);
                sb.append(Integer.toString(nodeToId.get(source)));
                sb.append(" ");
                sb.append(Integer.toString(nodeToId.get(target)));
                sb.append("\n");
            }
        }
    }
}
