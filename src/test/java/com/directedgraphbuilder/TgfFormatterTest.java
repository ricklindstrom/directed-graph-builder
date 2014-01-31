package com.directedgraphbuilder;

import static org.junit.Assert.*;

import java.awt.Color;
import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TgfFormatterTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

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

        String output = (new TgfFormatter(g)).format();

        System.out.println(output);
        
        StringTester st = new StringTester(output);
        st.contains("1 Node Name 1")
                .contains("2 Node Name 2")
                .contains("3 Node Name 3")
                .contains("4 Node Name 4")
                .contains("#")
                .contains("1 2")
                .contains("2 3")
                .contains("3 4");
        
    }

    
    @Test
    public void basicNodeTest() {
        Graph g = new Graph();        
        g.addNode("NodeId1", "Node Name 1");

        String output = (new TgfFormatter(g)).format();
        
        StringTester st = new StringTester(output);
        st.contains("1 Node Name 1");

    }
    
    @Test
    public void basicEdgeTest() {
        Graph g = new Graph();        
        g.addEdge("source", "target");

        String output = (new TgfFormatter(g)).format();

        StringTester st = new StringTester(output);
        st.contains("1 source").contains("2 target").contains("#").contains("1 2");
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

        File file = (new TgfFormatter(g)).save("graph.tgf");
        assertTrue("file.canRead", file.canRead());
        //file.deleteOnExit();
    }
    
    @Test
    public void simpleSaveTest() {
        Graph g = new Graph();

        g.addNode("Node Id 1", "Node Name 1").addEdge("Node Id 2");
        System.out.println(new TgfFormatter(g).format());
        //File file = (new TgfFormatter(g)).save("edgeBugTest.tgf");
        //assertTrue("file.canRead", file.canRead());
        //file.deleteOnExit();

        assertEquals("", 1, g.getNode("Node Id 1").getEdges().size());
    }
        
}
