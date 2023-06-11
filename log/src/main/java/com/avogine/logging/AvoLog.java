package com.avogine.logging;

import static java.lang.StackWalker.Option.RETAIN_CLASS_REFERENCE;

import java.util.*;

import org.slf4j.*;

/**
 * Simple utility class for retrieving a {@link Logger}.
 */
public final class AvoLog {

	private static final StackWalker walker = StackWalker.getInstance(RETAIN_CLASS_REFERENCE);
	
	private static final Map<Class<?>, Logger> loggerMap = new HashMap<>();
	
	private AvoLog() {
		// AvoLog should only be used through static references.
	}
	
	/**
	 * Retrieve a {@link Logger} from {@link LoggerFactory} for the calling class.
	 * @return a {@link Logger} from {@link LoggerFactory} for the calling class.
	 */
	public static Logger log() {
		Class<?> clazz = walker.getCallerClass();
		return loggerMap.computeIfAbsent(clazz, LoggerFactory::getLogger);
	}
	
}
