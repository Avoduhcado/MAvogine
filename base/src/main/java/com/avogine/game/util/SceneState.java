package com.avogine.game.util;

import com.avogine.game.Game;
import com.avogine.game.scene.Scene;

/**
 * A simple immutable record containing the current {@link Scene} from {@link Game#getCurrentScene()}.
 * @param scene The current scene being displayed.
 */
public record SceneState(Scene scene) {

}
