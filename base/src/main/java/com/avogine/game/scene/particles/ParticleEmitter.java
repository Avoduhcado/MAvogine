package com.avogine.game.scene.particles;

/**
 *
 */
public interface ParticleEmitter {

	/**
	 * 
	 */
	public default void init() {
		
	}
	
	/**
	 * 
	 */
	public void cleanup();
	
	/**
	 * @param delta
	 */
	public void update(float delta);
	
	/**
	 * @param count
	 */
	public void emitParticles(int count);
	
}
