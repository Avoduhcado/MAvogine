package com.avogine.game.util;

import com.avogine.game.scene.Scene;

/**
 * Simple immutable object containing relevant data about the game state during the current game loop.
 * @param scene The current scene being displayed.
 * @param delta The amount of time that has passed since the last frame was updated in fractions of a second.
 */
public record GameState<T extends Scene>(T scene, float delta) {

}
