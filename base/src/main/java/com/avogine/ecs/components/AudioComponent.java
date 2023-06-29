package com.avogine.ecs.components;

import java.util.List;

import com.avogine.audio.data.AudioSource;
import com.avogine.ecs.EntityComponent;

/**
 * TODO Implement multiple sources per component
 * @param sourceID 
 */
public record AudioComponent(List<AudioSource> sources) implements EntityComponent {

}
