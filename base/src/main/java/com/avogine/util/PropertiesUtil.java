package com.avogine.util;

import java.util.*;

import com.avogine.logging.AvoLog;

/**
 * Static utility for parsing non-String values from a properties file.
 */
public class PropertiesUtil {

	private PropertiesUtil() {
		
	}
	
	/**
	 * Read a boolean value or if no value matches the key return a default value.
	 * @param properties The {@link Properties} file to read from.
	 * @param key The key to attempt to retrieve.
	 * @param defaultValue A default value to return if no value matches the key.
	 * @return the boolean specified by the key or the default.
	 */
	public static Boolean getBoolean(Properties properties, String key, boolean defaultValue) {
		String value = properties.getProperty(key);
		if (value == null) {
			return defaultValue;
		}
		return Boolean.parseBoolean(value.trim());
	}
	
	/**
	 * Read an integer value or if no value matches the key return a default value.
	 * @param properties The {@link Properties} file to read from.
	 * @param key The key to attempt to retrieve.
	 * @param defaultValue A default value to return if no value matches the key.
	 * @return the integer specified by the key or the default.
	 */
	public static Integer getInteger(Properties properties, String key, int defaultValue) {
		String value = properties.getProperty(key);
		try {
			return Integer.parseInt(value.trim());
		} catch(NumberFormatException _) {
			AvoLog.log().info("Could not parse property '{}'. Received invalid value: '{}'", key, value);
		}
		return defaultValue;
	}
	
	/**
	 * Read a float value or if no value matches the key return a default value.
	 * @param properties The {@link Properties} file to read from.
	 * @param key The key to attempt to retrieve.
	 * @param defaultValue A default value to return if no value matches the key.
	 * @return the float specified by the key or the default.
	 */
	public static Float getFloat(Properties properties, String key, float defaultValue) {
		String value = properties.getProperty(key);
		try {
			return Float.parseFloat(value.trim());
		} catch (NumberFormatException _) {
			AvoLog.log().info("Could not parse property '{}'. Received invalid value: '{}'", key, value);
		}
		return defaultValue;
	}
	
	/**
	 * Read a float array value or if no value matches the key return a default value.
	 * @param properties The {@link Properties} file to read from.
	 * @param key The key to attempt to retrieve.
	 * @param defaultValue A default value to return if no value matches the key.
	 * @return the float array specified by the key or the default.
	 */
	public static Float[] getFloatArray(Properties properties, String key, Float[] defaultValue) {
		String value = properties.getProperty(key);
		try {
			return Arrays.stream(value.split(","))
				.map(Float::parseFloat)
				.toList()
				.toArray(new Float[0]);
		} catch (NullPointerException | NumberFormatException e) {
			AvoLog.log().info("Could not parse property", e);
		}
		return defaultValue;
	}
	
}
