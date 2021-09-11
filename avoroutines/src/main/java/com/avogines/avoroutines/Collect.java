package com.avogines.avoroutines;

/**
 *
 */
public class Collect<I, O> extends RoutineStep<I, O> {

	private Suspendable<I, O> function;
	
	/**
	 * @param consumer
	 */
	public Collect(SusConsumer<I> consumer) {
		function = v -> {
			consumer.consume(v);
			finished = true;
			Avoroutine.yieldA();
			return null;
		};
	}
	
	/**
	 * @param consumer
	 * @param input 
	 */
	public Collect(SusConsumer<I> consumer, I input) {
		function = v -> {
			consumer.consume(input);
			finished = true;
			Avoroutine.yieldA();
			return null;
		};
	}
	
	/**
	 * @param <T>
	 * @param <U>
	 * @param consumer
	 * @return
	 */
	public static <T, U> Collect<T, U> collect(SusConsumer<T> consumer) {
		return new Collect<>(consumer);
	}
	
	/**
	 * @param <T>
	 * @param <U>
	 * @param consumer
	 * @param input
	 * @return
	 */
	public static <T, U> Collect<T, U> collect(SusConsumer<T> consumer, T input) {
		return new Collect<>(consumer, input);
	}
	
	@Override
	public O execute(I input) throws SuspendExecution {
		return function.apply(input);
	}

}
