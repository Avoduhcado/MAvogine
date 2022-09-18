#version 330 core

in vec3 vertPosition;
in vec3 vertNormal;

uniform vec3 lightPosition;
uniform vec3 lightColor;

uniform vec3 objectColor;

out vec4 fragColor;

void main() {
	
	// Ambient
	vec3 ambient = 0.1f * lightColor;
	
	// Diffuse
	vec3 normal = normalize(vertNormal);
	vec3 lightDirection = normalize(light.position - vertPosition);
	float diffuseStrength = max(dot(normal, lightDirection), 0.0);
	vec3 diffuse = lightColor * diffuseStrength;
	
	vec3 result = (ambient + diffuse) * objectColor;
	fragColor = vec4(result, 1.0);
	
}