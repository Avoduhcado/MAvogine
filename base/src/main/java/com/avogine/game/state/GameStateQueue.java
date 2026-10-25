package com.avogine.game.state;

import java.util.Objects;
import java.util.function.Supplier;

import com.avogine.io.Window;

/**
 * TODO#43 Allow the state queue to "pre-load" assets of the next state without just immediately swapping to promote instances such as knowing a title scene will 
 * progress into a main game scene and can pull a set of assets it should begin loading in the background to speed up state swaps.
 * @param <T>
 */
public class GameStateQueue<T extends GameState<?, ?> & Swappable> {
	private T currentState;
	private Supplier<? extends T> queuedStateSupplier;
	
	/**
	 * @param initialState 
	 */
	public GameStateQueue(T initialState) {
		currentState = Objects.requireNonNull(initialState);
	}
	
	/**
	 * @return
	 */
	public T getCurrentState() {
		return currentState;
	}
	
	/**
	 * @param supplier
	 */
	public void queueState(Supplier<? extends T> supplier) {
		queuedStateSupplier = Objects.requireNonNull(supplier);
	}
	
	/**
	 * @return
	 */
	public boolean hasQueuedState() {
		return queuedStateSupplier != null;
	}
	
	/**
	 * @param window
	 */
	public void swapState(Window window) {
		Objects.requireNonNull(queuedStateSupplier);
		
		currentState.prepareForSwap(window);
		currentState.cleanup();
		currentState = queuedStateSupplier.get();
		queuedStateSupplier = null;
		currentState.init(window);
	}
}
