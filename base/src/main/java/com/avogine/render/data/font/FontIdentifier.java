package com.avogine.render.data.font;

import java.util.Arrays;
import java.util.Objects;

public record FontIdentifier(String fontResource, float...sizes) {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(sizes);
		result = prime * result + Objects.hash(fontResource);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof FontIdentifier))
			return false;
		FontIdentifier other = (FontIdentifier) obj;
		return Objects.equals(fontResource, other.fontResource) && Arrays.equals(sizes, other.sizes);
	}

	@Override
	public String toString() {
		return "FontIdentifier [fontResource=" + fontResource + ", sizes=" + Arrays.toString(sizes) + "]";
	}

}
