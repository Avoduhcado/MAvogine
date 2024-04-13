package com.avogine.game;

import java.util.*;

import com.avogine.experimental.annotation.InDev;
import com.avogine.io.event.AvoEvent;

@InDev
public class EventQueue {

	private final List<AvoEvent> events = new LinkedList<>();
	
	public void addEvent(AvoEvent event) {
		events.add(event);
	}
	
	public void processCurrentEvents() {
		events.subList(0, events.size() - 1);
	}
	
}
