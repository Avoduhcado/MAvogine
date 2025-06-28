package com.avogine.render.data;

import java.util.List;

import com.avogine.render.data.model.AnimatedFrame;

/**
 * @param name 
 * @param duration 
 * @param frames 
 */
public record Animation(String name, double duration, List<AnimatedFrame> frames) {

}
