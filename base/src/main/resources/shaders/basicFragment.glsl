#version 330 core

in vec2 vertTextureCoordinates;

uniform sampler2D sampleTexture;

out vec4 fragColor;

void main() {

	vec4 textureColor = texture(sampleTexture, vertTextureCoordinates);
	fragColor = textureColor;

}