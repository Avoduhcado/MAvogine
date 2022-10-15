#version 330 core

in vec2 vertTextureCoordinates;

uniform int isTextured;
uniform sampler2D sampleTexture;
uniform vec3 color;

out vec4 fragColor;

void main() {

	vec4 outColor;
	if (isTextured == 1) {
		outColor = texture(sampleTexture, vertTextureCoordinates);
	} else {
		outColor = vec4(color, 1.0);
	}
	fragColor = outColor;

}