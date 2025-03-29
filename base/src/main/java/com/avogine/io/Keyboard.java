package com.avogine.io;

import java.util.*;

import org.lwjgl.glfw.GLFW;

/**
 * 
 */
public class Keyboard {

	private final boolean[] keys;
	private final Map<Integer, Boolean> scancodes;
	
	private int mods;
	
	/**
	 * 
	 */
	public Keyboard() {
		keys = new boolean[GLFW.GLFW_KEY_LAST];
		Arrays.fill(keys, false);
		scancodes = new HashMap<>();
	}
	
	/**
	 * Return true if the given keyCode is currently pressed.
	 * 
	 * @param key
	 * @return true if the given keyCode is currently pressed.
	 */
	public boolean isKeyDown(int key) {
		if (key < keys.length) {
			return keys[key];
		} else {
			return scancodes.getOrDefault(key, false);
		}
	}
	
	/**
	 * @param key
	 * @param mods
	 * @return true if the given keyCode is currently pressed with the associated modifier keys.
	 */
	public boolean isKeyDownWithMod(int key, int mods) {
		return isKeyDown(key) && (getMods() & mods) != 0;
	}

	/**
	 * @return the keys
	 */
	public boolean[] getKeys() {
		return keys;
	}
	
	/**
	 * @param key
	 * @param pressed
	 */
	public void setKey(int key, boolean pressed) {
		keys[key] = pressed;
	}
	
	/**
	 * @return the scancodes
	 */
	public Map<Integer, Boolean> getScancodes() {
		return scancodes;
	}
	
	/**
	 * @param scancode
	 * @param pressed
	 */
	public void setScancode(int scancode, boolean pressed) {
		scancodes.put(scancode, pressed);
	}
	
	/**
	 * @return the mods
	 */
	public int getMods() {
		return mods;
	}
	
	/**
	 * @param mods the mods to set
	 */
	public void setMods(int mods) {
		this.mods = mods;
	}
	
}
