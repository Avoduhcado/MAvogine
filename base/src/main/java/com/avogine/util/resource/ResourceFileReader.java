package com.avogine.util.resource;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.file.*;
import java.nio.file.FileSystem;
import java.util.*;
import java.util.stream.*;

import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryUtil;

import com.avogine.logging.AvoLog;

/**
 * Static utility for reading resource files.
 * @author Dominus
 *
 */
public class ResourceFileReader {
		
	private static final String FILE_READ_ERROR = "Could not read file! {}";
	
	private ResourceFileReader() {
		
	}

	/**
	 * Read a resource file as plain text and return a {@link StringBuilder} of the file contents.
	 * @param filePath the file name starting from the base {@code resources} directory.
	 * @return A {@code StringBuilder} containing the contents of the file.
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
			AvoLog.log().error(FILE_READ_ERROR, filePath, e);
			System.exit(1);
		}

		return fileContents;
	}
	
	/**
	 * Read a properties file and return the loaded {@link Properties} or an empty one if no file was found.
	 * <p>
	 * TODO Create file if no file is found
	 * @param filePath The name of the file without an extension.
	 * @return The loaded {@link Properties} or an empty one if no file was found.
	 */
	public static Properties readPropertiesFile(String filePath) {
		Properties prop = new Properties();
		try (var inputStream = ResourceFileReader.class.getResourceAsStream("/" + filePath + ".properties")) {
			prop.load(inputStream);
		} catch (IOException e) {
			AvoLog.log().error("Error loading {} properties", filePath, e);
		}
		return prop;
	}
	
	/**
	 * Get a list of file names for every file contained in the given directory.
	 * @param resourceDirectory the directory to scan for files.
	 * @return a list of file names for every file contained in the given directory.
	 */
	public static Set<String> listFileNames(String resourceDirectory) {
		FileSystem fileSystem = null;
		try {
			URI uri = ResourceFileReader.class.getResource(resourceDirectory).toURI();
			Path myPath = null;
			if (uri.getScheme().equals("jar")) {
				// XXX Subsequent calls to listFileNames may throw a ClosedFileSystemException as this FileSystem will already have been closed here.
				fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
				myPath = fileSystem.getPath(resourceDirectory);
			} else {
				myPath = Paths.get(uri);
			}
			Set<Path> filePaths = walkFilePaths(myPath, resourceDirectory);
			return filePaths.stream()
					.map(Path::getFileName)
					.map(Path::toString)
					.collect(Collectors.toSet());
		} catch (URISyntaxException e) {
			throw new UnsupportedOperationException("Cannot read URI for resource: " + resourceDirectory);
		} catch (IOException e) {
			throw new RuntimeException("Could not open FileSystem for resource: " + resourceDirectory);
		} finally {
			if (fileSystem != null) {
				try {
					fileSystem.close();
				} catch (IOException e) {
					AvoLog.log().error("Could not close FileSystem.", e);
				}
			}
		}
	}
	
	private static Set<Path> walkFilePaths(Path path, String resourceDirectory) {
		try (Stream<Path> paths = Files.walk(path, 1)) {
			return paths
					.filter(Files::isRegularFile)
					.collect(Collectors.toSet());
		} catch (IOException e) {
			throw new RuntimeException("Cannot walk resource path: " + resourceDirectory);
		}
	}
	
	/**
	 * 
	 * @param resource
	 * @param bufferSize
	 * @return
	 */
	public static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) {
		AvoLog.log().debug("Reading: {}", resource);
		
		Path path = Paths.get(resource);
		if (path != null && Files.isReadable(path)) {
			return readResourceAsPath(path);
		} else {
			return readResourceAsInputStream(resource, bufferSize);
		}
	}
	
	private static ByteBuffer readResourceAsPath(Path path) {
		try (SeekableByteChannel fc = Files.newByteChannel(path)) {
			ByteBuffer buffer = BufferUtils.createByteBuffer((int) fc.size() + 1);
			while (true) {
				if (fc.read(buffer) == -1) {
					break;
				}
			}
			return buffer.flip();
		} catch (IOException e) {
			AvoLog.log().error(FILE_READ_ERROR, path, e);
			System.exit(1);
		}
		return null;
	}
	
	private static ByteBuffer readResourceAsInputStream(String resource, int bufferSize) {
		try (
				InputStream in = ResourceFileReader.class.getResourceAsStream(resource);
				ReadableByteChannel rbc = Channels.newChannel(in)
				) {
			ByteBuffer buffer = BufferUtils.createByteBuffer(bufferSize);

			while (true) {
				int bytes = rbc.read(buffer);
				if (bytes == -1) {
					break;
				}
				if (buffer.remaining() == 0) {
					buffer = resizeBuffer(buffer, buffer.capacity() * 3 / 2); // 50%
				}
			}
			buffer.flip();
			return MemoryUtil.memSlice(buffer);
		} catch (IOException e) {
			AvoLog.log().error(FILE_READ_ERROR, resource, e);
			System.exit(1);
		}
		return null;
	}
	
	private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
		ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
		buffer.flip();
		newBuffer.put(buffer);
		return newBuffer;
	}
	
}
