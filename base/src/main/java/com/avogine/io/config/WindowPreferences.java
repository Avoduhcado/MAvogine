package com.avogine.io.config;

/**
 * User configurable, but should also have sane defaults configured per project.
 * @param width 
 * @param height 
 * @param fullscreen 
 * @param monitor 
 * @param vsync
 * @param fpsCap 
 * @param backgroundFps
 */
public record WindowPreferences(int width, int height, boolean fullscreen, int monitor, boolean vsync, int fpsCap, int backgroundFps) {

}
