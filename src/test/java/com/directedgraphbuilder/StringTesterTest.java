package com.directedgraphbuilder;

import org.junit.Test;


public class StringTesterTest {

    @Test
    public void basicTest() {
        StringTester st = new StringTester("0Ant456Bat012Cat678Dog234");
        st.contains("Ant", "Bat", "Cat", "Dog");
    }
    
    @Test
    public void failTest() {
        StringTester st = new StringTester("0Ant456Bat012Cat678Dog234");
        try {
            st.contains("Ant", "Bat", "Cow", "Dog");
        } catch (AssertionError e) {
            StringTester ste = new StringTester(e.getMessage());
            ste.contains("Failed to find [Cow]");
            ste.contains( "index [7]");
        }
    }

    @Test
    public void overlapTest() {
        StringTester st = new StringTester("Test");
        st.contains("Test", "est", "st", "t");
        
        try {
           
        } catch (AssertionError e) {
            StringTester ste = new StringTester(e.getMessage());
            ste.contains("Failed to find [Cow]");
            ste.contains( "index [7]");
        }
    }

}
