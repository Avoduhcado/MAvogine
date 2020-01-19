package com.avogine.core.scene;

import com.avogine.core.system.Theater;

/**
 * TODO Should the scene instead be in charge of knowing what objects exist within it? That would cutdown greatly on bloated constructors and passing around things, plus benefits of add/remove from the container itself not
 * trying to update entities while holding different scenes in memory
 * @author Dominus
 *
 */
public abstract class SceneObject {

	// TODO Change this to a Scene instead of theater and source the theater from the scene? That path may be difficult since a scene lives inside a Stage inside a Theater
	protected Theater container;
	
	protected SceneObject(Theater theater) {
		this.container = theater;
	}
	
	public Theater getContainer() {
		return container;
	}
	
	public void setContainer(Theater container) {
		this.container = container;
	}
	
}
