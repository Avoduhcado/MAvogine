package com.avogine.concurrent;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Joiner;
import java.util.function.*;

import com.avogine.logging.AvoLog;

/**
 * @param <T> 
 * @param loopCondition 
 * @param testCase 
 * @param routine 
 *
 */
public record Avoroutine3<T>(Predicate<T> loopCondition, T testCase, Consumer<Avoscope> routine) {
	private static final ScopedValue<Long> FRAME_TIME_INTERVAL = ScopedValue.newInstance();
	
	private static final StableValue<Avocontext> DEFAULT_CONTEXT = StableValue.of(new Avocontext(1_000_000_000L / 144));
	
	/**
	 * @param scope
	 */
	public void run(Avoscope scope) {
		long currentTime = System.nanoTime();
		long accumulator = 0L;
		
		while (loopCondition.test(testCase)) {
			long startTime = System.nanoTime();
			long frameTime = startTime - currentTime;
			currentTime = startTime;
			accumulator += frameTime;
			
			while (accumulator >= FRAME_TIME_INTERVAL.get()) {
				routine.accept(scope);
				accumulator -= FRAME_TIME_INTERVAL.get();
			}
			
			long totalFrameTime = System.nanoTime() - startTime;
			try {
				Thread.sleep(Duration.ofNanos(FRAME_TIME_INTERVAL.get() - totalFrameTime));
			} catch (InterruptedException _) {
				Thread.currentThread().interrupt();
			}
		}
	}

	/**
	 * @param context
	 * @param routine
	 */
	public static void run2(Avocontext context, Consumer<Avoscope> routine) {
		ScopedValue.where(FRAME_TIME_INTERVAL, context.frameTimeInterval).run(() -> {
			try (var scope = StructuredTaskScope.open(
					Joiner.awaitAll(),
					config -> config.withThreadFactory(Thread.ofVirtual().name("a-", 0).factory()))) {
				new Avoroutine3<Boolean>(Boolean::booleanValue, true, routine).run(new Avoscope(scope, context));
				
				scope.join();
			} catch (InterruptedException _) {
				Thread.currentThread().interrupt();
			}
		});
	}
	
	/**
	 * @param routine
	 */
	public static void run2(Consumer<Avoscope> routine) {
		run2(DEFAULT_CONTEXT.orElseThrow(), routine);
	}
	
	/**
	 *
	 * @param scope
	 * @param context
	 */
	public static record Avoscope(StructuredTaskScope<?, Void> scope, Avocontext context) {
		/**
		 * @param scope
		 */
		public Avoscope(StructuredTaskScope<?, Void> scope) {
			this(scope, null);
		}
		
		/**
		 * @param context
		 * @param routine
		 */
		public void launch(Avocontext context, Avoroutine3<?> routine) {
			AvoLog.log().debug("Launching new routine");
			scope.fork(() -> ScopedValue
					.where(FRAME_TIME_INTERVAL, context.frameTimeInterval())
					.run(() -> routine.run(this)));
		}
		
		/**
		 * @param routine
		 */
		public void launch(Avoroutine3<?> routine) {
			launch(context, routine);
//			scope.fork(() -> routine.run(this));
		}
	}
	
	/**
	 *
	 * @param frameTimeInterval
	 */
	public static record Avocontext(long frameTimeInterval) {
		/**
		 * 
		 */
		public Avocontext {
			Objects.requireNonNull(frameTimeInterval);
		}
	}

}
