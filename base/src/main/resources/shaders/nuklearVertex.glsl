#version 300 es

uniform mat4 projectionMatrix;

layout (location=0) in vec2 Position;
layout (location=1) in vec2 TexCoord;
layout (location=2) in vec4 Color;

out vec2 Frag_UV;
out vec4 Frag_Color;

void main() {
	Frag_UV = TexCoord;
	Frag_Color = Color;
	gl_Position = projectionMatrix * vec4(Position.xy, 0, 1);
}