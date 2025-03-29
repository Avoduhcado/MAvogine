package com.avogine.audio;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.openal.ALC11.*;

import java.nio.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import org.lwjgl.openal.*;
import org.lwjgl.system.MemoryUtil;

import com.avogine.audio.data.*;
import com.avogine.logging.AvoLog;

/**
 * XXX For some reason ALC_DEVICE_SPECIFIER and ALC_DEFAULT_DEVICE_SPECIFIER only ever return the start of the device specifier, ie. OpenAL Soft, 
 * but it appears to be safe to just use ALC_ALL_DEVICES_SPECIFIER and ALC_DEFAULT_ALL_DEVICES_SPECIFIER either as a string list for all
 * values or as just alGetString for the first value. The _ALL_ constants appear to be from an extension, so more caution may need to be taken.
 */
public class Audio {

	private final List<SoundBuffer> soundBuffers;
	private final Map<String, SoundSource> soundSourceCache;
	
	private SoundListener listener;
	
	private long device;
	private long context;
	
	private ALCapabilities caps;

	private String currentDevice;
	
	private AudioProperties properties;
	
	private final List<SOFTEventProc> softEventCallbacks;
	private Timer defaultDeviceReopenTimer;

	/**
	 * Instantiate a new Audio system and initialize configuration properties from disk.
	 */
	public Audio() {
		soundBuffers = new ArrayList<>();
		soundSourceCache = new HashMap<>();
		
		properties = new AudioProperties();
		
		softEventCallbacks = new ArrayList<>();
	}
	
	/**
	 * Initialize the OpenAL subsystem:
	 * <ul>
	 * <li>Open a device.
	 * <li>Create the capabilities for that device.
	 * <li>Create a sound context, like the OpenGL one, and set it as the current one.
	 * </ul>
	 */
	public void init() {
		List<String> deviceList = enumerateAudioDevices();
		if (properties.defaultDevice != null && !deviceList.contains(properties.defaultDevice)) {
			device = alcOpenDevice((String) null);
		} else {
			device = alcOpenDevice(properties.defaultDevice);
		}
		if (device == MemoryUtil.NULL) {
			throw new IllegalStateException("Failed to open OpenAL device.");
		}
		
		var deviceCaps = ALC.createCapabilities(device);
		
		if (deviceCaps.OpenALC11) {
			currentDevice = alcGetString(device, ALC_ALL_DEVICES_SPECIFIER);
		}
		
		context = alcCreateContext(device, (IntBuffer) null);
		if (context == MemoryUtil.NULL) {
			throw new IllegalStateException("Failed to create OpenAL context.");
		}
		
		alcMakeContextCurrent(context);
		
		caps = AL.createCapabilities(deviceCaps, MemoryUtil::memCallocPointer);

		setListenerVolume(properties.listenerGain);
		
		if (deviceCaps.ALC_SOFT_reopen_device) {
			configureDeviceReopen();
		}
		if (caps.AL_SOFT_events) {
			configureSoftEvents();
			// With this disabled, if a device disconnects sources won't be stopped and can hopefully just immediately start playing on another reconnected device.
			// This might cause issues?
			alDisable(SOFTXHoldOnDisconnect.AL_STOP_SOURCES_ON_DISCONNECT_SOFT);
		}
	}
	
	/**
	 * @param soundBuffer
	 */
	public void addSoundBuffer(SoundBuffer soundBuffer) {
		soundBuffers.add(soundBuffer);
	}
	
	/**
	 * @param name
	 * @param soundSource
	 */
	public void addSoundSource(String name, SoundSource soundSource) {
		soundSourceCache.put(name, soundSource);
	}
	
	/**
	 * @param name
	 * @return
	 */
	public SoundSource removeSoundSource(String name) {
		return soundSourceCache.remove(name);
	}
	
	/**
	 * @param name
	 */
	public void playSoundSource(String name) {
		SoundSource soundSource = getSoundSource(name);
		if (soundSource != null && !soundSource.isPlaying()) {
			soundSource.play();
		}
	}
	
	/**
	 * 
	 */
	public void clearSources() {
		soundSourceCache.values().forEach(SoundSource::cleanup);
		soundSourceCache.clear();
	}
	
	/**
	 * 
	 */
	public void clearBuffers() {
		soundBuffers.forEach(SoundBuffer::cleanup);
		soundBuffers.clear();
	}
	
	/**
	 * Free all {@link SoundBuffer}s and {@link SoundSource}s, then destroy the context
	 * and close the device if they were properly allocated.
	 */
	public void cleanup() {
		if (defaultDeviceReopenTimer != null) {
			defaultDeviceReopenTimer.cancel();
		}
		
		clearSources();
		clearBuffers();
		
		softEventCallbacks.forEach(SOFTEventProc::free);
		
		alcMakeContextCurrent(MemoryUtil.NULL);
		if (caps != null) {
			MemoryUtil.memFree(caps.getAddressBuffer());
		}
		alcDestroyContext(context);
		alcCloseDevice(device);
	}
	
	/**
	 * @return the soundBuffers
	 */
	public List<SoundBuffer> getSoundBuffers() {
		return soundBuffers;
	}
	
	/**
	 * @param name
	 * @return a cached {@link SoundSource} with the given name.
	 */
	public SoundSource getSoundSource(String name) {
		// XXX Handle missing values, should this attempt to computeIfAbsent?
		return soundSourceCache.get(name);
	}
	
	/**
	 * @param model
	 */
	public void setAttenuationModel(int model) {
		alDistanceModel(model);
	}
	
	/**
	 * @return the listener
	 */
	public SoundListener getListener() {
		return listener;
	}
	
	/**
	 * @param listener the listener to set
	 */
	public void setListener(SoundListener listener) {
		this.listener = listener;
	}
	
	/**
	 * @return the address of the currently open device.
	 */
	public long getDevice() {
		return device;
	}
	
	/**
	 * The listener's gain acts as the max volume.
	 * @return the gain value of the listener.
	 */
	public float getListenerVolume() {
		return alGetListenerf(AL_GAIN);
	}
	
	/**
	 * @param volume The value to set the Listener's gain to.
	 */
	public void setListenerVolume(float volume) {
		alListenerf(AL_GAIN, volume);
		properties = new AudioProperties(properties.defaultDevice, volume);
	}
	
	/**
	 * @return the name of the currently opened device, or null if the properties specify to use the "System Default" device.
	 */
	public String getDeviceSpecifier() {
		if (properties.defaultDevice == null) {
			return null;
		}
		return alcGetString(device, ALC_ALL_DEVICES_SPECIFIER);
	}
	
	/**
	 * @param deviceSpecifier
	 */
	public void changeDevice(String deviceSpecifier) {
		properties = new AudioProperties(deviceSpecifier, properties.listenerGain);
		if (ALC.getCapabilities().ALC_SOFT_reopen_device) {
			SOFTReopenDevice.alcReopenDeviceSOFT(device, deviceSpecifier, (IntBuffer) null);
			currentDevice = alcGetString(device, ALC_ALL_DEVICES_SPECIFIER);
			configureDeviceReopen();
		}
	}

	/**
	 * @return a list of all available device specifiers.
	 */
	public List<String> enumerateAudioDevices() {
		return ALUtil.getStringList(0, ALC_ALL_DEVICES_SPECIFIER);
	}
	
	private void configureDeviceReopen() {
		if (defaultDeviceReopenTimer != null) {
			defaultDeviceReopenTimer.cancel();
			AvoLog.log().info("Cancelled default audio device poller.");
		}
		if (properties.defaultDevice != null) {
			return;
		}
		AvoLog.log().info("Scheduling default audio device poller.");

		defaultDeviceReopenTimer = new Timer("OpenAL-Device-Reopen_Timer", true);
		defaultDeviceReopenTimer.scheduleAtFixedRate(new DefaultDeviceReopenTask(), new Date(), TimeUnit.SECONDS.toMillis(1));
	}

	private void configureSoftEvents() {
		SOFTEventProc callback = SOFTEventProc.create((eventType, object, param, length, message, userParam) -> {
			switch (eventType) {
				case SOFTEvents.AL_EVENT_TYPE_DISCONNECTED_SOFT -> {
					if (ALC.getCapabilities().ALC_SOFT_reopen_device && properties.defaultDevice == null) {
						AvoLog.log().info("Audio device disconnected, attempting to reopen default...");
						SOFTReopenDevice.alcReopenDeviceSOFT(device, (ByteBuffer) null, (IntBuffer) null);
					} else {
						AvoLog.log().info("Audio device disconnected.");
					}
				}
				case SOFTEvents.AL_EVENT_TYPE_SOURCE_STATE_CHANGED_SOFT -> AvoLog.log().debug("Source state changed");
				case SOFTEvents.AL_EVENT_TYPE_BUFFER_COMPLETED_SOFT -> AvoLog.log().debug("Buffer completed?");
				default -> AvoLog.log().debug("Event of type: {}", eventType);
			}
		});
		SOFTEvents.alEventCallbackSOFT(callback, (ByteBuffer) null);
		int[] eventTypes = new int[] { SOFTEvents.AL_EVENT_TYPE_DISCONNECTED_SOFT };
		SOFTEvents.alEventControlSOFT(eventTypes, true);
		softEventCallbacks.add(callback);
	}
	
	private class DefaultDeviceReopenTask extends TimerTask {
		private static final int MAX_RECONNECTS = 3;
		private int reconnectAttempt;

		@Override
		public void run() {
			// Refresh the device list to see if any devices have changed.
			enumerateAudioDevices();
			var defaultDevice = alcGetString(0, ALC_DEFAULT_ALL_DEVICES_SPECIFIER);
			
			if (!defaultDevice.equalsIgnoreCase(currentDevice)) {
				AvoLog.log().debug("Default audio device has changed, reopening the device.");
				boolean reopenSuccess = SOFTReopenDevice.alcReopenDeviceSOFT(device, (ByteBuffer) null, (IntBuffer) null);
				if (reopenSuccess) {
					reconnectAttempt = 0;
					currentDevice = defaultDevice;
				} else {
					reconnectAttempt++;
					if (reconnectAttempt >= MAX_RECONNECTS) {
						reconnectAttempt = 0;
						currentDevice = defaultDevice;
					}
				}
			}
		}
	}
	
	/**
	 * Data structure for any customizable options to use while initializing the audio system.
	 * @param defaultDevice The default device to open when initializing OpenAL.
	 * Set to {@code null} to automatically open the default system device.
	 * @param listenerGain The gain setting of the listener. Pseudo max volume.
	 */
	public static record AudioProperties(String defaultDevice, float listenerGain) {
		/**
		 * Initialize a default {@link AudioProperties}.
		 */
		public AudioProperties() {
			this(null, 1.0f);
		}
	}

}
