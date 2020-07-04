/**
 * 
 */
package com.avogine.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple utility class for retrieving a {@link Logger}.
 */
public class LogUtil {

	private LogUtil() {
		
	}
	
	/**
	 * Retrieve a {@link Logger} from {@link LoggerFactory} for the given simple class name.
	 * <p>
	 * Example:
	 * <pre>LogUtil.requestLogger(MethodHandles.lookup().lookupClass().getSimpleName());</pre>
	 * @param className The class name to get a {@code Logger} for
	 * @return a {@code Logger} from {@code LoggerFactory} for the given class name.
	 */
	public static Logger requestLogger(String className) {
		return LoggerFactory.getLogger(className);
	}
	
}
