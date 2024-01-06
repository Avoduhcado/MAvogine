package com.avogine.ecs.components;

import java.util.*;

import com.avogine.audio.data.AudioSource;
import com.avogine.ecs.EntityComponent;

/**
 * Variation of AudioSourceComponent that allows playing multiple sources at once from a single component.
 * 
 * This could potentially also be implemented with a HashMap of IDs -> AudioSources and then enable the component to 
 * play a specific source in the case of multiple, unique sound effects overlapping. If you need multiples of the same
 * sound effects overlapping, the List approach seems most likely. If a Source upper bound is found, it would likely be
 * worth while to add a limit to how many sources can be added here.
 * @param sources 
 */
public record AudioComponent(List<AudioSource> sources) implements EntityComponent {
	
	/**
	 * 
	 */
	public AudioComponent() {
		this(new ArrayList<>());
	}
	
	@Override
	public void cleanup() {
		sources.forEach(AudioSource::cleanup);
	}

}
