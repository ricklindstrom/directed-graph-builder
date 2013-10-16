package com.directedgraphbuilder;

import java.util.Random;

public class Obfuscater {
    
	/**
	 * Creates a random string of the same length and title case of the input string.
	 * Each given word should return the same word output.
	 * @param s
	 * @return
	 */
	public static String obfuscate(String s) {
		StringBuilder sb = new StringBuilder();

		String[] pieces = s.split("[A-Z]");

		for (String piece : pieces) {
			String replacement = random(piece.length() + 1, new Random(piece.hashCode()));
			if (replacement != null && replacement.length() > 1) { 
				//First letter in uppercase
				sb.append(replacement.substring(0, 1).toUpperCase());
				//Other letters lowercase
				sb.append(replacement.substring(1).toLowerCase());
			}
		}

		return sb.toString();
	}
	
	public static String rot13(String s) {
		StringBuilder sb = new StringBuilder();
		for(char c : s.toCharArray()) {
            char cr;
            if      ((c >= 'a' && c <= 'm') || (c >= 'A' && c <= 'M')) cr = (char) (c + 13);
            else if ((c >= 'n' && c <= 'z') || (c >= 'N' && c <= 'Z')) cr = (char) (c - 13);
            else cr = c;
            sb = sb.append(cr);
		}
		return sb.toString();
	}
	
	 public static String random(int count, Random random) {
	
		 int start = 0;
		 int end = 0;
		 boolean letters = true;
		 boolean numbers = false;
		 char[] chars = null;

		 if (count == 0) {
			 return "";
		 }

	     if ((start == 0) && (end == 0)) {
	         end = 'z' + 1;
	         start = ' ';
	     }

	     StringBuilder buffer = new StringBuilder();
	     int gap = end - start;
	     while (count-- != 0) {
	         char ch;
	         if (chars == null) {
	             ch = (char) (random.nextInt(gap) + start);
	         } else {
	             ch = chars[random.nextInt(gap) + start];
	         }
	         if ((letters && numbers && Character.isLetterOrDigit(ch))
	             || (letters && Character.isLetter(ch))
	             || (numbers && Character.isDigit(ch))
	             || (!letters && !numbers)) {
	             buffer.append(ch);
	         } else {
	             count++;
	         }
	     }
	     return buffer.toString();
	 }

}
