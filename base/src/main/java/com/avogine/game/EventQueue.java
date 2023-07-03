package com.avogine.game;

import java.util.*;

import com.avogine.io.event.Event;

public class EventQueue {

	private final List<Event> events = new LinkedList<>();
	
	public void addEvent(Event event) {
		events.add(event);
	}
	
	public void processCurrentEvents() {
		events.subList(0, events.size() - 1);
	}
	
}
