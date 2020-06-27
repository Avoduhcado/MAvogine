package com.avogine.game.scene;

import com.avogine.experimental.annotation.InDev;
import com.avogine.io.Window;

/**
 * TODO Should the scene instead be in charge of knowing what objects exist within it? That would cutdown greatly on bloated constructors and passing around things, plus benefits of add/remove from the container itself not
 * trying to update entities while holding different scenes in memory
 * @author Dominus
 *
 */
@InDev
public abstract class SceneObject {

	// TODO Change this to a Scene instead of window and source the window from the scene? That path may be difficult since a scene lives inside a Stage inside a Theater
	protected Window container;
	
	protected SceneObject(Window window) {
		this.container = window;
	}
	
	public Window getContainer() {
		return container;
	}
	
	public void setContainer(Window container) {
		this.container = container;
	}
	
}
