#version 330 core

in vec3 vertPosition;
in vec3 vertNormal;
in vec2 vertTextureCoords;

uniform vec3 lightPosition;
uniform vec3 lightColor;

uniform sampler2D diffuseTexture;
uniform int hasNormalMap;
uniform sampler2D normalMap;

out vec4 fragColor;

vec4 calcLightColor(vec3 lightColor, float lightIntensity, vec3 position, vec3 toLightDir, vec3 normal) {
	vec4 diffuseColor = vec4(0, 0, 0, 0);
	vec4 specularColor = vec4(0, 0, 0, 0);

	// Diffuse Light
	float diffuseFactor = max(dot(normal, toLightDir), 0.0);
	diffuseColor = vec4(lightColor, 1.0) * lightIntensity * diffuseFactor;

	// Specular Light
	vec3 cameraDirection = normalize(-position);
	vec3 fromLightDir = -toLightDir;
	vec3 reflectedLight = normalize(reflect(fromLightDir , normal));
	float shininess = 0.0; // From Material uniform
	float specularFactor = max(dot(cameraDirection, reflectedLight), shininess);
	float specularPower = 1.0; // From Light uniform
	specularFactor = pow(specularFactor, specularPower);
	float materialReflectance = 0.0; // From Material uniform
	vec4 materialSpecular = vec4(0.0);
	specularColor = materialSpecular * lightIntensity * specularFactor * materialReflectance * vec4(lightColor, 1.0);
	
	return (diffuseColor + specularColor);
}

vec4 calcPointLight(vec3 lightPosition, vec3 lightColor, float lightIntensity, vec3 position, vec3 normal) {
	vec3 lightDirection = lightPosition - position;
	vec3 toLightDir = normalize(lightDirection);
	vec4 calcedlightColor = calcLightColor(lightColor, lightIntensity, position, toLightDir, normal);

	// Apply Attenuation
	float distance = length(lightDirection);
	float attenuationInv = 1;//light.att.constant + light.att.linear * distance + light.att.exponent * distance * distance;
	return calcedlightColor / attenuationInv;
}

void main() {

	// Normal map
	vec3 normal = normalize(vertNormal);
	if (hasNormalMap > 0) {
//		normal = texture(normalMap, vertTextureCoords).rgb;
//		normal = normalize(normal * 2.0 - 1.0);
//		normal = (vertModelMatrix * vec4(normal, 0.0)).xyz;
	}
//	normal = vertNormalMatrix * normal;
//	vec3 normalUV = texture(normalMap, vertTextureCoords).rgb;
//	vec3 mappedNormal = vertNormal * normalUV;
	
	// Ambient
	vec4 ambient = vec4(0.1f * lightColor, 1.0);
	
	// Diffuse
	vec4 diffuse = calcPointLight(lightPosition, lightColor, 1, vertPosition, normal);
//	vec3 lightDirection = normalize(lightPosition - vertPosition);
//	float diffuseStrength = max(dot(normal, lightDirection), 0.0);
//	vec3 diffuse = lightColor * diffuseStrength;
	
	vec4 result = (ambient + diffuse) * texture(diffuseTexture, vertTextureCoords);
//	vec4 result = vec4(diffuse, 0.0) * texture(diffuseTexture, vertTextureCoords);
//	vec4 result = vec4(diffuseStrength, 0, 0, 1);
//	vec4 result = texture(normalMap, vertTextureCoords);
//	vec4 result = vec4(ambient, 1.0) * texture(diffuseTexture, vertTextureCoords);
	
	fragColor = result;
//	fragColor = vec4(normal, 1.0);

}