package com.directedgraphbuilder;

import static org.junit.Assert.*;

import java.awt.Color;
import java.io.File;

import org.junit.Test;


public class GexfFormatterTest {

    @Test
    public void basicGraphTest() {
        Graph g = new Graph();

        g.addNode("NodeId1", "Node Name 1");
        g.addNode("NodeId2", "Node Name 2");
        g.addNode("NodeId3", "Node Name 3");
        g.addNode("NodeId4", "Node Name 4");
                
        g.addEdge("NodeId1", "NodeId2");
        g.addEdge("NodeId2", "NodeId3");
        g.addEdge("NodeId3", "NodeId4");

        String output = (new GexfFormatter(g)).format();

        System.out.println(output);
        
        StringTester st = new StringTester(output);
        st.contains("<node id=\"NodeId1\" label=\"Node Name 1\" />")
        	.contains("<node id=\"NodeId2\" label=\"Node Name 2\" />")
        	.contains("<node id=\"NodeId3\" label=\"Node Name 3\" />")
        	.contains("<node id=\"NodeId4\" label=\"Node Name 4\" />")
                .contains("<edge id=\"0\" source=\"NodeId1\" target=\"NodeId2\" />")
                .contains("<edge id=\"1\" source=\"NodeId2\" target=\"NodeId3\" />")
                .contains("<edge id=\"2\" source=\"NodeId3\" target=\"NodeId4\" />");
    }

    @Test
    public void saveTest() {
        Graph g = new Graph();

        g.setTopDown();
        g.addNode("NodeId1", "Node Name 1").addField("Field 1").addField("Field 2");
        g.addNode("NodeId2", "Node Name 2").addField("Field A");
        g.addNode("NodeId3", "Node Name 3").addField("Field I").setColor(Color.BLUE);
        g.addNode("NodeId4", "Node Name 4").addEdge("NodeId1").addEdge("NodeId5").addEdge("NodeId6");
        g.addEdge("NodeId1", "NodeId2").addEdge("NodeId3").addEdge("NodeId4");

        File file = (new GexfFormatter(g)).save("graph.gexf");
        assertTrue("file.canRead", file.canRead());
        //file.deleteOnExit();
    }
    
    @Test
    public void simpleSaveTest() {
        Graph g = new Graph();

        g.addNode("NodeId1", "Node Name 1").addEdge("NodeId2");
        File file = (new GexfFormatter(g)).save("edgeBugTest.gexf");
        assertTrue("file.canRead", file.canRead());
        file.deleteOnExit();
    }
        
}
