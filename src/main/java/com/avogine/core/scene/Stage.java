package com.avogine.core.scene;

import com.avogine.core.system.Play;
import com.avogine.core.system.Theater;

/**
 * A <tt>Stage</tt> object controls most window specific game logic and rendering.
 * @author Dominus
 *
 */
public interface Stage {

	public void init(Theater theater);
	
	public void input(Theater theater);
	
	/**
	 * Trigger any re-occurring frame updates.
	 * <p>
	 * This method is called once per frame during {@link Play#loop()}
	 * @param interval The time passed since the previous update
	 */
	public void update(float interval);
	
	/**
	 * Initiate the rendering of the entire stage.
	 */
	public void render();

	public void cleanup();

}
