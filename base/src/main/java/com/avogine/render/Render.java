package com.avogine.render;

/**
 *
 */
public abstract class Render {

	private static float anisotropicFiltering = 4f;

	private Render() {
	}

	/**
	 * @return the anisotropicFiltering
	 */
	public static float getAnisotropicFiltering() {
		return anisotropicFiltering;
	}

	/**
	 * @param anisotropicFiltering the anisotropicFiltering to set
	 */
	public static void setAnisotropicFiltering(float anisotropicFiltering) {
		Render.anisotropicFiltering = anisotropicFiltering;
	}

	/**
	 *
	 * @param anisotropicFiltering
	 */
	public record RenderPreferences(float anisotropicFiltering) {
		 
		public static RenderPreferences defaultPreferences = new RenderPreferences(4f);
		
		/**
		 * TODO Attempt to load saved prefs and fallback to default if no saved prefs are found
		 * @return the user's saved {@link RenderPreferences} or the default configuration if no saved preferences exist.
		 */
		public static RenderPreferences loadPrefs() {
			return defaultPreferences;
		}
	}
}
