package com.avogine.game.scene;

import org.joml.*;

import com.avogine.ecs.*;
import com.avogine.game.camera.*;

/**
 *
 */
public abstract class ECSScene extends Scene {

	protected final EntityWorld entityWorld;
	
	protected ECSScene(Matrix4f projection, Camera camera) {
		super(projection, camera);
		entityWorld = new EntityWorld();
	}
	
	/**
	 * @return the entityWorld
	 */
	public EntityWorld getEntityWorld() {
		return entityWorld;
	}

}
