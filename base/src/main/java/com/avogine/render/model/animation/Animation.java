package com.avogine.render.model.animation;

import java.util.List;

/**
 * @param name 
 * @param duration 
 * @param frames 
 */
public record Animation(String name, double duration, List<AnimatedFrame> frames) {

}
