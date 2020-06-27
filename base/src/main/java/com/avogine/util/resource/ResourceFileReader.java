package com.avogine.util.resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Static utility for reading resource files.
 * @author Dominus
 *
 */
public class ResourceFileReader {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

	/**
	 * Read a resource file as plain text and return a {@link StringBuilder} of the file contents.
	 * @param filePath the file name starting from <tt>src/main/resources/</tt>
	 * @return A <tt>StringBuilder</tt> containing the contents of the file.
	 */
	public static StringBuilder readTextFile(String filePath) {
		StringBuilder fileContents = new StringBuilder();
		try (InputStream in = ResourceFileReader.class.getResourceAsStream(filePath);
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));) {
			String line;
			while ((line = reader.readLine()) != null) {
				fileContents.append(line).append("\n");
			}
		} catch (IOException e) {
			logger.error("Could not read file! {}", filePath, e);
			System.exit(1);
		}

		return fileContents;
	}
	
	/**
	 * 
	 * @param resourceDirectory
	 * @return
	 */
	public static Set<Path> listResourcePaths(String resourceDirectory) {
		Set<Path> filePaths = new HashSet<>();
		try {
			URI uri = ResourceFileReader.class.getResource(resourceDirectory).toURI();
			Path myPath = null;
			if (uri.getScheme().equals("jar")) {
				// XXX Unclear if this needs explicitly closed, putting it in a try-with-resources here causes a Closed exception
				FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
				myPath = fileSystem.getPath(resourceDirectory);
			} else {
				myPath = Paths.get(uri);
			}
			try (Stream<Path> paths = Files.walk(myPath, 1)) {
				filePaths = paths
						.filter(Files::isRegularFile)
						.collect(Collectors.toSet());
			} catch (IOException e) {
				throw new RuntimeException("Cannot walk resource path: " + resourceDirectory);
			}
		} catch (URISyntaxException e) {
			throw new UnsupportedOperationException("Cannot read URI for resource: " + resourceDirectory);
		} catch (IOException e) {
			throw new RuntimeException("Could not open FileSystem for resource: " + resourceDirectory);
		}
		return filePaths;
	}
	
	/**
	 * 
	 * @param resource
	 * @param bufferSize
	 * @return
	 */
	public static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) {
		logger.info("Reading: {}", resource);
		ByteBuffer buffer = null;

		Path path = Paths.get(resource);
		if (Files.isReadable(path)) {
			try (SeekableByteChannel fc = Files.newByteChannel(path)) {
				buffer = MemoryUtil.memAlloc((int) fc.size() + 1);
				while (fc.read(buffer) != -1) {
					
				}
				buffer.flip();
			} catch (IOException e) {
				logger.error("Could not read file! {}", path, e);
				System.exit(1);
			}
		} else {
			try (InputStream in = ResourceFileReader.class.getResourceAsStream(resource);
					ReadableByteChannel rbc = Channels.newChannel(in)) {
				buffer = MemoryUtil.memAlloc(bufferSize);

				while (true) {
					int bytes = rbc.read(buffer);
					if (bytes == -1) {
						break;
					}
					if (buffer.remaining() == 0) {
						buffer = resizeBuffer(buffer, buffer.capacity() * 2);
					}
				}
				buffer.flip();
			} catch (IOException e) {
				logger.error("Could not read file! {}", path, e);
				System.exit(1);
			}
		}

		return buffer;
	}
	
	private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
		ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
		buffer.flip();
		newBuffer.put(buffer);
		return newBuffer;
	}

}
