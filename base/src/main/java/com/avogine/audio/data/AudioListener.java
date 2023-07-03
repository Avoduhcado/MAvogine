package com.avogine.audio.data;

import static org.lwjgl.openal.AL10.*;

import java.nio.FloatBuffer;

import org.joml.*;
import org.lwjgl.system.MemoryStack;

/**
 *
 */
public class AudioListener {

	private final Vector3f transformedAt;
	private final Vector3f transformedUp;

	public AudioListener(Vector3f position, Vector3f velocity, Quaternionf orientation) {
		transformedAt = new Vector3f();
		transformedUp = new Vector3f();
		
		setPosition(position);
		setVelocity(velocity);
		setOrientation(orientation);
	}

	public AudioListener() {
		this(new Vector3f(), new Vector3f(), new Quaternionf());
	}

	public void setVelocity(Vector3f velocity) {
		alListener3f(AL_VELOCITY, velocity.x, velocity.y, velocity.z);
	}

	public void setPosition(Vector3f position) {
		alListener3f(AL_POSITION, position.x, position.y, position.z);
	}
	
	public void setPosition(float x, float y, float z) {
		alListener3f(AL_POSITION, x, y, z);
	}
	
	public void setOrientation(Quaternionf orientation) {
		// The "Forward" direction of objects is -Z by default
		orientation.transform(0, 0, -1, transformedAt);
		orientation.transformPositiveY(transformedUp);
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer orientationBuffer = stack.mallocFloat(6);
			transformedAt.get(orientationBuffer);
			transformedUp.get(3, orientationBuffer);
			alListenerfv(AL_ORIENTATION, orientationBuffer);
		}
	}
	
}
