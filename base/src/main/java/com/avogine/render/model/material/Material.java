package com.avogine.render.model.material;

/**
 *
 */
public sealed interface Material permits SimpleMaterial, PBRMaterial, CustomMaterial {
	
}
