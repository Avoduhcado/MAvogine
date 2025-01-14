package com.avogine.audio.data;

import static org.lwjgl.stb.STBVorbis.*;

import java.nio.*;

import org.lwjgl.openal.AL10;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.*;

/**
 *
 */
public class AudioBuffer {

	private final int bufferID;
	
	private ShortBuffer pcm;
	
	/**
	 * @param filePath 
	 */
	public AudioBuffer(String filePath) {
		this.bufferID = AL10.alGenBuffers();
		try (STBVorbisInfo info = STBVorbisInfo.malloc()) {
			pcm = readVorbis(filePath, info);
			
			// Copy to buffer
			AL10.alBufferData(bufferID, info.channels() == 1 ? AL10.AL_FORMAT_MONO16 : AL10.AL_FORMAT_STEREO16, pcm, info.sample_rate());
		}
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
	
	/**
	 * @return the bufferID
	 */
	public int getBufferID() {
		return bufferID;
	}
	
	private ShortBuffer readVorbis(String filePath, STBVorbisInfo info) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer error = stack.mallocInt(1);
			long decoder = stb_vorbis_open_filename(filePath, error, null);
			if (decoder == MemoryUtil.NULL) {
				throw new RuntimeException("Failed to open Ogg Vorbis file. Error: " + error.get(0));
			}
			
			stb_vorbis_get_info(decoder, info);
			
			int channels = info.channels();
			
			int lengthSamples = stb_vorbis_stream_length_in_samples(decoder);
			
			ShortBuffer result = MemoryUtil.memAllocShort(lengthSamples * channels);
			
			result.limit(stb_vorbis_get_samples_short_interleaved(decoder, channels, result));
			stb_vorbis_close(decoder);
			
			return result;
		}
	}
	
}
