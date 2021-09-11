package com.avogine.junk;

import com.avogines.avoroutines.*;
import com.avogines.avoroutines.Avoroutine.*;

/**
 *
 */
public class TestJunk {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		var avo = getDummyRoutine("Hello");
		
		var running = true;
		var toAdd = true;
		
		long deltaTime = 16;
		
		while (running) {
			if (toAdd) {
				AvoroutineCache.get().launchRoutine(avo);
				toAdd = false;
			}
			AvoroutineCache.get().process(deltaTime);
			System.out.println("loop");
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if (avo.getState() == State.FINISHED) {
				running = false;
			}
		}
	}
	
	public static Avoroutine<Long, String> getDummyRoutine(String greeting) {
		var avo = new Avoroutine<Long, String>();
		avo.add(RunExecute.apply(() -> System.out.println(greeting)));
		avo.add(Delay.delayFor(32L));
		avo.add(Collect.collect(System.out::println));
		return avo;
	}

}
