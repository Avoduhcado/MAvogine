package com.avogine.render.opengl.model.util;

import static org.lwjgl.assimp.Assimp.*;

import java.nio.IntBuffer;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.joml.*;
import org.joml.Math;
import org.joml.primitives.AABBf;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import com.avogine.logging.AvoLog;
import com.avogine.render.model.animation.*;
import com.avogine.render.model.material.*;
import com.avogine.render.model.material.data.BlinnPhongData;
import com.avogine.render.model.mesh.data.MeshData;
import com.avogine.render.model.mesh.data.MeshData.AnimMeshData;
import com.avogine.render.opengl.model.*;
import com.avogine.render.opengl.model.mesh.*;
import com.avogine.render.opengl.texture.util.TextureCache;
import com.avogine.render.util.AssimpFileUtils;

/**
 *
 */
public class ModelLoader {
	/**
	 * Max number of bones a single mesh can support.
	 */
	public static final int MAX_BONES = 150;
	
	private static final Matrix4f IDENTITY_MATRIX = new Matrix4f();
	
	private record Bone(int boneId, String boneName, Matrix4f offsetMatrix) {}
	private record VertexWeight(int boneId, int vertexId, float weight) {}
	
	private ModelLoader() {}
	
	/**
	 * @param id
	 * @param modelPath
	 * @param textureCache
	 * @return a {@link StaticModel} loaded from the given path.
	 */
	public static StaticModel loadModel(String id, String modelPath, TextureCache textureCache) {
		AIScene aiScene = AssimpFileUtils.readSceneFromMemory(modelPath, aiProcess_GenSmoothNormals | aiProcess_JoinIdenticalVertices |
				aiProcess_Triangulate | aiProcess_FixInfacingNormals | aiProcess_CalcTangentSpace | aiProcess_LimitBoneWeights |
				aiProcess_GenBoundingBoxes | aiProcess_PreTransformVertices);
		
		List<SimpleMaterial> materials = loadMaterials(aiScene, modelPath, textureCache);
		
		List<AIMesh> aiMeshes = AssimpFileUtils.readMeshes(aiScene);
		List<Bone> bones = new ArrayList<>();
		List<StaticMesh> noMaterialMeshes = new ArrayList<>();
		Map<SimpleMaterial, List<StaticMesh>> materialMap = materials.stream()
				.collect(Collectors.toMap(
						Function.identity(), 
						_ -> new ArrayList<>()));
		for (AIMesh aiMesh : aiMeshes) {
			int materialIndex = aiMesh.mMaterialIndex();
			MeshData meshData = processMesh(aiMesh, bones);
			var mesh = new StaticMesh(meshData);

			if (materialIndex >= 0 && materialIndex < materialMap.size()) {
				materialMap.get(materials.get(materialIndex)).add(mesh);
			} else {
				noMaterialMeshes.add(mesh);
			}
		}
		if (!noMaterialMeshes.isEmpty()) {
			materialMap.put(new SimpleMaterial(), noMaterialMeshes);
		}

		aiReleaseImport(aiScene);

		return new StaticModel(id, materialMap);
	}
	
	/**
	 * TODO#57 convert modelPath to modelName, source modelName from a resource property that points to the file location and pass _that_ file location to the reader
	 * @param id 
	 * @param modelPath
	 * @param textureCache 
	 * @return a {@link AnimatedModel} loaded from the given path.
	 */
	public static AnimatedModel loadAnimatedModel(String id, String modelPath, TextureCache textureCache) {
		AIScene aiScene = AssimpFileUtils.readSceneFromMemory(modelPath, aiProcess_GenSmoothNormals | aiProcess_JoinIdenticalVertices |
				aiProcess_Triangulate | aiProcess_FixInfacingNormals | aiProcess_CalcTangentSpace | aiProcess_LimitBoneWeights |
				aiProcess_GenBoundingBoxes);
		
		List<SimpleMaterial> materials = loadMaterials(aiScene, modelPath, textureCache);

		List<AIMesh> aiMeshes = AssimpFileUtils.readMeshes(aiScene);
		List<Bone> bones = new ArrayList<>();
		List<AnimatedMesh> noMaterialMeshes = new ArrayList<>();
		Map<SimpleMaterial, List<AnimatedMesh>> materialMap = materials.stream()
				.collect(Collectors.toMap(
						Function.identity(), 
						_ -> new ArrayList<>()));
		for (AIMesh aiMesh : aiMeshes) {
			int materialIndex = aiMesh.mMaterialIndex();
			MeshData meshData = processMesh(aiMesh, bones);
			var mesh = new AnimatedMesh(meshData);

			if (materialIndex >= 0 && materialIndex < materialMap.size()) {
				materialMap.get(materials.get(materialIndex)).add(mesh);
			} else {
				noMaterialMeshes.add(mesh);
			}
		}
		if (!noMaterialMeshes.isEmpty()) {
			materialMap.put(new SimpleMaterial(), noMaterialMeshes);
		}

		List<AIAnimation> aiAnimations = AssimpFileUtils.readAnimations(aiScene);
		List<Animation> animations = new ArrayList<>();
		if (!aiAnimations.isEmpty()) {
			Node rootNode = buildNodesTree(aiScene.mRootNode(), null);
			Matrix4f globalInverseTransformation = toMatrix(aiScene.mRootNode().mTransformation()).invert();
			animations.addAll(processAnimations(aiScene, bones, rootNode, globalInverseTransformation));
		}

		aiReleaseImport(aiScene);

		return new AnimatedModel(id, materialMap, animations);
	}
	
	private static List<SimpleMaterial> loadMaterials(AIScene aiScene, String modelPath, TextureCache textureCache) {
		List<AIMaterial> aiMaterials = AssimpFileUtils.readMaterials(aiScene);
		String modelDirectory = modelPath.substring(0, modelPath.lastIndexOf('/') + 1);
		return aiMaterials.stream()
				.map(aiMaterial -> processSimpleMaterial(aiMaterial, modelDirectory, textureCache))
				.toList();
	}
	
	private static SimpleMaterial processSimpleMaterial(AIMaterial aiMaterial, String modelDirectory, TextureCache textureCache) {
		float[] specularFactor = new float[] { 0.0f };
		int[] pMax = new int[] { 1 };
		int result = aiGetMaterialFloatArray(aiMaterial, AI_MATKEY_SHININESS, aiTextureType_NONE, 0, specularFactor, pMax);
		if (result != aiReturn_SUCCESS) {
			AvoLog.log().info("No value for: {}.", AI_MATKEY_SHININESS);
		}
		
		AIString texturePath = AIString.create();
		String diffuseMapPath = processMaterialTexture(aiMaterial, aiTextureType_DIFFUSE, texturePath, modelDirectory, textureCache);
		String specularMapPath = processMaterialTexture(aiMaterial, aiTextureType_SPECULAR, texturePath, modelDirectory, textureCache);
		
		return new SimpleMaterial(new BlinnPhongData(diffuseMapPath, specularMapPath, specularFactor[0]));
	}
	
	@SuppressWarnings("unused")
	private static Material processPBRMaterial(AIMaterial aiMaterial, String modelDirectory, TextureCache textureCache) {
		AIString texturePath = AIString.create();
		String albedoTexture = processMaterialTexture(aiMaterial, aiTextureType_BASE_COLOR, texturePath, modelDirectory, textureCache);
		String normalTexture = processMaterialTexture(aiMaterial, aiTextureType_NORMALS, texturePath, modelDirectory, textureCache);
		String metallicTexture = processMaterialTexture(aiMaterial, aiTextureType_METALNESS, texturePath, modelDirectory, textureCache);
		String roughnessTexture = processMaterialTexture(aiMaterial, aiTextureType_DIFFUSE_ROUGHNESS, texturePath, modelDirectory, textureCache);
		String aoTexture = processMaterialTexture(aiMaterial, aiTextureType_AMBIENT_OCCLUSION, texturePath, modelDirectory, textureCache);
		
		return new PBRMaterial(albedoTexture, normalTexture, metallicTexture, roughnessTexture, aoTexture);
	}

	@SuppressWarnings("unused")
	private static Vector4f processMaterialColor(AIMaterial aiMaterial, String materialKeyColor, AIColor4D color) {
		int result = aiGetMaterialColor(aiMaterial, materialKeyColor, aiTextureType_NONE, 0, color);
		if (result == aiReturn_SUCCESS) {
			return new Vector4f(color.r(), color.g(), color.b(), color.a());
		}
		return SimpleMaterial.DEFAULT_COLOR;
	}
	
	private static String processMaterialTexture(AIMaterial aiMaterial, int textureType, AIString texturePath, String modelDirectory, TextureCache textureCache) {
		aiGetMaterialTexture(aiMaterial, textureType, 0, texturePath, (IntBuffer) null, null, null, null, null, null);
		String filePath = texturePath.dataString();
		if (filePath != null && !filePath.isBlank()) {
			textureCache.getTexture(modelDirectory + filePath); 
			return modelDirectory + filePath;
		}
		return null;
	}
	
	private static MeshData processMesh(AIMesh aiMesh, List<Bone> bones) {
		float[] positions = processVertices(aiMesh);
		float[] normals = processNormals(aiMesh);
		float[] tangents = processTangents(aiMesh);
		float[] bitangents = processBitangents(aiMesh);
		float[] textureCoordinates = processTextureCoordinates(aiMesh);
		int[] indices = processIndices(aiMesh);
		
		AnimMeshData boneWeights = processBones(aiMesh, bones);
		
		AABBf aabb = processAABB(aiMesh);
		int materialIndex = aiMesh.mMaterialIndex();
		
		return new MeshData(positions, normals, tangents, bitangents, textureCoordinates, boneWeights, indices, aabb, materialIndex);
	}
	
	private static float[] processVertices(AIMesh aiMesh) {
		AIVector3D.Buffer buffer = aiMesh.mVertices();
		var data = new float[buffer.remaining() * 3];
		int pos = 0;
		while (buffer.hasRemaining()) {
			AIVector3D vertex = buffer.get();
			data[pos++] = vertex.x();
			data[pos++] = vertex.y();
			data[pos++] = vertex.z();
		}
		return data;
	}
	
	private static float[] processNormals(AIMesh aiMesh) {
		// Normals should always be present, either provided by the mesh itself, or generated as a post-processing step on import.
		AIVector3D.Buffer buffer = aiMesh.mNormals();
		var data = new float[buffer.remaining() * 3];
		int pos = 0;
		while (buffer.hasRemaining()) {
			AIVector3D normal = buffer.get();
			data[pos++] = normal.x();
			data[pos++] = normal.y();
			data[pos++] = normal.z();
		}
		return data;
	}
	
	private static float[] processTangents(AIMesh aiMesh) {
		if (aiMesh.isNull(AIMesh.MTANGENTS)) {
			return new float[aiMesh.mNumVertices() * 3];
		}
		AIVector3D.Buffer buffer = aiMesh.mTangents();
		var data = new float[buffer.remaining() * 3];
		int pos = 0;
		while (buffer.hasRemaining()) {
			AIVector3D tangent = buffer.get();
			data[pos++] = tangent.x();
			data[pos++] = tangent.y();
			data[pos++] = tangent.z();
		}
		return data;
	}
	
	private static float[] processBitangents(AIMesh aiMesh) {
		if (aiMesh.isNull(AIMesh.MBITANGENTS)) {
			return new float[aiMesh.mNumVertices() * 3];
		}
		AIVector3D.Buffer buffer = aiMesh.mBitangents();
		var data = new float[buffer.remaining() * 3];
		int pos = 0;
		while (buffer.hasRemaining()) {
			AIVector3D bitangent = buffer.get();
			data[pos++] = bitangent.x();
			data[pos++] = bitangent.y();
			data[pos++] = bitangent.z();
		}
		return data;
	}
	
	private static float[] processTextureCoordinates(AIMesh aiMesh) {
		if (aiMesh.isNull(AIMesh.MTEXTURECOORDS)) {
			return new float[aiMesh.mNumVertices() * 2];
		}
		AIVector3D.Buffer buffer = aiMesh.mTextureCoords(0);
		var data = new float[buffer.remaining() * 2];
		int pos = 0;
		while (buffer.hasRemaining()) {
			AIVector3D textureCoordinate = buffer.get();
			data[pos++] = textureCoordinate.x();
			data[pos++] = textureCoordinate.y();
		}
		return data;
	}
	
	private static int[] processIndices(AIMesh aiMesh) {
		List<Integer> indices = new ArrayList<>();
		int numFaces = aiMesh.mNumFaces();
		AIFace.Buffer aiFaces = aiMesh.mFaces();
		for (int i = 0; i < numFaces; i++) {
			AIFace aiFace = aiFaces.get(i);
			IntBuffer buffer = aiFace.mIndices();
			while (buffer.hasRemaining()) {
				indices.add(buffer.get());
			}
		}
		return indices.stream().mapToInt(Integer::intValue).toArray();
	}
	
	private static AnimMeshData processBones(AIMesh aiMesh, List<Bone> bones) {
		if (aiMesh.isNull(AIMesh.MBONES)) {
			return new AnimMeshData(new int[aiMesh.mNumVertices() * AnimatedMesh.MAX_WEIGHTS], new float[aiMesh.mNumVertices() * AnimatedMesh.MAX_WEIGHTS]);
		}
		
		Map<Integer, List<VertexWeight>> weightMap = new HashMap<>();
		int numBones = aiMesh.mNumBones();
		PointerBuffer aiBones = aiMesh.mBones();
		for (int i = 0; i < numBones; i++) {
			AIBone aiBone = AIBone.create(aiBones.get(i));
			int id = bones.size();
			Bone bone = new Bone(id, aiBone.mName().dataString(), toMatrix(aiBone.mOffsetMatrix()));
			bones.add(bone);
			AIVertexWeight.Buffer aiWeights = aiBone.mWeights();
			aiWeights.forEach(aiWeight -> {
				VertexWeight vertexWeight = new VertexWeight(bone.boneId(), aiWeight.mVertexId(), aiWeight.mWeight());
				weightMap.computeIfAbsent(vertexWeight.vertexId(), _ -> new ArrayList<>()).add(vertexWeight);
			});
		}
		
		List<Integer> boneIDs = new ArrayList<>();
		List<Float> weights = new ArrayList<>();
		
		int numVertices = aiMesh.mNumVertices();
		for (int i = 0; i < numVertices; i++) {
			List<VertexWeight> vertexWeights = weightMap.get(i);
			int size = vertexWeights != null ? vertexWeights.size() : 0;
			for (int j = 0; j < AnimatedMesh.MAX_WEIGHTS; j++) {
				if (j < size) {
					VertexWeight vertexWeight = vertexWeights.get(j);
					weights.add(vertexWeight.weight());
					boneIDs.add(vertexWeight.boneId());
				} else {
					weights.add(0.0f);
					boneIDs.add(0);
				}
			}
		}
		
		return new AnimMeshData(boneIDs.stream().mapToInt(Integer::intValue).toArray(), mapToFloatArray(weights));
	}
	
	private static float[] mapToFloatArray(List<Float> list) {
		int size = list != null ? list.size() : 0;
		float[] floatArray = new float[size];
		for (int i = 0; i < size; i++) {
			floatArray[i] = list.get(i);
		}
		return floatArray;
	}
	
	private static AABBf processAABB(AIMesh aiMesh) {
		AIAABB aiaabb = aiMesh.mAABB();
		AIVector3D aiMin = aiaabb.mMin();
		AIVector3D aiMax = aiaabb.mMax();
		return new AABBf(aiMin.x(), aiMin.y(), aiMin.z(), aiMax.x(), aiMax.y(), aiMax.z());
	}
	
	private static Matrix4f toMatrix(AIMatrix4x4 aiMatrix4x4) {
		Matrix4f result = new Matrix4f();
		result.m00(aiMatrix4x4.a1());
		result.m10(aiMatrix4x4.a2());
		result.m20(aiMatrix4x4.a3());
		result.m30(aiMatrix4x4.a4());
		result.m01(aiMatrix4x4.b1());
		result.m11(aiMatrix4x4.b2());
		result.m21(aiMatrix4x4.b3());
		result.m31(aiMatrix4x4.b4());
		result.m02(aiMatrix4x4.c1());
		result.m12(aiMatrix4x4.c2());
		result.m22(aiMatrix4x4.c3());
		result.m32(aiMatrix4x4.c4());
		result.m03(aiMatrix4x4.d1());
		result.m13(aiMatrix4x4.d2());
		result.m23(aiMatrix4x4.d3());
		result.m33(aiMatrix4x4.d4());
		
		return result;
	}
	
	private static Node buildNodesTree(AINode aiNode, Node parentNode) {
		String nodeName = aiNode.mName().dataString();
		Node node = new Node(nodeName, parentNode, toMatrix(aiNode.mTransformation()));
		
		int numChildren = aiNode.mNumChildren();
		PointerBuffer aiChildren = aiNode.mChildren();
		for (int i = 0; i < numChildren; i++) {
			AINode aiChildNode = AINode.create(aiChildren.get(i));
			Node childNode = buildNodesTree(aiChildNode, node);
			node.addChild(childNode);
		}
		
		return node;
	}
	
	private static List<Animation> processAnimations(AIScene aiScene, List<Bone> bones, Node rootNode, Matrix4f globalInverseTransformation) {
		List<Animation> animations = new ArrayList<>();
		
		// Process all animations
		int numAnimations = aiScene.mNumAnimations();
		PointerBuffer aiAnimations = aiScene.mAnimations();
		for (int i = 0; i < numAnimations; i++) {
			AIAnimation aiAnimation = AIAnimation.create(aiAnimations.get(i));
			int maxFrames = calcAnimationMaxFrames(aiAnimation);
			
			List<AnimatedFrame> frames = new ArrayList<>();
			Animation animation = new Animation(aiAnimation.mName().dataString(), aiAnimation.mDuration(), frames);
			animations.add(animation);
			
			for (int j = 0; j < maxFrames; j++) {
				Matrix4f[] boneMatrices = new Matrix4f[MAX_BONES];
				Arrays.fill(boneMatrices, IDENTITY_MATRIX);
				var animatedFrame = new AnimatedFrame(boneMatrices);
				buildFrameMatrices(aiAnimation, bones, animatedFrame, j, rootNode, rootNode.getNodeTransformation(), globalInverseTransformation);
				frames.add(animatedFrame);
			}
		}
		
		return animations;
	}
	
	private static int calcAnimationMaxFrames(AIAnimation aiAnimation) {
		int maxFrames = 0;
		int numNodeAnims = aiAnimation.mNumChannels();
		PointerBuffer aiChannels = aiAnimation.mChannels();
		for (int i = 0; i < numNodeAnims; i++) {
			AINodeAnim aiNodeAnim = AINodeAnim.create(aiChannels.get(i));
			int numFrames = Math.max(Math.max(aiNodeAnim.mNumPositionKeys(), aiNodeAnim.mNumScalingKeys()), aiNodeAnim.mNumRotationKeys());
			maxFrames = Math.max(maxFrames, numFrames);
		}

		return maxFrames;
	}
	
	private static void buildFrameMatrices(AIAnimation aiAnimation, List<Bone> bones, AnimatedFrame animatedFrame, int frame, Node node, Matrix4f parentTransformation, Matrix4f globalInverseTransformation) {
		String nodeName = node.getName();
		AINodeAnim aiNodeAnim = findAINodeAnim(aiAnimation, nodeName);
		Matrix4f nodeTransformation = node.getNodeTransformation();
		if (aiNodeAnim != null) {
			nodeTransformation = buildNodeTransformationMatrix(aiNodeAnim, frame);
		}
		Matrix4f nodeGlobalTransformation = new Matrix4f(parentTransformation).mul(nodeTransformation);
		
		bones.stream()
		.filter(bone -> bone.boneName().equals(nodeName))
		.forEach(bone -> {
			Matrix4f boneTransformation = new Matrix4f(globalInverseTransformation).mul(nodeGlobalTransformation).mul(bone.offsetMatrix());
			animatedFrame.boneMatrices()[bone.boneId()] = boneTransformation;
		});
		
		for (Node childNode : node.getChildren()) {
			buildFrameMatrices(aiAnimation, bones, animatedFrame, frame, childNode, nodeGlobalTransformation, globalInverseTransformation);
		}
	}
	
	private static AINodeAnim findAINodeAnim(AIAnimation aiAnimation, String nodeName) {
		AINodeAnim result = null;
		int numAnimNodes = aiAnimation.mNumChannels();
		PointerBuffer aiChannels = aiAnimation.mChannels();
		for (int i = 0; i < numAnimNodes; i++) {
			AINodeAnim aiNodeAnim = AINodeAnim.create(aiChannels.get(i));
			if (nodeName.equals(aiNodeAnim.mNodeName().dataString())) {
				result = aiNodeAnim;
				break;
			}
		}
		return result;
	}
	
	private static Matrix4f buildNodeTransformationMatrix(AINodeAnim aiNodeAnim, int frame) {
		AIVectorKey.Buffer positionKeys = aiNodeAnim.mPositionKeys();
		AIVectorKey.Buffer scalingKeys = aiNodeAnim.mScalingKeys();
		AIQuatKey.Buffer rotationKeys = aiNodeAnim.mRotationKeys();
		
		AIVectorKey aiVecKey;
		AIVector3D vec;
		
		Matrix4f nodeTransformation = new Matrix4f();
		int numPositionsKeys = aiNodeAnim.mNumPositionKeys();
		if (numPositionsKeys > 0) {
			aiVecKey = positionKeys.get(Math.min(numPositionsKeys - 1, frame));
			vec = aiVecKey.mValue();
			nodeTransformation.translate(vec.x(), vec.y(), vec.z());
		}
		int numRotationsKeys = aiNodeAnim.mNumRotationKeys();
		if (numRotationsKeys > 0) {
			AIQuatKey quatKey = rotationKeys.get(Math.min(numRotationsKeys - 1, frame));
			AIQuaternion aiQuat = quatKey.mValue();
			Quaternionf quat = new Quaternionf(aiQuat.x(), aiQuat.y(), aiQuat.z(), aiQuat.w());
			nodeTransformation.rotate(quat);
		}
		int numScalingKeys = aiNodeAnim.mNumScalingKeys();
		if (numScalingKeys > 0) {
			aiVecKey = scalingKeys.get(Math.min(numScalingKeys - 1, frame));
			vec = aiVecKey.mValue();
			nodeTransformation.scale(vec.x(), vec.y(), vec.z());
		}
		
		return nodeTransformation;
	}
	
}
