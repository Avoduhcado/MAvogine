package com.avogine.util;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.stream.Collectors;

import org.lwjgl.BufferUtils;

import com.avogine.logging.AvoLog;

/**
 *
 */
public class ResourceUtils {

	private ResourceUtils() {
		
	}
	
	private static final int DEFAULT_BUFFER_SIZE = 8 * 1024;
	
	/**
	 * @param resourcePath A relative path to the resource file from the resources directory.
	 * @return A string containing the contents of the file, line by line.
	 */
	public static String readResource(String resourcePath) {
		String fileContents = null;
		try (var in = ResourceUtils.class.getResourceAsStream(resourcePath);
				var reader = new BufferedReader(new InputStreamReader(in));
				var lines = reader.lines();) {
			fileContents = lines.collect(Collectors.joining("\n"));
		} catch (IOException e) {
			AvoLog.log().error("Failed to read file: {}", resourcePath, e);
		}
		return fileContents;
	}
	
	/**
	 * Allocates a ByteBuffer via {@link BufferUtils} with the given size and attempts to read the given resource into it, resizing if necessary.
	 * </p>
	 * Caution should be taken when reading large files as the ByteBuffer will not only be held in memory, but also persist until
	 * garbage collection removes it. A sensible bufferSize should also be chosen relative to the resources being read since if the
	 * bytes don't fit in the initial size, the buffer will be doubled in size until they do. 
	 * 
	 * @param resourcePath
	 * @param bufferSize
	 * @return a buffer containing the byte contents of the given resource.
	 */
	public static ByteBuffer readResourceToBuffer(String resourcePath, int bufferSize) {
		AvoLog.log().debug("Reading: {}", resourcePath);
		
		ByteBuffer buffer = null;
		try (var source = ResourceUtils.class.getResourceAsStream(resourcePath)) {
			buffer = BufferUtils.createByteBuffer(bufferSize);
			
			byte[] buf = new byte[8192];
			while (true) {
				int bytesRead = source.read(buf, 0, buf.length);
				if (bytesRead == -1) {
					break;
				}
				if (buffer.remaining() < bytesRead) {
					buffer = resizeBuffer(buffer, Math.max(buffer.capacity() * 2, buffer.capacity() - buffer.remaining() + bytesRead));
				}
				buffer.put(buf, 0, bytesRead);
			}
			
			buffer.flip();
		} catch (IOException e) {
			AvoLog.log().error("Failed to read file: {}", resourcePath, e);
		}
		return buffer;
	}
	
	/**
	 * Allocates a ByteBuffer via {@link BufferUtils} with a default size and attempts to read the given resource into it, resizing if necessary.
	 * @param resourcePath
	 * @return a buffer containing the byte contents of the given resource.
	 */
	public static ByteBuffer readResourceToBuffer(String resourcePath) {
		return readResourceToBuffer(resourcePath, DEFAULT_BUFFER_SIZE);
	}

	/**
	 * XXX Some sort of optional debugging output could be included here to offer suggestions for larger sized files to allocate a larger initial buffer to avoid multiple calls to increase the size.
	 * @param buffer
	 * @param newCapacity
	 * @return
	 */
	private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
		ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
		buffer.flip();
		newBuffer.put(buffer);
		
		return newBuffer;
	}
}
