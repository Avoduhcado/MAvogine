package com.avogine.io.config;

/**
 * User configurable, but should also have sane defaults configured per project.
 * @param width 
 * @param height 
 * @param fullscreen 
 * @param monitor 
 * @param fps A value of 0 indicates enabling VSync
 * @param backgroundFps
 */
public record WindowPreferences(int width, int height, boolean fullscreen, int monitor, int fps, int backgroundFps) {

}
