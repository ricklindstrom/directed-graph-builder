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
            int r = 0xff - (Math.abs(1 + value.hashCode()) % 0xce);
            int g = 0xff - (Math.abs(1 + value.hashCode()) % 0xdd);
            int b = 0xff - (Math.abs(1 + value.hashCode()) % 0xec);
            return new Color(r, g, b);
        }
    }        


    /**
     * Converts a Color to a hex color string. For example Color.GREEN will return "#00FF00".
     * @see java.awt.Color#decode(String) which does the opposite
     * @param color Color to encode
     * @return String that represents an opaque color as a 24-bit integer. i.e. a hex Color string in the format #rrggbb. 
     */
    public static String encodeColor(Color color) {
        if (color == null) {
            //If no color, then return a middle-of-the-road gray.
            return "#808080";
        }
        return "#" + String.format("%06x", color.getRGB() & 0xffffff);
        
    }

}
