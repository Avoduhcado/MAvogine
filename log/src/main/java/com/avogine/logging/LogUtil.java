/**
 * 
 */
package com.avogine.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dominus
 *
 */
public class LogUtil {

	private LogUtil() {
		
	}
	
	/**
	 * Retrieve a {@link Logger} from {@link LoggerFactory} for the given {@code Class}.
	 * @param clazz The {@code Class} to get a {@code Logger} for
	 * @return a {@code Logger} from {@code LoggerFactory} for the given {@code Class}.
	 */
	public static Logger requestLogger(Class<?> clazz) {
		return LoggerFactory.getLogger(clazz.getSimpleName());
	}
	
}
