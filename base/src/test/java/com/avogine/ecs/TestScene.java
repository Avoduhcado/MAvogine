package com.avogine.ecs;

import com.avogine.game.camera.*;
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
	 * @param camera
	 */
	protected TestScene() {
		super(null, new Camera());
	}

	@Override
	public void init(Window window) {
		// Not implemented
	}

	@Override
	public void cleanup() {
		// Not implemented
	}

}
