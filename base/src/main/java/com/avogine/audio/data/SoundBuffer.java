package com.avogine.audio.data;

import static org.lwjgl.stb.STBVorbis.*;

import java.io.IOException;
import java.nio.*;

import org.lwjgl.openal.AL10;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.*;

import com.avogine.logging.AvoLog;
import com.avogine.util.ResourceUtils;

/**
 *
 */
public class SoundBuffer {

	private final int bufferID;
	
	private ShortBuffer pcm;
	
	/**
	 * @param filePath 
	 */
	public SoundBuffer(String filePath) {
		this.bufferID = AL10.alGenBuffers();
		try (STBVorbisInfo info = STBVorbisInfo.malloc()) {
			pcm = readVorbis(filePath, info);
			
			// Copy to buffer
			AL10.alBufferData(bufferID, info.channels() == 1 ? AL10.AL_FORMAT_MONO16 : AL10.AL_FORMAT_STEREO16, pcm, info.sample_rate());
		} catch (IOException e) {
			AvoLog.log().error("Failed to load sound file: {}", filePath, e);
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
	
	private ShortBuffer readVorbis(String filePath, STBVorbisInfo info) throws IOException {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer error = stack.mallocInt(1);
			
			ByteBuffer fileBuffer = ResourceUtils.readResourceToBuffer(filePath);
			long decoder = stb_vorbis_open_memory(fileBuffer, error, null);
			if (decoder == MemoryUtil.NULL) {
				throw new IOException("Failed to open Ogg Vorbis file. Error: " + error.get(0));
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
	
	/**
	 * @return the bufferID
	 */
	public int getBufferID() {
		return bufferID;
	}
	
}
