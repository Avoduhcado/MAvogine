package com.avogine.ecs.components;

import com.avogine.audio.data.AudioSource;
import com.avogine.ecs.EntityComponent;

/**
 * @param source The {@link AudioSource} being played by this component.
 *
 */
public record AudioSourceComponent(AudioSource source) implements EntityComponent {

	@Override
	public void cleanup() {
		source.cleanup();
	}
	
}
