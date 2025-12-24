package com.avogine.concurrent;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import com.avogine.concurrent.Avoroutine3.Avocontext;
import com.avogine.logging.AvoLog;

/**
 *
 */
public class CoolSuspendDemo {

	private static Avoroutine3<AtomicInteger> loopingCounter(int loopLimit) {
		final var counter = new AtomicInteger();
		return new Avoroutine3<>(c -> c.get() < loopLimit, counter, _ -> AvoLog.log().info("Inner count {}", counter.incrementAndGet()));
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final AtomicInteger count = new AtomicInteger(0);
		
		Avoroutine3.run2(new Avocontext(Duration.ofSeconds(1).toNanos()), scope -> {
			AvoLog.log().info("{} second", count.incrementAndGet());
			if (count.get() % 3 == 0) {
				scope.launch(loopingCounter(3));
			}
			if (count.get() % 5 == 0) {
				scope.launch(new Avocontext(Duration.ofSeconds(2).toNanos()), loopingCounter(4));
			}
		});
	}

}
