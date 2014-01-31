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
        StringTester tester = new StringTester(g.toString());
        tester.contains(
    		"start\" -> \"org_eclipse_jdt_internal_junit_runner_RemoteTestRunner",
    		"org_eclipse_jdt_internal_junit_runner_TestExecution\" -> \"org_eclipse_jdt_internal_junit4_runner_JUnit4TestReference",
    		"org_junit_internal_runners_JUnit4ClassRunner\" -> \"org_junit_internal_runners_ClassRoadie",
    		"org_eclipse_jdt_internal_junit_runner_RemoteTestRunner\" -> \"org_eclipse_jdt_internal_junit_runner_TestExecution"
        );
    }
    
    @Test
    public void twoCallTest() {

        CallGraphBuilder.EXCLUSIONS = CallGraphBuilder.EXCLUSIONS.replace("CallGraphBuilder", "XCallGraphBuilderX");

        try {
        	CallGraphBuilder.clear();
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
