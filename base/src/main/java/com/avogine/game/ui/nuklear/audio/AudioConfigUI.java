package com.avogine.game.ui.nuklear.audio;

import static org.lwjgl.nuklear.Nuklear.*;

import java.util.List;
import java.util.stream.Collectors;

import org.lwjgl.nuklear.*;

import com.avogine.io.Audio;

/**
 *
 */
public class AudioConfigUI {

	private static final String SYSTEM_DEFAULT = "System Default";
	private static final String OPENAL_PREFIX = "OpenAL Soft on ";
	
	private final Audio audio;

	private final int[] volume = new int[1];
	private int deviceSelection;
	private List<String> deviceList;
	private String deviceListString;
	
	private NkVec2 comboBounds;
	
//	private int[] textLen = { 0 };
//	private String editText = "Hello";
//	
//	private NkPluginFilterI asciiFilter;
	
	/**
	 * @param audio 
	 * 
	 */
	public AudioConfigUI(Audio audio) {
		this.audio = audio;
		
		setUIVolume(audio.getListenerVolume());
		deviceList = audio.enumerateAudioDevices();
		
		deviceListString = SYSTEM_DEFAULT + "\0" + deviceList.stream()
			.map(s -> {
				if (s.startsWith(OPENAL_PREFIX)) {
					return s.substring(OPENAL_PREFIX.length());
				}
				return s;
			})
			.collect(Collectors.joining("\0"));
		deviceSelection = deviceList.indexOf(audio.getDeviceSpecifier()) + 1;
		
		comboBounds = NkVec2.create().set(400, 400);
		
//		asciiFilter = NkPluginFilter.create(Nuklear::nnk_filter_ascii);
	}

	/**
	 * @param context
	 */
	public void layout(NkContext context) {
		nk_layout_row_dynamic(context, 25, 1);
		nk_label(context, "Volume:", NK_TEXT_LEFT);
		float previousVolume = volume[0];
		nk_property_int(context, "Master:", 0, volume, 100, 1, 0.5f);
		if (previousVolume != volume[0]) {
			changeVolume();
		}

		//				ByteBuffer buffer = stack.calloc(256);
		//				int length = memASCII(editText, false, buffer);
		//				IntBuffer len = stack.ints(length);
		//				nk_edit_string(context, NK_EDIT_SIMPLE, buffer, len, 128, asciiFilter);
		//				editText = memASCII(buffer, len.get(0));

		nk_label(context, "Playback Device:", NK_TEXT_LEFT);
		int currentSelection = deviceSelection;
		deviceSelection = nk_combo_string(context, deviceListString, deviceSelection, deviceList.size() + 1, 25, comboBounds);
		if (currentSelection != deviceSelection) {
			changeDevice();
		}
	}
	
	private void changeVolume() {
		audio.setListenerVolume(getALVolume());
	}
	
	private void changeDevice() {
		if (deviceSelection == 0) {
			audio.changeDevice(null);
		} else {
			audio.changeDevice(deviceList.get(deviceSelection - 1));
		}
	}
	
	private float getALVolume() {
		return ((float) volume[0]) / 100.0f;
	}
	
	private void setUIVolume(float alVolume) {
		this.volume[0] = (int) (alVolume * 100);
	}
	
	/**
	 * 
	 */
	public void cleanup() {
		comboBounds.free();
	}

}
