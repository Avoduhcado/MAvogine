package com.avogine.audio.loader;

import java.nio.*;

import org.lwjgl.openal.AL10;
import org.lwjgl.stb.*;
import org.lwjgl.system.*;

import com.avogine.audio.data.AudioBuffer;
import com.avogine.util.resource.*;

/**
 *
 */
public class AudioLoader {

	/**
	 * @param soundFile
	 * @return
	 */
	public static AudioBuffer loadSound(String soundFile) {
		int bufferID = AL10.alGenBuffers();
		ShortBuffer pcm;
		
		try (STBVorbisInfo info = STBVorbisInfo.malloc();
				MemoryStack stack = MemoryStack.stackPush()) {
			String filePath = ResourceConstants.AUDIO_PATH + soundFile;
			ByteBuffer vorbis = ResourceFileReader.ioResourceToByteBuffer(filePath, 32 * 1024);
			
			IntBuffer error = stack.mallocInt(1);
			long decoder = STBVorbis.stb_vorbis_open_memory(vorbis, error, null);
			if (decoder == MemoryUtil.NULL) {
				throw new RuntimeException("Failed to open Ogg Vorbis file. Error: " + error.get(0));
			}

			STBVorbis.stb_vorbis_get_info(decoder, info);

			int channels = info.channels();

			int lengthSamples = STBVorbis.stb_vorbis_stream_length_in_samples(decoder);

			pcm = MemoryUtil.memAllocShort(lengthSamples);

			pcm.limit(STBVorbis.stb_vorbis_get_samples_short_interleaved(decoder, channels, pcm) * channels);
			STBVorbis.stb_vorbis_close(decoder);

			// Copy to buffer
			AL10.alBufferData(bufferID, info.channels() == 1 ? AL10.AL_FORMAT_MONO16 : AL10.AL_FORMAT_STEREO16, pcm, info.sample_rate());
		}
		
		return new AudioBuffer(bufferID, pcm);
	}

}
