#version 330 core

const int MAX_WEIGHTS = 4;
const int MAX_BONES = 150;

layout (location=0) in vec3 position;
layout (location=1) in vec3 normal;
layout (location=4) in vec2 textureCoordinates;
layout (location=5) in vec4 boneWeights;
layout (location=6) in ivec4 boneIndices;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;
uniform mat4 normalMatrix;
uniform mat4 boneMatrices[MAX_BONES];

out vec3 vertPosition;
out vec3 vertNormal;
out vec2 vertTextureCoordinates;

void main() {

	vec4 initPosition = vec4(0);
	vec4 initNormal = vec4(0);
	
	int count = 0;
	for (int i = 0; i < MAX_WEIGHTS; i++) {
		float weight = boneWeights[i];
		if (weight > 0) {
			count++;
			int boneIndex = boneIndices[i];
			vec4 tmpPosition = boneMatrices[boneIndex] * vec4(position, 1.0);
			initPosition += weight * tmpPosition;
			
			vec4 tmpNormal = boneMatrices[boneIndex] * vec4(normal, 0.0);
			initNormal += weight * tmpNormal;
		}
	}
	if (count == 0) {
		initPosition = vec4(position, 1.0);
		initNormal = vec4(normal, 0.0);
	}
	
	mat4 modelViewMatrix = viewMatrix * modelMatrix;
	mat4 mvPosition = modelViewMatrix * initPos;
	gl_Position = projectionMatrix * mvPosition;
	
	// Transform outs to view space
	vertPosition = mvPosition.xyz;
	vertNormal = normalize(modelViewMatrix * initNormal).xyz;
	vertTextureCoordinates = textureCoordinates;
	
}