package com.avogine.audio.data;

import org.joml.Vector3f;
import org.lwjgl.openal.AL10;

/**
 *
 */
public class AudioSource {

	private final int sourceId;

	/**
	 * 
	 * @param loop If the sound should restart playing when it ends.
	 * @param relative If the position of the source is relative to the listener or not.
	 * Note that 3D audio will only work with mono (single channel) sounds. Any stereo sounds will ignore all
	 * source relative/distance fall-off settings.
	 */
	public AudioSource(boolean loop, boolean relative) {
		this.sourceId = AL10.alGenSources();
		AL10.alSourcei(sourceId, AL10.AL_LOOPING, loop ? AL10.AL_TRUE : AL10.AL_FALSE);
		AL10.alSourcei(sourceId, AL10.AL_SOURCE_RELATIVE, relative ? AL10.AL_TRUE : AL10.AL_FALSE);
	}

	public void setBuffer(int bufferId) {
		stop();
		AL10.alSourcei(sourceId, AL10.AL_BUFFER, bufferId);
	}

	public void setPosition(Vector3f position) {
		AL10.alSource3f(sourceId, AL10.AL_POSITION, position.x, position.y, position.z);
	}

	public void setVelocity(Vector3f speed) {
		AL10.alSource3f(sourceId, AL10.AL_VELOCITY, speed.x, speed.y, speed.z);
	}

	public void setGain(float gain) {
		AL10.alSourcef(sourceId, AL10.AL_GAIN, gain);
	}
	
	public boolean isInitial() {
		return AL10.alGetSourcei(sourceId, AL10.AL_SOURCE_STATE) == AL10.AL_INITIAL;
	}
	
	public boolean isPlaying() {
		return AL10.alGetSourcei(sourceId, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
	}

	public void play() {
		AL10.alSourcePlay(sourceId);
	}

	public void pause() {
		AL10.alSourcePause(sourceId);
	}

	public void stop() {
		AL10.alSourceStop(sourceId);
	}

	public void cleanup() {
		stop();
		AL10.alDeleteSources(sourceId);
	}

}
