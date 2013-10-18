package com.directedgraphbuilder;

import java.awt.Color;


/**
 * Some color manipulation utilities
 *
 */
public class ColorUtil {

    /**
     * Returns a Color for an object that is determined by the object's hash code.
     * @param value Object
     * @return a Color for an object that is determined by the object's hash code.
     */
    public static Color hashColor(Object value) {
        if (value == null) {
            return Color.WHITE.darker();
        } else {
        	int hash = Math.abs(value.hashCode());
            int r = 0xff - (hash % 0xce);
            int g = 0xff - (hash % 0xdd);
            int b = 0xff - (hash % 0xec);
            Color color = (new Color(r, g, b));
            //Return a brighter color unless the brighter color is white
            return color; // (!Color.WHITE.equals(color.brighter()) ? color.brighter() : color);
        }
    }        


    /**
     * Converts a Color to a hex color string. For example Color.GREEN will return "#00FF00".
     * @see java.awt.Color#decode(String) which does the opposite
     * @param color Color to encode
     * @return A hex Color string in the format #rrggbb. 
     */
    public static String encodeColor(Color color) {
        if (color == null) {
            //If no color, then return a middle-of-the-road gray.
            return "#808080";
        }
        return "#" + String.format("%06x", color.getRGB() & 0xffffff);
        
    }

}
