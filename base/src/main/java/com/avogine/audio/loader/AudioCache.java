package com.avogine.audio.loader;

import java.util.*;

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
	 * @return An {@link AudioBuffer} containing the requested sound file
	 */
	public AudioBuffer getSound(String soundFile) {
		return soundMap.computeIfAbsent(soundFile, v -> AudioLoader.loadSound(soundFile));
	}
	
	public void cleanup() {
		soundMap.values().forEach(AudioBuffer::cleanup);
		soundMap.clear();
	}
	
}
