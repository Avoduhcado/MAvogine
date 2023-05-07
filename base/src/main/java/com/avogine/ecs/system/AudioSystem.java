package com.avogine.ecs.system;

import java.util.UUID;

import com.avogine.audio.data.AudioListener;
import com.avogine.ecs.*;
import com.avogine.ecs.components.*;
import com.avogine.game.Game;
import com.avogine.game.scene.ECSScene;
import com.avogine.game.util.*;

/**
 *
 */
public class AudioSystem extends EntitySystem implements Updateable {

	private static record SourceArchetype(UUID id, AudioSourceComponent audioSource, TransformComponent transform) implements EntityArchetype {}
	private static record ListenerArchetype(UUID id, AudioListenerTag tag, TransformComponent transform) implements EntityArchetype {}
	
	private final AudioListener audioListener;
	
	/**
	 * 
	 */
	public AudioSystem() {
		audioListener = new AudioListener();
	}
	
	@Override
	public void onRegister(Game game) {
		// Nothing to register
	}

	@Override
	public void onUpdate(GameState gameState) {
		if (gameState.scene() instanceof ECSScene scene) {
			updateSources(scene.getEntityManager());
		}
	}
	
	private void updateSources(EntityManager manager) {
		manager.query(ListenerArchetype.class).findFirst().ifPresent(listener -> {
			audioListener.setPosition(listener.transform.position());
			audioListener.setOrientation(listener.transform.orientation());
		});
		
		manager.query(SourceArchetype.class).forEach(source -> {
			if (source.audioSource.source().isInitial()) {
				source.audioSource.source().play();
			}
			
			source.audioSource.source().setPosition(source.transform.position());
		});
	}

}
