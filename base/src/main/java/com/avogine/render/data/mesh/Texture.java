package com.avogine.render.data.mesh;

/**
 *
 */
public class Texture {

	private final int id;
	private TextureType type;
	
	/**
	 * @param id 
	 * 
	 */
	public Texture(int id) {
		this.id = id;
	}
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * @return the type
	 */
	public TextureType getType() {
		return type;
	}
	
	/**
	 * @param type the type to set
	 */
	public void setType(TextureType type) {
		this.type = type;
	}
	
	/**
	 *
	 */
	public enum TextureType {
		/**
		 * 
		 */
		DIFFUSE,
		/**
		 * 
		 */
		SPECULAR
	}
	
}
