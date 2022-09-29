package com.avogine.ecs;

import org.joml.*;

import com.avogine.game.*;
import com.avogine.game.scene.*;
import com.avogine.io.*;

/**
 *
 */
public class TestScene extends ECSScene {

	/**
	 * @param projection
	 */
	protected TestScene() {
		super(null);
	}

	@Override
	public void init(Game game, Window window) {
		// Not implemented
	}

	@Override
	public Matrix4f getView() {
		return null;
	}
	
}
