package com.avogine.game;

import com.avogine.entity.Renderable;
import com.avogine.experimental.annotation.InDev;
import com.avogine.game.scene.Scene;
import com.avogine.io.Window;

/**
 * A {@code Stage} object controls most window specific game logic and rendering.
 * @author Dominus
 *
 * XXX Should be removed
 */
@InDev
public interface Stage {

	/**
	 * @param window
	 */
	public void init(Window window);
	
	/**
	 * @param window
	 */
	public void input(Window window);
	
	/**
	 * Trigger any recurring frame updates.
	 * <p>
	 * This method is called once per frame during {@code Play.loop()}
	 * @param interval The time passed since the previous update
	 * @param window 
	 */
	public void update(float interval, Window window);
	
	/**
	 * Initiate the rendering of the entire stage.
	 * @param window 
	 */
	public void render(Window window);
	
	/**
	 * @return the currently active {@link Scene} being presented on this {@code Stage}.
	 */
	public Scene<? extends Renderable> getScene();

	/**
	 * 
	 */
	public void cleanup();

}
