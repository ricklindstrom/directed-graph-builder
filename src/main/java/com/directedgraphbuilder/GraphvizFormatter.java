package com.directedgraphbuilder;

import java.util.List;
import java.util.Map.Entry;

import com.directedgraphbuilder.Graph.Node;

public class GraphvizFormatter extends Formatter {

    private Graph graph; 
    
    public GraphvizFormatter(Graph graph) { this.graph = graph; }
    
    @Override
    public String format() {

        StringBuilder sb = new StringBuilder();
        sb.append("digraph G { \n");
        sb.append("  graph [rankdir=" + graph.getRankdir() + " fontname=Helvetica]; \n");
        sb.append("  node [fontsize=11 shape=record fontname=Helvetica style=filled fillcolor=lightyellow]; \n");
        sb.append("  edge [fontname=Helvetica fontsize=9]; \n");
        sb.append("\n");

        //Render each clustered node inside the cluster that owns it
        for (Entry<String, List<Node>> cluster : graph.getClusters().entrySet()) {
            sb.append("subgraph cluster_" + cluster.getKey() + " { \n");
            sb.append("    [label=\"" + cluster.getKey() + "\"]; \n");
            
            //Render the nodes in the current cluster
            for (Node node : cluster.getValue()) {
                sb.append(q(node.getId())).append(";\n");
            }
            sb.append("}\n\n");
        }

        //Render nodes that don't belong to a cluster
        for (Node node : graph.getNodes().values()) { 
            sb.append(format(node));
        }

        sb.append("}\n");

        return sb.toString();
    }
    
    private String format(Graph.Node node) {
        final StringBuilder sb = new StringBuilder();
        final boolean flipRecord = ("TD".equals(graph.getRankdir()));
        final String name;
        if (flipRecord) {
            name = "{" + node.getName() + "}";
        } else {
            name = node.getName();
        }

        sb.append(q(node.getId())).append(" [label=").append(q(name));
        if (node.getColor() != null) {
            sb.append(" fillcolor=" + q(ColorUtil.encodeColor(node.getColor().brighter())));
        } 
        sb.append("];\n");

        
        for (final String edgeId : node.getEdges()) {
            sb.append("  ").append(q(node.getId())).append(" -> ").append(q(edgeId));
            if (node.getColor() != null) {
                sb.append(" [color=" + q(ColorUtil.encodeColor(node.getColor().darker())) + " ]");
            }
            sb.append(";\n");
        }

        sb.append("\n");
        return sb.toString();
        
    }
    

}
