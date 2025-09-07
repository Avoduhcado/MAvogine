#version 330 core

const int MAX_WEIGHTS = 4;
const int MAX_BONES = 150;

layout (location=0) in vec3 position;
layout (location=1) in vec3 normal;
layout (location=4) in vec2 textureCoordinates;
layout (location=5) in vec4 boneWeights;
layout (location=6) in ivec4 boneIndices;

uniform mat4 projection;
uniform mat4 view;
uniform mat4 model;
uniform mat4 normalMatrix;

uniform mat4 boneMatrices[MAX_BONES];

out vec3 vertPosition;
out vec3 vertNormal;
out vec2 vertTextureCoordinates;

void main() {
	
	vec4 initPosition = vec4(0, 0, 0, 0);
	vec4 initNormal = vec4(0, 0, 0, 0);
	
	int count = 0;
	for (int i = 0; i < MAX_WEIGHTS; i++) {
		float weight = boneWeights[i];
		if (weight > 0) {
			count++;
			int boneIndex = boneIndices[i];
			vec4 tmpPos = boneMatrices[boneIndex] * vec4(position, 1.0);
			initPosition += weight * tmpPos;
			
			vec4 tmpNormal = boneMatrices[boneIndex] * vec4(normal, 0.0);
			initNormal += weight * tmpNormal;
		}
	}
	if (count == 0) {
		initPosition = vec4(position, 1.0);
		initNormal = vec4(normal, 0.0);
	}
	
	mat4 modelViewMatrix = view * model;
	vec4 mvPosition = modelViewMatrix * initPosition;
	gl_Position = projection * mvPosition;
	
	// Transform outs to view space
	vertPosition = vec3(mvPosition) / mvPosition.w;
//	vertNormal = mat3(normalMatrix) * normal;
	vertNormal = mat3(transpose(inverse(view * model))) * initNormal.xyz;
	vertTextureCoordinates = textureCoordinates;
	
}