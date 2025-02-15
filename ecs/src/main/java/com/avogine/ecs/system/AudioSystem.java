package com.avogine.ecs.system;

import java.util.UUID;

import com.avogine.audio.data.*;
import com.avogine.ecs.*;
import com.avogine.ecs.components.*;
import com.avogine.game.scene.*;
import com.avogine.game.util.*;

/**
 *
 */
public class AudioSystem extends EntitySystem implements Updateable {

	private static record SourceArchetype(UUID id, AudioSourceComponent audioSource, TransformComponent transform) implements EntityArchetype {}
	private static record SourcesArchetype(UUID id, AudioComponent audio, TransformComponent transform) implements EntityArchetype {}
	private static record ListenerArchetype(UUID id, AudioListenerTag tag, TransformComponent transform) implements EntityArchetype {}
	
	private final SoundListener audioListener;
	
	/**
	 * 
	 */
	public AudioSystem() {
		audioListener = new SoundListener();
	}
	
	@Override
	public void onRegister(RegisterableGame game) {
		// Nothing to register
	}

	@Override
	public void onUpdate(Scene scene, float delta) {
		if (scene instanceof ECSScene ecsScene) {
			updateSources(ecsScene.getEntityManager());
		}
	}
	
	private void updateSources(EntityManager manager) {
		manager.query(ListenerArchetype.class).findFirst().ifPresent(listener -> {
			audioListener.setPosition(listener.transform.position());
			audioListener.setOrientation(listener.transform.orientation());
		});
		
		manager.query(SourceArchetype.class).forEach(source -> {
			source.audioSource.source().setPosition(source.transform.position());
		});
		
		manager.query(SourcesArchetype.class).forEach(source -> {
			source.audio.sources().forEach(audioSource -> {
				audioSource.setPosition(source.transform.position());
				if (audioSource.isStopped()) {
					audioSource.cleanup();
				}
			});
			source.audio.sources().removeIf(SoundSource::isStopped);
		});
	}

}
