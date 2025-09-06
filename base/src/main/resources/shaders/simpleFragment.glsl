#version 330 core

in vec3 vertPosition;
in vec3 vertNormal;
in vec2 vertTextureCoordinates;

uniform vec3 lightPosition;
uniform vec3 lightColor;

uniform sampler2D diffuseMap;
uniform sampler2D specularMap;
uniform float specularFactor;

out vec4 fragColor;

void main() {
	
	// Ambient
	float ambientStrength = 0.6;
	vec3 ambient = (lightColor * ambientStrength) * texture(diffuseMap, vertTextureCoordinates).rgb;
	
	// Diffuse
	vec3 normal = normalize(vertNormal);
	vec3 lightDirection = normalize(lightPosition - vertPosition);
	float diffuseStrength = max(dot(normal, lightDirection), 0.0);
	vec3 diffuse = lightColor * diffuseStrength * texture(diffuseMap, vertTextureCoordinates).rgb;
	
	// Specular
	vec3 viewDirection = normalize(-vertPosition);
	vec3 reflectDirection = reflect(-lightDirection, normal);
	float specularStrength = pow(max(dot(viewDirection, reflectDirection), 0.0), specularFactor);
	vec3 specular = lightColor * specularStrength * texture(specularMap, vertTextureCoordinates).rgb;
	
	vec3 result = ambient + diffuse + specular;
	fragColor = vec4(result, 1.0);
	
}