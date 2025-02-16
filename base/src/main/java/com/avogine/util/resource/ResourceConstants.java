package com.avogine.util.resource;

/**
 * Constant values for different resource directory paths relative to the /resources directory.
 */
public enum ResourceConstants {
	
	/** The root directory for all model resources. */
	MODELS("/models/"),
	/** The root directory for all shader resources. */
	SHADERS("/shaders/"),
	/** The root directory for all texture resources. */
	TEXTURES("/textures/"),
	/** The root directory for all font resources. */
	FONTS("/fonts/"),
	/** The root directory for all sound resources. */
	SOUNDS("/sounds/");

	private String directory;

	/**
	 * @return the root directory for this resource.
	 */
	public String getDirectory() {
		return directory;
	}

	/**
	 * Return a relative file path starting from this {@link ResourceConstants} directory and joining each sub path element with a {@code /}.
	 * @param paths A list of sub path elements pointing to a specific file location.
	 * @return a relative file path starting from this {@link ResourceConstants} directory and joining each sub path element with a {@code /}.
	 */
	public String with(String...paths) {
		return directory + String.join("/", paths);
	}

	private ResourceConstants(String directory) {
		this.directory = directory;
	}

}
