package com.avogine.avoroutines;

/**
 * @param <I> Function input type
 * @param <O> Function output type
 *
 */
public class RunExecute<I, O> extends RoutineStep<I, O> {

	private Suspendable<I, O> function;
	
	/**
	 * @param runnable 
	 * 
	 */
	public RunExecute(SusRunnable runnable) {
		function = v -> {
			runnable.run();
			finished = true;
			Avoroutine.yieldA();
			return null;
		};
	}
	
	/**
	 * @param <T>
	 * @param <U>
	 * @param runnable 
	 * @return
	 */
	public static <T, U> RunExecute<T, U> apply(SusRunnable runnable) {
		return new RunExecute<>(runnable);
	}
	
	@Override
	public O execute(I input) throws SuspendExecution {
		return function.apply(input);
	}

}
