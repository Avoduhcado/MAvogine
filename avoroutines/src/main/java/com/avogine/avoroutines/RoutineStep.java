package com.avogine.avoroutines;

/**
 * @param <I> 
 * @param <O> 
 *
 */
public abstract class RoutineStep<I, O> {

	protected boolean finished;
	
	/**
	 * @param input
	 * @return
	 * @throws SuspendExecution 
	 */
	public abstract O execute(I input) throws SuspendExecution;
	
	/**
	 * @return true if the current step is finished processing
	 */
	public boolean isFinished() {
		return finished;
	}
	
}
