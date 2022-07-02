/**
 * 
 */
package com.avogine.logging;

import static java.lang.StackWalker.Option.*;

import org.slf4j.*;

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
	public static Logger requestExplicitLogger(String className) {
		return LoggerFactory.getLogger(className);
	}
	
	/**
	 * Retrieve a {@link Logger} from {@link LoggerFactory} for the calling class.
	 * @return a {@link Logger} from {@link LoggerFactory} for the calling class.
	 */
	public static Logger requestLogger() {
		StackWalker walker = StackWalker.getInstance(RETAIN_CLASS_REFERENCE);
		Class<?> clazz = walker.getCallerClass();
		return LoggerFactory.getLogger(clazz);
	}
	
}
