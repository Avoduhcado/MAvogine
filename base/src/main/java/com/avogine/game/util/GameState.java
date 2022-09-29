package com.avogine.game.util;

import com.avogine.game.scene.*;

/**
 * Simple immutable object containing relevant data about the game state during the current game loop.
 * @param delta The amount of time that has passed since the last frame was updated in fractions of a second.
 * @param scene The current scene being displayed.
 */
public record GameState(Scene scene, float delta) {

}
