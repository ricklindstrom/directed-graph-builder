package com.directedgraphbuilder;

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

        int i = 1;
        for (final Node node : graph.getNodes().values()) {
            nodeToId.put(node, i);
            sb.append(i).append(" ").append(node.getSimpleName()).append("\n");
            i++;
        }

        
        for (final Node source : graph.getNodes().values()) {
            for (final String edgeId : source.getEdges()) {
                Node target = graph.getNodes().get(edgeId);
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
                Node target = graph.getNodes().get(edgeId);
                sb.append(nodeToId.get(source));
                sb.append(" ");
                sb.append(nodeToId.get(target));
                sb.append("\n");
            }
        }

        return sb.toString();
    }
}
