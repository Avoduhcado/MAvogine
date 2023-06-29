package com.avogine.ecs.components;

import com.avogine.ecs.EntityComponent;


/**
 *
 */
public class TransformComponent implements EntityComponent {
	
	private float x;
	private float y;
	private float z;
	private float rx;
	private float ry;
	private float rz;
	private float rw;
	private float sx;
	private float sy;
	private float sz;
	
	/**
	 * Translation, orientation, and scale.
	 * @param x 
	 * @param y 
	 * @param z 
	 * @param rx 
	 * @param ry 
	 * @param rz 
	 * @param rw 
	 * @param sx 
	 * @param sy 
	 * @param sz 
	 */
	public TransformComponent(
			float x, float y, float z,
			float rx, float ry, float rz, float rw,
			float sx, float sy, float sz) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.rx = rx;
		this.ry = ry;
		this.rz = rz;
		this.rw = rw;
		this.sx = sx;
		this.sy = sy;
		this.sz = sz;
	}
	
	/**
	 * Default translation, orientation, 1 dimensional scale constructor
	 * @param x
	 * @param y
	 * @param z
	 * @param rx
	 * @param ry
	 * @param rz
	 * @param rw
	 * @param s
	 */
	public TransformComponent(
			float x, float y, float z,
			float rx, float ry, float rz, float rw,
			float s) {
		this(x, y, z, rx, ry, rz, rw, s, s, s);
	}
	
	/**
	 * Default translation, orientation constructor
	 * @param x
	 * @param y
	 * @param z
	 * @param rx
	 * @param ry
	 * @param rz
	 * @param rw
	 */
	public TransformComponent(
			float x, float y, float z,
			float rx, float ry, float rz, float rw) {
		this(x, y, z, rx, ry, rz, rw, 1);
	}
	
	/**
	 * Default translation constructor
	 * @param x
	 * @param y
	 * @param z
	 */
	public TransformComponent(float x, float y, float z) {
		this(x, y, z, 0, 0, 0, 1);
	}
	
	/**
	 * Default no argument constructor
	 */
	public TransformComponent() {
		this(0, 0, 0);
	}
	
	public float x() {
		return x;
	}
	
	public TransformComponent x(float x) {
		this.x = x;
		return this;
	}
	
	public float y() {
		return y;
	}
	
	public TransformComponent y(float y) {
		this.y = y;
		return this;
	}

	public float z() {
		return z;
	}
	
	public TransformComponent z(float z) {
		this.z = z;
		return this;
	}

	public float rx() {
		return rx;
	}
	
	public TransformComponent rx(float rx) {
		this.rx = rx;
		return this;
	}

	public float ry() {
		return ry;
	}
	
	public TransformComponent ry(float ry) {
		this.ry = ry;
		return this;
	}

	public float rz() {
		return rz;
	}
	
	public TransformComponent rz(float rz) {
		this.rz = rz;
		return this;
	}

	public float rw() {
		return rw;
	}
	
	public TransformComponent rw(float rw) {
		this.rw = rw;
		return this;
	}

	public float sx() {
		return sx;
	}
	
	public TransformComponent sx(float sx) {
		this.sx = sx;
		return this;
	}

	public float sy() {
		return sy;
	}
	
	public TransformComponent sy(float sy) {
		this.sy = sy;
		return this;
	}

	public float sz() {
		return sz;
	}
	
	public TransformComponent sz(float sz) {
		this.sz = sz;
		return this;
	}
	
	@Override
	public String toString() {
		return x + " " + y + " " + z + " " + rx + " " + ry + " " + rz +" " + rw;
	}
	
}
