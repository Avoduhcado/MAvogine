package com.avogine.game.ui.nuklear;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.nuklear.Nuklear.*;

import java.nio.IntBuffer;
import java.util.function.Supplier;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.nuklear.*;
import org.lwjgl.system.MemoryStack;

import com.avogine.game.Game;
import com.avogine.game.scene.Scene;
import com.avogine.game.ui.nuklear.audio.AudioConfigUI;
import com.avogine.game.util.*;

/**
 * 
 */
public class GameMenu implements UIElement, Renderable, Cleanupable {

	private final Game game;
	
	private boolean saveGameEnabled;
	private boolean loadGameEnabled;
	private Supplier<Scene> quitToTitleSupplier;
	
	private String backgroundTitle;
	private NkRect backgroundPosition;
	private int backgroundOpts;

	private String windowTitle;
	private NkRect position;
	private int windowOpts;
	
	private AudioConfigUI audioConfig;
	private String audioWindowTitle;
	private NkRect audioPosition;
	private int audioWindowOpts;
	
	/**
	 * @param game 
	 * @param quitToTitleSupplier 
	 * 
	 */
	public GameMenu(Game game, Supplier<Scene> quitToTitleSupplier) {
		this.game = game;
		
		saveGameEnabled = false;
		loadGameEnabled = false;
		this.quitToTitleSupplier = quitToTitleSupplier;
		
		backgroundTitle = "BACKGROUND_GLASS";
		backgroundOpts = NK_WINDOW_BACKGROUND | NK_WINDOW_NO_INPUT;
		
		windowOpts = NK_WINDOW_NO_SCROLLBAR;
		windowTitle = "GAME_MENU";
		
		audioConfig = new AudioConfigUI(game.getAudio());
		audioWindowTitle = "AUDIO_GAME_SETTINGS";
		audioWindowOpts = NK_WINDOW_BORDER;
	}
	
	@Override
	public void onRegister(Game game) {
		NkContext context = game.getGUI().getContext();
		
		backgroundPosition = NkRect.calloc();
		nk_begin(context, backgroundTitle, backgroundPosition, 0);
		nk_end(context);
		nk_window_show(context, backgroundTitle, showOnInit() ? NK_SHOWN : NK_HIDDEN);
		nk_window_set_focus(context, backgroundTitle);
		
		position = NkRect.calloc();
		nk_begin(context, windowTitle, position, 0);
		nk_end(context);
		nk_window_show(context, windowTitle, showOnInit() ? NK_SHOWN : NK_HIDDEN);
		nk_window_set_focus(context, windowTitle);
		
		audioPosition = NkRect.calloc();
		nk_begin(context, audioWindowTitle, audioPosition, 0);
		nk_end(context);
		nk_window_show(context, audioWindowTitle, NK_HIDDEN);
		nk_window_set_focus(context, audioWindowTitle);
	}
	
	@Override
	public boolean showOnInit() {
		return false;
	}

	@Override
	public void onRender(SceneState sceneState) {
		var context = game.getGUI().getContext();
		prepare(context, game.getWindow().getId());
	}

	@Override
	public void prepare(NkContext context, long windowId) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);

			GLFW.glfwGetFramebufferSize(windowId, w, h);
			int displayWidth = w.get(0);
			int displayHeight = h.get(0);

			nk_window_show_if(context, windowTitle, NK_HIDDEN, nk_window_is_hidden(context, windowTitle));
			nk_window_set_focus(context, windowTitle);
			nk_window_show_if(context, audioWindowTitle, NK_HIDDEN, nk_window_is_hidden(context, audioWindowTitle));
			nk_window_set_focus(context, audioWindowTitle);
			
			if (nk_input_is_key_released(context.input(), NK_KEY_TAB)) {
				nk_window_show(context, backgroundTitle, NK_SHOWN);
				nk_window_set_focus(context, backgroundTitle);
				nk_window_show(context, windowTitle, NK_SHOWN);
				nk_window_set_focus(context, windowTitle);
				// Maybe move this back in to a regular keyboard listener?
				glfwSetInputMode(windowId, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
			}

			layoutBackgroundGlass(context, stack, displayWidth, displayHeight);
			layoutMenuButtons(context, stack, displayWidth, displayHeight, windowId);
			layoutAudioMenu(context, displayWidth);
		}
	}
	
	private void layoutBackgroundGlass(NkContext context, MemoryStack stack, float width, float height) {
		NkColor halfTransparent = NkColor.calloc(stack).set((byte) 0, (byte) 0, (byte) 0, (byte) 128);
		nk_style_push_color(context, context.style().window().fixed_background().data().color(), halfTransparent);

		backgroundPosition.x(0).y(0).w(width).h(height);
		nk_begin(context, backgroundTitle, backgroundPosition, backgroundOpts);
		nk_end(context);
		
		nk_style_pop_color(context);
	}
	
	private void layoutMenuButtons(NkContext context, MemoryStack stack, float width, float height, long windowId) {
		NkColor transparent = NkColor.calloc(stack).set((byte) 0, (byte) 0, (byte) 0, (byte) 0);
		nk_style_push_color(context, context.style().window().fixed_background().data().color(), transparent);

		position.x((width * 0.5f) - 100).y((height * 0.5f) - 100).w(200).h(45 * 6f);
		if (nk_begin(context, windowTitle, position, windowOpts)) {
			nk_layout_row_dynamic(context, 35, 1);
			if (saveGameEnabled && nk_button_label(context, "Save Game")) {
				// TODO Save games not implemented
			}

			if (loadGameEnabled && nk_button_label(context, "Load Game")) {
				// TODO No saved games
			}

			if (nk_button_label(context, "Options")) {
				showOptions(context);
			}

			if (nk_button_label(context, "Quit to Title")) {
				loadTitleScene();
			}
			
			if (nk_button_label(context, "Quit to Desktop")) {
				GLFW.glfwSetWindowShouldClose(game.getWindow().getId(), true);
			}
			
			if (nk_button_label(context, "Return to Game")) {
				nk_window_show(context, backgroundTitle, NK_HIDDEN);
				nk_window_show(context, windowTitle, NK_HIDDEN);
				nk_window_set_focus(context, windowTitle);
				glfwSetInputMode(windowId, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
			}
		}
		nk_end(context);
		
		nk_style_pop_color(context);
	}
	
	private void layoutAudioMenu(NkContext context, float width) {
		audioPosition.x((width * 0.5f) - 200).y(300).w(400).h(230);
		if (nk_begin(context, audioWindowTitle, audioPosition, audioWindowOpts)) {
			audioConfig.layout(context);

			nk_spacer(context);

			nk_layout_row_begin(context, NK_DYNAMIC, 25, 3);
			nk_layout_row_push(context, 0.33f);
			nk_spacer(context);
			if (nk_button_label(context, "Close")) {
				nk_window_show(context, audioWindowTitle, NK_HIDDEN);
				nk_window_show(context, windowTitle, NK_SHOWN);
				nk_window_set_focus(context, windowTitle);
			}
			nk_layout_row_end(context);
		}
		nk_end(context);
	}

	private void loadTitleScene() {
		game.queueSceneSwap(quitToTitleSupplier.get());
	}
	
	private void showOptions(NkContext context) {
		nk_window_show(context, audioWindowTitle, NK_SHOWN);
		nk_window_show(context, windowTitle, NK_HIDDEN);
		nk_window_set_focus(context, audioWindowTitle);
	}

	@Override
	public void onCleanup() {
		backgroundPosition.free();
		position.free();
		audioPosition.free();
		audioConfig.cleanup();
	}

}
