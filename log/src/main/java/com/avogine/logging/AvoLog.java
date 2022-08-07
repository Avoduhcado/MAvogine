package com.avogine.logging;

import static java.lang.StackWalker.Option.*;

import org.slf4j.*;

/**
 * Simple utility class for retrieving a {@link Logger}.
 */
public final class AvoLog {

	private AvoLog() {
		// AvoLog should only be used through static references.
	}
	
	/**
	 * Retrieve a {@link Logger} from {@link LoggerFactory} for the calling class.
	 * @return a {@link Logger} from {@link LoggerFactory} for the calling class.
	 */
	public static Logger log() {
		StackWalker walker = StackWalker.getInstance(RETAIN_CLASS_REFERENCE);
		Class<?> clazz = walker.getCallerClass();
		return LoggerFactory.getLogger(clazz);
	}
	
}
