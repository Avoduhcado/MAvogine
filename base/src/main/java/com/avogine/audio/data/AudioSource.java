package com.avogine.audio.data;

import static org.lwjgl.openal.AL10.*;

import org.joml.Vector3f;

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
		this.sourceId = alGenSources();
		alSourcei(sourceId, AL_LOOPING, loop ? AL_TRUE : AL_FALSE);
		alSourcei(sourceId, AL_SOURCE_RELATIVE, relative ? AL_TRUE : AL_FALSE);
	}

	public void setBuffer(int bufferId) {
		stop();
		alSourcei(sourceId, AL_BUFFER, bufferId);
	}

	public void setPosition(Vector3f position) {
		alSource3f(sourceId, AL_POSITION, position.x, position.y, position.z);
	}

	public void setVelocity(Vector3f speed) {
		alSource3f(sourceId, AL_VELOCITY, speed.x, speed.y, speed.z);
	}

	public void setGain(float gain) {
		alSourcef(sourceId, AL_GAIN, gain);
	}
	
	public boolean isInitial() {
		return alGetSourcei(sourceId, AL_SOURCE_STATE) == AL_INITIAL;
	}
	
	public boolean isPlaying() {
		return alGetSourcei(sourceId, AL_SOURCE_STATE) == AL_PLAYING;
	}
	
	public boolean isPaused() {
		return alGetSourcei(sourceId, AL_SOURCE_STATE) == AL_PAUSED;
	}
	
	public boolean isStopped() {
		return alGetSourcei(sourceId, AL_SOURCE_STATE) == AL_STOPPED;
	}

	public void play() {
		alSourcePlay(sourceId);
	}
	
	public void play(AudioBuffer buffer) {
		alSourcei(sourceId, AL_BUFFER, buffer.getBufferID());
		alSourcePlay(sourceId);
	}

	public void pause() {
		alSourcePause(sourceId);
	}

	public void stop() {
		alSourceStop(sourceId);
	}

	public void cleanup() {
		stop();
		alDeleteSources(sourceId);
	}

}
