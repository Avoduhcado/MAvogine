package com.avogine.ecs;

import org.joml.*;

import com.avogine.game.scene.*;
import com.avogine.io.*;

/**
 *
 */
public class TestScene extends ECSScene {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param projection
	 */
	protected TestScene() {
		super(null);
	}

	@Override
	public void init(Window window) {
		// Not implemented
	}

	@Override
	public void update(float delta) {
		// Not implemented
		
	}

	@Override
	public Matrix4f getView() {
		return null;
	}
	
	@Override
	public void cleanup() {
		// Not implemented
	}

}
