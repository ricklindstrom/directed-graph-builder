package com.directedgraphbuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class QueueTest {

	@Test
	public void test() throws Exception {


		ExecutorService x = Executors.newSingleThreadExecutor();
		
		for (int i = 0; i < 10; i++ ){
			final int ii = i;
			x.execute(
				new Runnable() {
					public void run() {
						try {
							Thread.sleep((long) (Math.random() * 100));
						} catch (InterruptedException e) {}
				    	System.out.println("Hello " + ii);							
					}
				}
			);
		}
		x.shutdown();
		System.out.println("OK");
		x.awaitTermination(10, TimeUnit.SECONDS);
		System.out.println("Terminated");
	}

}
