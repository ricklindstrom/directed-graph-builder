package com.directedgraphbuilder;

import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class GraphTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void basicGraphTest() {
        Graph g = new Graph();

        g.setTopDown();
        g.addNode("NodeId1", "Node Name 1").addField("Field 1").addField("Field 2");
        g.addNode("NodeId2", "Node Name 2").addField("Field A");
        g.addNode("NodeId3", "Node Name 3").addField("Field I").setColor(Color.BLUE);
        g.addNode("NodeId4", "Node Name 4").addEdge("NodeId1").addEdge("NodeId5").addEdge("NodeId6");
        g.addEdge("NodeId1", "NodeId2").addEdge("NodeId3").addEdge("NodeId4");

        String output = g.toString();

        StringTester st = new StringTester(output);
        st.contains("digraph","{")
                .contains("NodeId1","[","label","Node Name 1","|","Field 1","|","Field 2","];")
                .contains("NodeId2")
                .contains("NodeId3", "fillcolor=\"#0000ff\"")
                .contains("}");

        st.contains("NodeId4","->","NodeId1",";");
        st.contains("NodeId1","->","NodeId2",";");
    }

    
    @Test
    public void basicNodeTest() {
        Graph g = new Graph();        
        g.addNode("NodeId1", "Node Name 1").addField("Field 1").addField("Field 2");

        StringTester st = new StringTester(g.toString());
        st.contains("digraph","{","NodeId1","[","label","Node Name 1","|","Field 1","|", "Field 2","];","}");

    }

    @Test
    public void rankdirTest() {
        Graph g1 = new Graph();
        g1.setLeftRight();
        g1.addNode("NodeId1");

        StringTester st1 = new StringTester(g1.toString());
        st1.contains("{","rankdir","LR","NodeId1","}");

        g1.setTopDown();
        StringTester st2 = new StringTester(g1.toString());
        st2.contains("{","rankdir","TD","NodeId1","}");
    }

    
    @Test
    public void basicEdgeTest() {
        Graph g = new Graph();        
        g.addEdge("source", "target");

        StringTester st = new StringTester(g.toString());
        st.contains("digraph","{","source","->","target",";","}");
    }

    @Test
    public void fileSaveTest() {
        Graph g = new Graph();
        g.addNode("NodeId1");
        File file = g.save("filetest.dot");
        assertTrue("file canRead", file.canRead());      
        file.deleteOnExit();
    }

    @Test
    public void clusterFileSaveTest() {
        Graph g = new Graph();
        g.addNode("NodeId1").setCluster("Cluster1");
        g.addNode("NodeId2").setCluster("Cluster2");
        g.addNode("NodeId3").setCluster("Cluster3");
        
        g.addEdge("NodeId1", "NodeId3");
        
        File file = g.save("clusterOnlyTest.dot");
        assertTrue("file canRead", file.canRead());      
        file.deleteOnExit();
    }

    
    @Test
    public void clusterTest() { 
        Graph g = new Graph();        
        g.addNode("NodeId1").setCluster("one");
        g.addNode("NodeId2").setCluster("two");
        g.addEdge("NodeId1", "NodeId2");

        StringTester st = new StringTester(g.toString());
        st.contains("digraph","{","subgraph cluster_one","{","NodeId1","}","}");
        st.contains("digraph","{","subgraph cluster_two","{","NodeId2","}","}");
        st.contains("digraph","{","NodeId1","->","NodeId2", ";","}");

        System.out.println(g.toString());
    }

    @Test
    public void buildClusterGraphTest() {
        Graph g = new Graph();        
        g.addNode("NodeId1").setCluster("one");
        g.addNode("NodeId2").setCluster("two");
        g.addEdge("NodeId1", "NodeId2");
        
        Graph clusterGraph = g.buildClusterGraph();
        
        System.out.println(clusterGraph.toString());
        
    }
}
