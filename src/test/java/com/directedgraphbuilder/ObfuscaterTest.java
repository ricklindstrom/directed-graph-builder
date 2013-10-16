package com.directedgraphbuilder;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Test;

public class ObfuscaterTest {

	@Test
	public void testObfuscate() {

		assertEquals("Repeatable behavoir: One",
				Obfuscater.obfuscate("One"), 
				Obfuscater.obfuscate("One"));

		assertEquals("Repeatable behavoir: OneTwoThree",
				Obfuscater.obfuscate("OneTwoThree"), 
				Obfuscater.obfuscate("OneTwoThree"));

		assertEquals("Composable behavoir OneTwo", Obfuscater.obfuscate("OneTwo"), 
				Obfuscater.obfuscate("One") + Obfuscater.obfuscate("Two"));

		assertEquals("Composable behavoir OneTwoThreeFour", Obfuscater.obfuscate("OneTwoThreeFour"), 
				Obfuscater.obfuscate("One") + Obfuscater.obfuscate("Two") +
				Obfuscater.obfuscate("Three") + Obfuscater.obfuscate("Four"));
	}

	@Test
	public void testRandom() {
		String s = Obfuscater.random(8, new Random());
		assertEquals(8, s.length());
	}

}
