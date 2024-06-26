package com.avogine.serialize;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.*;

import org.junit.jupiter.api.Test;

import com.avogine.game.scene.Scene;
import com.avogine.io.serializer.SceneMapper;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;

/**
 *
 */
class SceneMapperTest {

	@Test
	void serializeTest() throws JsonMappingException, JsonProcessingException {
		var mapper = new ObjectMapper();
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
//		mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		
		Car car = new Car("Yellow", 1994);
		String json = mapper.writeValueAsString(car);
		
		System.out.println(json);
		
		Car car2 = mapper.readValue(json, Car.class);
		
		assertEquals("Yellow", car2.color);
		assertEquals(1994, car2.year);
		
		JsonNode jsonNode = mapper.readTree(json);
		
		assertEquals("Yellow", jsonNode.get("color").asText());
		assertEquals(1994, jsonNode.get("year").asInt());
	}
	
	/**
	 * TODO Make a proper test for scene serialization support, pass in a target file to serialize too and use Junit temp files to handle automatically cleaning
	 * @see SceneMapper#serializeScene(Scene)
	 * @param scene
	 * @throws IOException
	 */
	public static void serializeScene(Scene scene) throws IOException {
		var mapper = new ObjectMapper();
//	    mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
	    mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		
	    mapper.writeValue(new File("scene.json"), scene);
	    System.out.println("done");

//		String json = mapper.writeValueAsString(scene);
		
//		System.out.println(json);
	}
	
	private static class Car {
		
		private String color;
		private int year;
		
		/**
		 * 
		 */
		public Car() {
			// TODO Auto-generated constructor stub
		}
		
		public Car(String color, int year) {
			this.color = color;
			this.year = year;
		}
		
		
		@Override
		public String toString() {
			return "Color: " + color + " ; " + "year: " + year;
		}
		
	}

}
