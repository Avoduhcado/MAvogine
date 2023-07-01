package com.avogine.ecs.components;

import java.util.List;

import com.avogine.audio.data.AudioSource;
import com.avogine.ecs.EntityComponent;

/**
 * TODO Make this store less heavy weight values
 * @param sources 
 */
public record AudioComponent(List<AudioSource> sources) implements EntityComponent {

}
