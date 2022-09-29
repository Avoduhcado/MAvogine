package com.avogine.game.util;

import com.avogine.game.*;
import com.avogine.game.scene.*;

/**
 * A simple immutable record containing the current {@link Scene} from {@link Game#getCurrentScene()}.
 * @param scene The current scene being displayed.
 *
 */
public record SceneState(Scene scene) {

}
