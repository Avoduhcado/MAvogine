package com.avogine.game.ui.nuklear;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.nuklear.Nuklear.*;

import java.nio.IntBuffer;
import java.util.function.Supplier;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.nuklear.*;
import org.lwjgl.system.MemoryStack;

import com.avogine.audio.Audio;
import com.avogine.game.scene.Scene;
import com.avogine.game.state.GameState;
import com.avogine.game.ui.nuklear.audio.AudioConfigUI;
import com.avogine.game.util.*;
import com.avogine.io.*;

/**
 * TODO Investigate initializing with all elements hidden, and a key listener for ESC to unhide the menu
 * @param <T> 
 */
public class GameMenu<T extends GameState<?, ?>> implements UIElement, Renderable, Cleanupable {

	private final NkContext context;
	
	private boolean loadGameEnabled;
	private Supplier<Class<T>> quitToTitleSupplier;
	
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
	 * @param context 
	 * @param audio 
	 * @param stateSwapper 
	 * @param quitToTitleSupplier 
	 * 
	 */
	public GameMenu(NkContext context, Audio audio, Supplier<Class<T>> quitToTitleSupplier) {
		this.context = context;
		
		loadGameEnabled = false;
		this.quitToTitleSupplier = quitToTitleSupplier;
		
		backgroundTitle = "BACKGROUND_SHEET";
		backgroundOpts = NK_WINDOW_BACKGROUND | NK_WINDOW_NO_INPUT;
		
		windowOpts = NK_WINDOW_NO_SCROLLBAR;
		windowTitle = "GAME_MENU";
		
		audioConfig = new AudioConfigUI(audio);
		audioWindowTitle = "AUDIO_GAME_SETTINGS";
		audioWindowOpts = NK_WINDOW_BORDER;
	}
	
	@Override
	public void onRegister(RegisterableGame game) {
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
	public void onRender(Window window, Scene scene) {
		prepare(context, window);
	}

	@Override
	public void prepare(NkContext context, Window window) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);

			GLFW.glfwGetFramebufferSize(window.getId(), w, h);
			int displayWidth = w.get(0);
			int displayHeight = h.get(0);

			// TODO Move this Style stuff to some GameUIStyle class or at least into the register method to only perform it once
			NkStyle style = context.style(); 
			NkColor background = NkColor.malloc(stack);
			// Set background to slightly transparent layer
			background.r((byte) 0).g((byte) 0).b((byte) 0).a((byte) 128);
			style.window().fixed_background().data().color().set(background);

			NkColor text = NkColor.malloc(stack);
			text.r((byte) 255).g((byte) 255).b((byte) 255).a((byte) 255);
			style.text().color().set(text);

			NkColor disabledItemColor = NkColor.malloc(stack);
			disabledItemColor.r((byte) 40).g((byte) 40).b((byte) 40).a((byte) 255);
			NkStyleItem disabledStyleItem = nk_style_item_color(disabledItemColor, NkStyleItem.malloc(stack));
			NkColor disabledTextColor = NkColor.malloc(stack);
			disabledTextColor.r((byte) 60).g((byte) 60).b((byte) 60).a((byte) 255);
			NkStyleButton disabledStyleButton = NkStyleButton.malloc(stack).set(style.button());
			disabledStyleButton.normal(disabledStyleItem);
			disabledStyleButton.active(disabledStyleItem);
			disabledStyleButton.hover(disabledStyleItem);
			disabledStyleButton.border_color(disabledTextColor);
			disabledStyleButton.text_background(disabledTextColor);
			disabledStyleButton.text_normal(disabledTextColor);
			disabledStyleButton.text_active(disabledTextColor);
			disabledStyleButton.text_hover(disabledTextColor);

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
				glfwSetInputMode(window.getId(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
			}
			
			backgroundPosition.x(0).y(0).w(displayWidth).h(displayHeight);
			if (nk_begin(context, backgroundTitle, backgroundPosition, backgroundOpts)) {
				
			}
			nk_end(context);
			
			background.r((byte) 255).g((byte) 0).b((byte) 0).a((byte) 0);
			style.window().fixed_background().data().color().set(background);
			
			position.x((displayWidth * 0.5f) - 100).y((displayHeight * 0.5f) - 100).w(200).h(45 * 6);
			if (nk_begin(context, windowTitle, position, windowOpts)) {
				nk_layout_row_dynamic(context, 35, 1);
				if (nk_button_label_styled(context, disabledStyleButton, "Save Game")) {
					// TODO Save games not implemented
				}

				if (nk_button_label_styled(context, loadGameEnabled ? style.button() : disabledStyleButton, "Load Game") && loadGameEnabled) {
					// TODO No saved games
				}

				if (nk_button_label(context, "Options")) {
					showOptions(context);
				}

				if (nk_button_label(context, "Quit to Title")) {
					loadTitleScene();
				}
				
				if (nk_button_label(context, "Quit to Desktop")) {
					GLFW.glfwSetWindowShouldClose(window.getId(), true);
				}
				
				if (nk_button_label(context, "Return to Game")) {
					nk_window_show(context, backgroundTitle, NK_HIDDEN);
					nk_window_show(context, windowTitle, NK_HIDDEN);
					nk_window_set_focus(context, windowTitle);
					glfwSetInputMode(window.getId(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);
				}
			}
			nk_end(context);
			
			background.r((byte) 64).g((byte) 64).b((byte) 64).a((byte) 255);
			style.window().fixed_background().data().color().set(background);

			text.r((byte) 255).g((byte) 255).b((byte) 255).a((byte) 255);
			style.text().color().set(text);
			
			audioPosition.x((displayWidth * 0.5f) - 200).y(300).w(400).h(230);
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
			
//			AvoLog.log().debug("Main window \tclosed: {} hidden: {}", nk_window_is_closed(context, windowTitle), nk_window_is_hidden(context, windowTitle));
//			AvoLog.log().debug("Audio window \tclosed: {} hidden: {}", nk_window_is_closed(context, audioWindowTitle), nk_window_is_hidden(context, audioWindowTitle));
//			AvoLog.log().debug("Active \tmain: {} audio: {}", nk_window_is_active(context, windowTitle), nk_window_is_active(context, audioWindowTitle));
		}
	}

	private void loadTitleScene() {
		// Not implemented
	}
	
	private void showOptions(NkContext context) {
		nk_window_show(context, audioWindowTitle, NK_SHOWN);
		nk_window_show(context, windowTitle, NK_HIDDEN);
		nk_window_set_focus(context, audioWindowTitle);
	}

	@Override
	public void onCleanup() {
//		backgroundPosition.free();
		position.free();
		audioPosition.free();
		audioConfig.cleanup();
	}

}
