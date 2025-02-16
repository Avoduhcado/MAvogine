package com.avogine.avoroutines;

import java.util.*;

/**
 *
 */
public class AvoIterator<E extends RoutineStep<Long, Boolean>> implements Iterator<E> {

	private E element;
	private boolean hasElement;
	
	@Override
	public boolean hasNext() {
		return hasElement;
	}

	@Override
	public E next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		E result = element;
		hasElement = false;
		element = null;
		
		return result;
	}

}
