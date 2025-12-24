package com.avogine.concurrent;

import java.time.Duration;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Joiner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 *
 */
public class Routine {

	private static final AtomicInteger UNNAMED_SCOPE = new AtomicInteger(1);
	
	private static final ScopedValue<String> AVOROUTINE_NAME = ScopedValue.newInstance();
	
	private static final ScopedValue<Duration> AVOROUTINE_SLEEP = ScopedValue.newInstance();
	
	protected static record Avoroutine(StructuredTaskScope<?, Void> scope) {
		
		public void launch(String name, Consumer<Avoroutine> block) {
			scope.fork(() -> {
				if (name == null) {
					block.accept(this);
				} else {
					ScopedValue.where(AVOROUTINE_NAME, name).run(() -> block.accept(this));
				}
			});
		}
		
		public void launch(Consumer<Avoroutine> block) {
			launch(null, block);
		}
		
		public String getName() {
			return AVOROUTINE_NAME.get();
		}
		
		public void sleep(Duration currentFrame) {
			try {
				Thread.sleep(AVOROUTINE_SLEEP.get().minus(currentFrame));
			} catch (InterruptedException _) {
				Thread.currentThread().interrupt();
			}
		}
		
	}
	
	public record Avocontext(String name, Duration sleepIntervalPerFrame) {
		
	}
	
	public static void runBlocking(Avocontext context, Consumer<Avoroutine> block) {
		ScopedValue
		.where(AVOROUTINE_NAME, context.name == null ? AVOROUTINE_NAME.get() : context.name)
		.where(AVOROUTINE_SLEEP, context.sleepIntervalPerFrame == null ? AVOROUTINE_SLEEP.get() : context.sleepIntervalPerFrame)
		.run(() -> {
			try (var scope = StructuredTaskScope.open(
					Joiner.awaitAll(),
					config -> config.withThreadFactory(Thread.ofVirtual().name("a-", 0).factory()))) {
				block.accept(new Avoroutine(scope));
				
				scope.join();
			} catch (InterruptedException _) {
				Thread.currentThread().interrupt();
			}
		});
	}
	
	/**
	 * @param name
	 * @param block
	 */
	public static void runBlocking(String name, Consumer<Avoroutine> block) {
		ScopedValue.where(AVOROUTINE_NAME, name).run(() -> {
			try (var scope = StructuredTaskScope.open(
					Joiner.awaitAll(),
					config -> config.withThreadFactory(Thread.ofVirtual().name("a-", 0).factory()))) {
				block.accept(new Avoroutine(scope));
				
				scope.join();
			} catch (InterruptedException _) {
				Thread.currentThread().interrupt();
			}
		});
	}
	
	/**
	 * @param block
	 */
	public static void runBlocking(Consumer<Avoroutine> block) {
		runBlocking("Avoroutine-" + UNNAMED_SCOPE.getAndAdd(1), block);
	}
	
}
