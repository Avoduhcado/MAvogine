package com.avogine.io.serializer;

import java.io.*;

import com.avogine.game.scene.*;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonAutoDetect.*;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;

/**
 *
 */
public class SceneMapper {

	protected SceneMapper() {
		
	}
	
	public static void main(String[] args) {
		try {
			SceneMapper.serializeTest();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void serializeTest() throws JsonMappingException, JsonProcessingException {
		var mapper = new ObjectMapper();
	    mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
//		mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		
		Car car = new Car("Yellow", 1994);
		String json = mapper.writeValueAsString(car);
		
		System.out.println(json);
		
		Car car2 = mapper.readValue(json, Car.class);
		
		System.out.println(car2.toString());
		
		JsonNode jsonNode = mapper.readTree(json);
		
		System.out.println(jsonNode.get("color").asText());
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
