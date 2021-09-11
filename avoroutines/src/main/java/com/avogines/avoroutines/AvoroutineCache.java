package com.avogines.avoroutines;

import java.util.*;

import com.avogines.avoroutines.Avoroutine.*;

/**
 * This is currently designed to only store time updated {@link Avoroutine}s with no concern for what their output is.
 */
public class AvoroutineCache {

	private LinkedList<Avoroutine<Long, ?>> routines = new LinkedList<>();
	
	private static AvoroutineCache cache;
	
	/**
	 * @return a singleton {@link AvoroutineCache}
	 */
	public static AvoroutineCache get() {
		if (cache == null) {
			cache = new AvoroutineCache();
		}
		return cache;
	}
	
	/**
	 * @param routine
	 */
	public void launchRoutine(Avoroutine<Long, ?> routine) {
		if (routines.add(routine)) {
			routines.getLast().launch();
		}
	}
	
	/**
	 * @return
	 */
	public LinkedList<Avoroutine<Long, ?>> getRoutines() {
		return routines;
	}
	
	/**
	 * TODO Pass in deltaTime
	 * @param input 
	 */
	public void process(Long input) {
		routines.forEach(routine -> routine.resume(input));
		routines.removeIf(routine -> routine.getState() == State.FINISHED);
	}
	
}
