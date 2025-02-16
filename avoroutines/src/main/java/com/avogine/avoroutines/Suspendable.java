package com.avogine.avoroutines;

/**
 *
 */
@FunctionalInterface
public interface Suspendable<I, O> {

	/**
	 * @param input
	 * @return
	 * @throws SuspendExecution
	 */
	public O apply(I input) throws SuspendExecution;
	
}
