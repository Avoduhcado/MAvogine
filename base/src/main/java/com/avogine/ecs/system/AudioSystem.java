package com.avogine.ecs.system;

import org.joml.Quaternionf;

import com.avogine.audio.data.*;
import com.avogine.ecs.EntityManager;
import com.avogine.ecs.components.*;
import com.avogine.game.Game;
import com.avogine.game.scene.ECSScene;
import com.avogine.game.util.*;

/**
 *
 */
public class AudioSystem implements Updateable {

	private final AudioListener audioListener;
	
	private final Quaternionf listenerOrientation;
	
	/**
	 * 
	 */
	public AudioSystem() {
		audioListener = new AudioListener();
		listenerOrientation = new Quaternionf();
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
		manager.query(AudioListenerTag.class, TransformComponent.class).findFirst().ifPresent(chunk -> {
			var transform = chunk.getAs(TransformComponent.class, 0);
			transform.orientation(listenerOrientation);
			audioListener.setPosition(transform.x(), transform.y(), transform.z());
			audioListener.setOrientation(listenerOrientation);
		});
		manager.query(AudioComponent.class, TransformComponent.class).forEach(chunk -> {
			for (int i = 0; i < chunk.getChunkSize(); i++) {
				var transform = chunk.getAs(TransformComponent.class, i);
				var audioSource = chunk.getAs(AudioComponent.class, i);
				
				audioSource.sources().forEach(source -> {
					source.setPosition(transform.x(), transform.y(), transform.z());
					if (source.isStopped()) {
						source.cleanup();
					}
				});
				audioSource.sources().removeIf(AudioSource::isStopped);
			}
		});
	}

}
