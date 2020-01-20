package com.avogine.core.scene;

import com.avogine.core.entity.Renderable;
import com.avogine.core.system.Play;
import com.avogine.core.system.Window;

/**
 * A <tt>Stage</tt> object controls most window specific game logic and rendering.
 * @author Dominus
 *
 */
public interface Stage {

	public void init(Window window);
	
	public void input(Window window);
	
	/**
	 * Trigger any re-occurring frame updates.
	 * <p>
	 * This method is called once per frame during {@link Play#loop()}
	 * @param interval The time passed since the previous update
	 */
	public void update(float interval, Window window);
	
	/**
	 * Initiate the rendering of the entire stage.
	 */
	public void render(Window window);
	
	public Scene<? extends Renderable> getScene();

	public void cleanup();

}
