package com.avogine.avoroutines;

/**
 * @param <I> 
 * @param <O> 
 *
 */
public class Delay<I, O> extends RoutineStep<I, O> {

	private Suspendable<Long, Long> function;
	
	private final Long delay;
	private long currentTime;
		
	/**
	 * @param delay the amount of time in milliseconds to delay this step
	 * 
	 */
	public Delay(Long delay) {
		this.delay = delay;
		this.currentTime = 0;
		
		function = deltaTime -> {
			currentTime += deltaTime;
			if (currentTime >= this.delay) {
				finished = true;
			}
			Avoroutine.yieldA();
			return deltaTime;
		};
	}
	
	/**
	 * @param delay
	 * @return
	 */
	public static <I, O> Delay<I, O> delayFor(Long delay) {
		return new Delay<>(delay);
	}
	
	@Override
	public O execute(I input) throws SuspendExecution {
		// TODO What if the input isn't a Long?
		function.apply((Long) input);
		return null;
	}

}
