#version 300 es

precision mediump float;

uniform sampler2D nuklearTexture;

in vec2 Frag_UV;
in vec4 Frag_Color;

out vec4 Out_Color;

void main() {
	Out_Color = Frag_Color * texture(nuklearTexture, Frag_UV.st);
}