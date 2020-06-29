package com.avogine.audio.data;

import org.joml.Vector3f;
import org.lwjgl.openal.AL10;

/**
 *
 */
public class AudioListener {

	public AudioListener() {
		this(new Vector3f(0, 0, 0));
	}

	public AudioListener(Vector3f position) {
		AL10.alListener3f(AL10.AL_POSITION, position.x, position.y, position.z);
		AL10.alListener3f(AL10.AL_VELOCITY, 0, 0, 0);

	}

	public void setSpeed(Vector3f speed) {
		AL10.alListener3f(AL10.AL_VELOCITY, speed.x, speed.y, speed.z);
	}

	public void setPosition(Vector3f position) {
		AL10.alListener3f(AL10.AL_POSITION, position.x, position.y, position.z);
	}

	public void setOrientation(Vector3f at, Vector3f up) {
		float[] data = new float[6];
		data[0] = at.x;
		data[1] = at.y;
		data[2] = at.z;
		data[3] = up.x;
		data[4] = up.y;
		data[5] = up.z;
		AL10.alListenerfv(AL10.AL_ORIENTATION, data);
	}

}
