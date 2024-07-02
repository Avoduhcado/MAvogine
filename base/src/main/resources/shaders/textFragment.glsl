#version 330 core

in vec2 vertTextureCoordinates;

uniform sampler2D fontTexture;
uniform vec4 textColor;

out vec4 fragColor;

void main() {

	fragColor = textColor * vec4(1.0, 1.0, 1.0, texture(fontTexture, vertTextureCoordinates).r);

}