package com.avogine.io.serializer;

import com.fasterxml.jackson.databind.*;

/**
 *
 */
public class JsonMapper {

	private static ObjectMapper defaultMapper;

	private JsonMapper() {
		
	}
	
	/**
	 * @return a singleton {@link ObjectMapper} for general JSON processing.
	 */
	public static ObjectMapper defaultMapper() {
		if (defaultMapper == null) {
			defaultMapper = new ObjectMapper();
			defaultMapper.enable(SerializationFeature.INDENT_OUTPUT);
			defaultMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		}
		return defaultMapper;
	}
	
}
