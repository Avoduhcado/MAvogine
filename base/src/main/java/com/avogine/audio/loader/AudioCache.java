package com.avogine.audio.loader;

import java.util.HashMap;
import java.util.Map;

import com.avogine.audio.data.AudioBuffer;

/**
 *
 */
public class AudioCache {

	private Map<String, AudioBuffer> soundMap = new HashMap<>();
	
	private static AudioCache instance;
	
	public static AudioCache getInstance() {
		if (instance == null) {
			instance = new AudioCache();
		}
		return instance;
	}
	
	/**
	 * @param soundFile
	 * @return
	 */
	public AudioBuffer getSound(String soundFile) {
		return soundMap.computeIfAbsent(soundFile, v -> AudioLoader.loadSound(soundFile));
	}
	
}
