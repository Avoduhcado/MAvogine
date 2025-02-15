package com.avogine.ecs.components;

import com.avogine.audio.data.SoundSource;
import com.avogine.ecs.EntityComponent;

/**
 * @param source The {@link SoundSource} being played by this component.
 *
 */
public record AudioSourceComponent(SoundSource source) implements EntityComponent {

	@Override
	public void cleanup() {
		source.cleanup();
	}
	
}
