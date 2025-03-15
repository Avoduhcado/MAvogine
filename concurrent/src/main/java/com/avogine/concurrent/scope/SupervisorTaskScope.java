package com.avogine.concurrent.scope;

import java.util.concurrent.StructuredTaskScope;

/**
 *
 */
public class SupervisorTaskScope extends StructuredTaskScope<Void> {

	/**
	 * 
	 */
	public SupervisorTaskScope() {
		super();
	}
	
	public SupervisorTaskScope(String name) {
		super(name, Thread.ofVirtual().factory());
	}
	
	/**
	 * Fork a new background Subtask that produces no result.
	 * @param task the {@link Runnable} task to perform.
	 */
	public void launch(Runnable task) {
		super.fork(() -> {
			task.run();
			return null;
		});
	}
}
