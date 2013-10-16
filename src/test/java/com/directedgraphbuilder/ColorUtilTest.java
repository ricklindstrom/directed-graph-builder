package com.directedgraphbuilder;

import static org.junit.Assert.*;

import java.awt.Color;

import org.junit.Test;

public class ColorUtilTest {

    @Test
    public void testHashColor() {
        assertNotNull("hashColor", ColorUtil.hashColor("Hello"));
        assertEquals("consistent input - consistent output", ColorUtil.hashColor("Hello"), ColorUtil.hashColor("Hello"));
        assertNotSame("different input - different output", ColorUtil.hashColor("Hello"), ColorUtil.hashColor("Goodbye"));
    }

    @Test
    public void testEncodeColor() {
        assertEquals("white", "#ffffff", ColorUtil.encodeColor(Color.WHITE));
        assertEquals("red", "#ff0000", ColorUtil.encodeColor(Color.RED));
        assertEquals("green", "#00ff00", ColorUtil.encodeColor(Color.GREEN));
        assertEquals("blue", "#0000ff", ColorUtil.encodeColor(Color.BLUE));
        assertEquals("black", "#000000", ColorUtil.encodeColor(Color.BLACK));
    }

}
