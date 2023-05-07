package com.avogine.io;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.openal.ALC11.ALC_ALL_DEVICES_SPECIFIER;

import java.nio.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import org.lwjgl.openal.*;
import org.lwjgl.system.MemoryUtil;

import com.avogine.audio.loader.AudioCache;
import com.avogine.logging.AvoLog;
import com.avogine.util.PropertiesUtil;
import com.avogine.util.resource.ResourceFileReader;

/**
 *
 */
public class Audio {

	private long device;
	private long context;
	
	private AudioProperties properties;
	
	private String defaultInitializedDevice;

	/**
	 * 
	 */
	public Audio() {
	}
	
	/**
	 * Initialize the OpenAL subsystem:
	 * <ul>
	 * <li>Open the default device.
	 * <li>Create the capabilities for that device.
	 * <li>Create a sound context, like the OpenGL one, and set it as the current one.
	 * </ul>
	 */
	public void init() {
		enumerateAudioDevices();
		
		device = alcOpenDevice((ByteBuffer) null);
		if (device == MemoryUtil.NULL) {
			throw new IllegalStateException("Failed to open the default OpenAL device.");
		}
		defaultInitializedDevice = alcGetString(0, ALC11.ALC_DEFAULT_ALL_DEVICES_SPECIFIER);
		context = alcCreateContext(device, (IntBuffer) null);
		if (context == MemoryUtil.NULL) {
			throw new IllegalStateException("Failed to create OpenAL context.");
		}
		alcMakeContextCurrent(context);
		ALCCapabilities deviceCaps = ALC.createCapabilities(device);
		AL.createCapabilities(deviceCaps);
		
		initProperties();
		
		alDistanceModel(properties.attenuationModel);
		alListenerf(AL_GAIN, properties.listenerGain);

		// With this disabled, if a device disconnects sources won't be stopped and can hopefully just immediately start playing on another reconnected device.
		// This might cause issues?
		alDisable(SOFTXHoldOnDisconnect.AL_STOP_SOURCES_ON_DISCONNECT_SOFT);

		configureDeviceReopen();
		configureSoftEvents();
	}
	
	private List<String> enumerateAudioDevices() {
		if (alcIsExtensionPresent(0, "ALC_ENUMERATION_EXT")) {
			return ALUtil.getStringList(0, ALC_ALL_DEVICES_SPECIFIER);
		}
		return List.of();
	}
	
	private void configureDeviceReopen() {
		if (alcIsExtensionPresent(device, "ALC_SOFT_reopen_device")) {
			// TODO Provide an option for a user to select their default device, ie. if set to "System Default" then run this code, if set to a specific device do not.
			AvoLog.log().debug("Scheduling default audio device poller.");
			
			var timer = new java.util.Timer();
			timer.scheduleAtFixedRate(new TimerTask() {
				private int reconnectTry;
				
				@Override
				public void run() {
					// For some reason we need to first poll for all devices before the system default will actually be detected if it has changed.
					ALUtil.getStringList(0, ALC_ALL_DEVICES_SPECIFIER);
					
					var defaultDevice = alcGetString(0, ALC11.ALC_DEFAULT_ALL_DEVICES_SPECIFIER);
					if (!defaultDevice.equalsIgnoreCase(defaultInitializedDevice)) {
						AvoLog.log().debug("Default audio device has changed, reopening the device.");
						boolean reopenSuccess = SOFTReopenDevice.alcReopenDeviceSOFT(device, (ByteBuffer) null, (IntBuffer) null);
						if (reopenSuccess) {
							reconnectTry = 0;
							defaultInitializedDevice = defaultDevice;
						} else {
							reconnectTry++;
							if (reconnectTry >= 3) {
								reconnectTry = 0;
								defaultInitializedDevice = defaultDevice;
							}
						}
					}
				}
			}, new Date(), TimeUnit.SECONDS.toMillis(1));
		}
	}

	private void configureSoftEvents() {
		if (alIsExtensionPresent("AL_SOFT_events")) {
			SOFTEvents.alEventCallbackSOFT((eventType, object, param, length, message, userParam) -> {
				switch (eventType) {
				case SOFTEvents.AL_EVENT_TYPE_DISCONNECTED_SOFT -> {
					AvoLog.log().debug("Device disconnected, attempting to reopen...");
					ALUtil.getStringList(0, ALC_ALL_DEVICES_SPECIFIER);
					alcGetString(0, ALC11.ALC_DEFAULT_ALL_DEVICES_SPECIFIER);
					SOFTReopenDevice.alcReopenDeviceSOFT(device, (ByteBuffer) null, (IntBuffer) null);
				}
				case SOFTEvents.AL_EVENT_TYPE_SOURCE_STATE_CHANGED_SOFT ->
					AvoLog.log().debug("Source state changed");
				case SOFTEvents.AL_EVENT_TYPE_BUFFER_COMPLETED_SOFT ->
					AvoLog.log().debug("Buffer completed?");
				default ->
					System.out.println("Event of type: " + eventType);
				}
			}, (ByteBuffer) null);
			int[] eventTypes = new int[] { SOFTEvents.AL_EVENT_TYPE_DISCONNECTED_SOFT };
			SOFTEvents.alEventControlSOFT(eventTypes, true);
		}
	}
	
	private void initProperties() {
		Properties prop = ResourceFileReader.readPropertiesFile("audio");
		properties = new AudioProperties(
				PropertiesUtil.getInteger(prop, "attenuationModel", AL_INVERSE_DISTANCE_CLAMPED),
				PropertiesUtil.getFloat(prop, "listenerGain", 1.0f));
	}

	/**
	 * 
	 */
	public void cleanup() {
		AudioCache.getInstance().cleanup();
		if (context != MemoryUtil.NULL) {
			alcDestroyContext(context);
		}
		if (device != MemoryUtil.NULL) {
			alcCloseDevice(device);
		}
	}
	
	/**
	 * Data structure for any customizable options to use while initializing the audio system.
	 * @param attenuationModel Value applied to {@link AL10#alDistanceModel(int)}.
	 * @param listenerGain The gain setting of the listener. Pseudo max volume.
	 */
	public record AudioProperties(int attenuationModel, float listenerGain) {
	}

}
