package com.directedgraphbuilder;

import java.util.Arrays;

import org.junit.Assert;

public class StringTester {
    
    String text = null;
    public StringTester(String text) {
        this.text = text;
    }

    
    public StringTester contains(String... items) {
        
        int fromIndex = 0;
        int i = 0;
        for (String item : items) {
            i = text.indexOf(item, fromIndex);
            if (i < 0) {
                String sequence = Arrays.toString(items);
                String msg = String.format("Failed to find [%s] of sequence [%s] from index [%s] in [%s]", 
                        item, sequence, fromIndex, text);
                Assert.fail(msg);
            }
            fromIndex = i;
        }
        return this;
    }
    
}
