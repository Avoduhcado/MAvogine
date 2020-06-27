package com.avogine.game;

import java.util.LinkedList;
import java.util.List;

import com.avogine.experimental.annotation.InDev;
import com.avogine.io.event.Event;

@InDev
public class EventQueue {

	private final List<Event> events = new LinkedList<>();
	
	public void addEvent(Event event) {
		events.add(event);
	}
	
	public void processCurrentEvents() {
		events.subList(0, events.size() - 1);
	}
	
}
