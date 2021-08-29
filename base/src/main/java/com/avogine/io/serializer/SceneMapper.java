package com.avogine.io.serializer;

import java.io.*;

import com.avogine.game.scene.*;
import com.fasterxml.jackson.databind.*;

/**
 *
 */
public class SceneMapper {

	protected SceneMapper() {
		
	}
	
	public static void serializeScene(Scene scene) throws IOException {
		var mapper = new ObjectMapper();
//	    mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
	    mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		
	    mapper.writeValue(new File("scene.json"), scene);
	    System.out.println("done");

//		String json = mapper.writeValueAsString(scene);
		
//		System.out.println(json);
	}
	
}
