package com.avogine.concurrent;

import java.time.Duration;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Joiner;
import java.util.concurrent.atomic.AtomicInteger;

import com.avogine.logging.AvoLog;

/**
 *
 */
public class ManyThreads {
	void main() {
		try (var scope = StructuredTaskScope.open(Joiner.awaitAll())) {
			var counter = new AtomicInteger();
			int frames = 0;
			boolean running = true;
		
			AvoLog.log().info("Started");
			for (int i = 0; i < 1000; i++) {
				scope.fork(() -> {
					try {
						Thread.sleep(Duration.ofSeconds(2));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (counter.incrementAndGet() % 100 == 0) {
						AvoLog.log().info("Batch");
					}
				});
			}
			
			while (running) {
				AvoLog.log().info("Frame loop: " + frames);
				
				Thread.sleep(Duration.ofSeconds(1));
				frames++;
				
				if (frames >= 5) {
					running = false;
				}
			}
			
			scope.join();
			AvoLog.log().info("Finished");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

