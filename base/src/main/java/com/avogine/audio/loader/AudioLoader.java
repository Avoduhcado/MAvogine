package com.avogine.audio.loader;

import com.avogine.audio.data.AudioBuffer;

/**
 *
 */
public class AudioLoader {

	public static AudioBuffer loadSound(String soundFile) {
		return new AudioBuffer(soundFile);
//		int bufferID = alGenBuffers();
//		ShortBuffer pcm;
//		
//		try (STBVorbisInfo info = STBVorbisInfo.malloc();
//				MemoryStack stack = MemoryStack.stackPush()) {
//			String filePath = ResourceConstants.AUDIO_PATH + soundFile;
//			ByteBuffer vorbis = ResourceFileReader.ioResourceToByteBuffer(filePath, 32 * 1024);
//			
//			IntBuffer error = stack.mallocInt(1);
//			long decoder = stb_vorbis_open_memory(vorbis, error, null);
//			if (decoder == MemoryUtil.NULL) {
//				// TODO Probably don't need a RuntimeException here, just log an error.
//				throw new RuntimeException("Failed to open Ogg Vorbis file. Error: " + error.get(0));
//			}
//
//			stb_vorbis_get_info(decoder, info);
//
//			int channels = info.channels();
//			int lengthSamples = stb_vorbis_stream_length_in_samples(decoder);
//
//			pcm = MemoryUtil.memAllocShort(lengthSamples * channels);
//
//			stb_vorbis_get_samples_short_interleaved(decoder, channels, pcm);
//			stb_vorbis_close(decoder);
//
//			// Copy to buffer
//			alBufferData(bufferID, info.channels() == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16, pcm, info.sample_rate());
//		}
//		
//		return new AudioBuffer(bufferID, pcm);
	}

}
