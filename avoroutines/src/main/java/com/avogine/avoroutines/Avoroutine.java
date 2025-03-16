package com.avogine.avoroutines;

import java.util.*;

/**
 * @param <I>
 * @param <O> 
 *
 */
public class Avoroutine<I, O> extends LinkedList<RoutineStep<I, O>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 *
	 */
	public enum State {
		NEW, RUNNING, SUSPENDED, FINISHED;
	}
	
	private Iterator<RoutineStep<I, O>> iterator;
	private State state;
	
	private RoutineStep<I, O> currentStep;
		
	/**
	 * Suspend execution on a currently running {@link Avoroutine}.
	 * @throws SuspendExecution Used to control flow, do not process the catch.
	 */
	public static void yieldA() throws SuspendExecution {
		throw new SuspendExecution();
	}
	
	/**
	 * 
	 */
	public Avoroutine() {
		this.state = State.NEW;
	}
	
	/**
	 * Launch this routine.
	 * <p>
	 */
	public void launch() {
		// XXX Resetting the state here may not be ideal? Might want to make this whole thing a no-op if we aren't new.
		state = State.NEW;
		iterator = iterator();
	}
	
	/**
	 * Resume the routine if it isn't finished.
	 * <p>
	 * When the routine is new, it will fetch the iterator from the underlying list to loop through each function.
	 * While the routine is running its state will reflect as much (this likely won't be of any use due to this 
	 * being a blocking operation). If the routine throws a {@link SuspendExecution} then the state will be updated
	 * to Suspended. Finally, if the iterator has been exhausted, the state will be switched to Finished and the 
	 * routine will need to be reset before it can be run again.
	 * @param input 
	 */
	public void resume(I input) {
		if (state == State.FINISHED) {
			return;
		}
		state = State.FINISHED;
		
//		Supplier<I> inputSupplier = () -> input;
		
		while (iterator.hasNext()) {
			if (currentStep == null || currentStep.isFinished()) {
				currentStep = iterator.next();
			}
			state = State.RUNNING;
			try {
				// TODO Fetch and store inputs and outputs
				currentStep.execute(input);
			} catch (SuspendExecution ex) {
				state = State.SUSPENDED;
				break;
			}
		}
	}
	
	/**
	 * @return the current state of the routine
	 */
	public State getState() {
		return state;
	}
	
}
