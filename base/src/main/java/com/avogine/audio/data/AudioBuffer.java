package com.avogine.audio.data;

import java.nio.ShortBuffer;

import org.lwjgl.openal.AL10;
import org.lwjgl.system.MemoryUtil;

/**
 *
 */
public class AudioBuffer {

	private final int bufferID;
	
	private ShortBuffer pcm;
//	private ByteBuffer vorbis;
	
	public AudioBuffer(int id, ShortBuffer pcm) {
		bufferID = id;
		this.pcm = pcm;
//		this.vorbis = vorbis;
	}
	
	/**
	 * @return the bufferID
	 */
	public int getBufferID() {
		return bufferID;
	}
	
	/**
	 * 
	 */
	public void cleanup() {
		AL10.alDeleteBuffers(bufferID);
		if (pcm != null) {
			MemoryUtil.memFree(pcm);
		}
	}
	
}
