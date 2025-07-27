package com.avogine.render.opengl.model.mesh;

import com.avogine.render.opengl.VAO;

/**
 *
 */
public interface Renderable {

	/**
	 * Free memory.
	 */
	public void cleanup();
	
	/**
	 * 
	 */
	public void bind();
	
	/**
	 * Render this VAO.
	 */
	public void draw();
	
	/**
	 * @return the vertexCount
	 */
	public int getVertexCount();
	
	/**
	 * @return the vao
	 */
	public VAO getVAO();
}
