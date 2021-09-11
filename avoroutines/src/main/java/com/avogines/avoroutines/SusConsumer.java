package com.avogines.avoroutines;

/**
 * @param <I> 
 *
 */
@FunctionalInterface
public interface SusConsumer<I> {

	/**
	 * @param input
	 */
	public void consume(I input) throws SuspendExecution;
	
}
