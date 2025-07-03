package com.avogine.render.opengl.model.util;

import java.util.*;
import java.util.stream.Collectors;

import org.lwjgl.assimp.Assimp;

import com.avogine.render.model.Material;
import com.avogine.render.opengl.image.util.TextureCache;
import com.avogine.render.opengl.model.*;
import com.avogine.render.opengl.model.mesh.*;
import com.avogine.render.util.assimp.AssimpModelLoader;

/**
 *
 */
public class ModelLoader extends AssimpModelLoader {

	private static final int LOADER_FLAGS = Assimp.aiProcess_GenSmoothNormals | Assimp.aiProcess_JoinIdenticalVertices |
			Assimp.aiProcess_Triangulate | Assimp.aiProcess_FixInfacingNormals | Assimp.aiProcess_CalcTangentSpace | Assimp.aiProcess_LimitBoneWeights |
			Assimp.aiProcess_GenBoundingBoxes;
	
	private ModelLoader() {
		super();
	}
	
	/**
	 * @param id
	 * @param modelPath
	 * @param textureCache
	 * @return an {@link AnimatedModel} loaded from the given modelPath.
	 * @throws IllegalStateException if the model file could not be opened.
	 */
	public static AnimatedModel loadAnimatedModel(String id, String modelPath, TextureCache textureCache) {
		ModelData modelData = loadModel(modelPath, textureCache, LOADER_FLAGS);
		
		Map<Material, List<AnimatedMesh>> materialMeshMap = modelData.materialMeshMap().entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().stream().map(AnimatedMesh::new).toList()));
		return new AnimatedModel(id, materialMeshMap, modelData.animations());
	}
	
	/**
	 * @param id 
	 * @param modelPath
	 * @param textureCache 
	 * @return a {@link StaticModel} loaded from the given modelPath.
	 * @throws IllegalStateException if the model file could not be opened.
	 */
	public static StaticModel loadModel(String id, String modelPath, TextureCache textureCache) {
		ModelData modelData = loadModel(modelPath, textureCache, LOADER_FLAGS | Assimp.aiProcess_PreTransformVertices);
		
		Map<Material, List<StaticMesh>> materialMeshMap = modelData.materialMeshMap().entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().stream().map(StaticMesh::new).toList()));
		return new StaticModel(id, materialMeshMap);
	}
	
}
