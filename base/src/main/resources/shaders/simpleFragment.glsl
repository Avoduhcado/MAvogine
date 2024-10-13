#version 330 core

in vec3 vertPosition;
in vec3 vertNormal;
in vec2 vertTextureCoordinates;

uniform vec3 viewPosition;

uniform vec3 lightPosition;
uniform vec3 lightColor;

uniform int hasTexture;
uniform vec3 objectColor;
uniform sampler2D objectTexture;

out vec4 fragColor;

void main() {
	
	// Ambient
	vec3 ambient = 0.3f * lightColor;
	
	// Diffuse
	vec3 normal = normalize(vertNormal);
	vec3 lightDirection = normalize(lightPosition - vertPosition);
	float diffuseStrength = max(dot(normal, lightDirection), 0.0);
	vec3 diffuse = lightColor * diffuseStrength;
	
	// Specular
	float specularStrength = 0.5;
	vec3 viewDirection = normalize(viewPosition - vertPosition);
	vec3 reflectDirection = reflect(-lightDirection, normal);
	float spec = pow(max(dot(viewDirection, reflectDirection), 0.0), 16);
	vec3 specular = specularStrength * spec * lightColor;
	
	vec3 result = (ambient + diffuse + specular);
	if (hasTexture == 1) {
		result *= texture(objectTexture, vertTextureCoordinates).xyz;
	} else {
		result *= objectColor;
	}
	fragColor = vec4(result, 1.0);
	
}