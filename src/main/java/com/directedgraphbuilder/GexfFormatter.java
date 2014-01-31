package com.directedgraphbuilder;

import java.io.IOException;
import java.text.MessageFormat;

import com.directedgraphbuilder.Graph.Node;

public class GexfFormatter extends Formatter {

	private static final String OPEN_GRAPH = 
	"<?xml version='1.0' encoding='UTF-8'?>\n" +
	"<gexf xmlns='http://www.gexf.net/1.2draft' version='1.2'>\n" +
//	"    <meta lastmodifieddate='2009-03-20'>\n" +
//	"        <creator>Gexf.net</creator>\n" +
//	"        <description>A hello world! file</description>\n" +
//	"    </meta>\n" +
	"    <graph mode='static' defaultedgetype='directed'>\n" +
    "        <nodes>\n";
//	"            <node id='0' label='Hello' />\n"
//	"            <node id='1' label='Word' />\n" +
//	"        </nodes>\n" +
//	"        <edges>\n" +
//	"            " +
//	"        </edges>\n" +
//	"    </graph>\n" +
//	"</gexf>";
	
	private static final String NODE = "  <node id={0} label={1} />\n";
	private static final String END_NODES_START_EDGES = " </nodes><edges>\n";
	private static final String EDGE = "  <edge id={0} source={1} target={2} />\n";
	private static final String CLOSE_GRAPH = "</edges></graph></gexf>";

    private Graph graph; 
    
    public GexfFormatter(Graph graph) { this.graph = graph; }
    
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
            sb.append(MessageFormat.format(NODE, q(node.getId()), q(node.getName())));
        }
        sb.append(END_NODES_START_EDGES);

        //Render edges
        int e = 0;
        for (Node node: graph.getNodes().values()) {
            for (String targetEdge : node.getEdges()) {
                sb.append(MessageFormat.format(EDGE, q(e++), q(node.getId()), q(targetEdge)));
            }
        }

    	sb.append(CLOSE_GRAPH);
    }
    
}
