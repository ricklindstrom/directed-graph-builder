package com.directedgraphbuilder;

import static org.junit.Assert.*;

import org.junit.Test;

public class CallGraphBuilderTest {

    @Test
    public void basicTest() {
        CallGraphBuilder b = new CallGraphBuilder();
        
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        
        b.add(stackTrace);
        
        Graph g = b.buildGraph();
        
        String result = g.toString();
        
        StringTester tester = new StringTester(result);
        System.out.println(result);
    }
    
    @Test
    public void twoCallTest() {

        CallGraphBuilder.EXCLUSIONS = CallGraphBuilder.EXCLUSIONS.replace("CallGraphBuilder", "XCallGraphBuilderX");

        try {
            CallGraphBuilder.start();

            new TestClass1().method();
            new TestClass2().method();
            
            Graph g = CallGraphBuilder.stop();
            g.save("twoCallTest.dot");
            String result = g.toString();
            assertTrue(result.contains("TestClass1"));
            assertTrue(result.contains("TestClass2"));

        } finally {
            CallGraphBuilder.EXCLUSIONS = CallGraphBuilder.EXCLUSIONS.replace("XCallGraphBuilderX", "CallGraphBuilder");            
        }

    }
    
    @Test
    public void testAddingDuplicateStackTraceHasNoEffect() {
        CallGraphBuilder b = new CallGraphBuilder();
        b.add(Thread.currentThread().getStackTrace());
        String before = b.buildGraph().toString();
        b.add(Thread.currentThread().getStackTrace());
        String after = b.buildGraph().toString();
        
        assertEquals("before and after", before, after);
    }
    
    
    private class TestClass1 {        
        public void method() {
            (new Destination()).destinationMethod();
        }
    }
    private class TestClass2 {
        public void method() {
            (new Destination()).destinationMethod();
        }
    }

    
    private class Destination {
        public void destinationMethod() {
            CallGraphBuilder.capture();
        }
    }
}
