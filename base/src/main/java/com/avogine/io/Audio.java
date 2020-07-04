package com.avogine.io;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.system.MemoryUtil;

import com.avogine.audio.data.AudioListener;
import com.avogine.audio.data.AudioSource;

/**
 *
 */
public class Audio {

	private long device;
	private long context;

	private AudioListener listener;

	private final Map<String, AudioSource> audioSourceMap;

	private final Matrix4f cameraMatrix;

	/**
	 * 
	 */
	public Audio() {
		audioSourceMap = new HashMap<>();
		cameraMatrix = new Matrix4f();
	}

	/**
	 * Initialize the OpenAL subsystem:
	 * <ul>
	 * <li>Open the default device.
	 * <li>Create the capabilities for that device.
	 * <li>Create a sound context, like the OpenGL one, and set it as the current one.
	 * </ul>
	 */
	public void init() {
		device = ALC10.alcOpenDevice((ByteBuffer) null);
		if (device == MemoryUtil.NULL) {
			throw new IllegalStateException("Failed to open the default OpenAL device.");
		}
		ALCCapabilities deviceCaps = ALC.createCapabilities(device);
		context = ALC10.alcCreateContext(device, (IntBuffer) null);
		if (context == MemoryUtil.NULL) {
			throw new IllegalStateException("Failed to create OpenAL context.");
		}
		ALC10.alcMakeContextCurrent(context);
		AL.createCapabilities(deviceCaps);
	}

	/**
	 * @param name
	 * @param audioSource
	 */
	public void addAudioSource(String name, AudioSource audioSource) {
		audioSourceMap.put(name, audioSource);
	}

	/**
	 * @param name
	 * @return
	 */
	public AudioSource getAudioSource(String name) {
		return audioSourceMap.get(name);
	}

	/**
	 * @param name
	 */
	public void playAudioSource(String name) {
		if (audioSourceMap.containsKey(name) && !audioSourceMap.get(name).isPlaying()) {
			audioSourceMap.get(name).play();
		}
	}

	/**
	 * @param name
	 */
	public void removeAudioSource(String name) {
		audioSourceMap.remove(name);
	}

	public AudioListener getListener() {
		return listener;
	}

	public void setListener(AudioListener listener) {
		this.listener = listener;
	}

	public void updateListenerPosition(Vector3f position, Vector3f rotationZYX) {
		// Update camera matrix with camera data
		cameraMatrix.rotationX((float) Math.toRadians(rotationZYX.z))
				.rotateY((float) Math.toRadians(rotationZYX.y))
				.translate(-position.x, -position.y, -position.z);

		listener.setPosition(position);
		Vector3f at = new Vector3f();
		cameraMatrix.positiveZ(at).negate();
		Vector3f up = new Vector3f();
		cameraMatrix.positiveY(up);
		listener.setOrientation(at, up);
	}

	/**
	 * @param model
	 */
	public void setAttenuationModel(int model) {
		AL10.alDistanceModel(model);
	}

	/**
	 * 
	 */
	public void cleanup() {
		for (AudioSource soundSource : audioSourceMap.values()) {
			soundSource.cleanup();
		}
		audioSourceMap.clear();
		if (context != MemoryUtil.NULL) {
			ALC10.alcDestroyContext(context);
		}
		if (device != MemoryUtil.NULL) {
			ALC10.alcCloseDevice(device);
		}
	}

}
