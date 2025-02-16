package com.avogine.serialize;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

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
	
	private static class Car {
		
		private String color;
		private int year;
		
		@SuppressWarnings("unused")
		protected Car() {
			// Used by serializer
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
