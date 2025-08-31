package com.avogine.render.opengl.model.util;

import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.nio.*;
import java.util.*;

import org.joml.*;
import org.joml.Math;
import org.joml.primitives.AABBf;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import com.avogine.logging.AvoLog;
import com.avogine.render.model.animation.*;
import com.avogine.render.model.mesh.data.*;
import com.avogine.render.opengl.model.Model;
import com.avogine.render.opengl.model.material.*;
import com.avogine.render.opengl.model.material.data.BlinnPhongData;
import com.avogine.render.opengl.model.mesh.*;
import com.avogine.render.opengl.texture.util.TextureCache;
import com.avogine.render.util.AssimpFileUtils;

/**
 *
 */
public class ModelLoader {
	/**
	 * Max number of bone weights that can be applied to a single vertex.
	 */
	public static final int MAX_WEIGHTS = 4;

	/**
	 * Max number of bones a single mesh can support.
	 */
	public static final int MAX_BONES = 150;
	
	/**
	 * Default color vector to use when no actual color is specified.
	 */
	public static final Vector4f DEFAULT_COLOR = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);

	private static final Matrix4f IDENTITY_MATRIX = new Matrix4f();
	
	private record AnimMeshData(FloatBuffer weights, IntBuffer boneIds) {}

	private record Bone(int boneId, String boneName, Matrix4f offsetMatrix) {}

	private record VertexWeight(int boneId, int vertexId, float weight) {}
	
	private ModelLoader() {}
	
	/**
	 * TODO convert modelPath to modelName, source modelName from a resource property that points to the file location and pass _that_ file location to the reader
	 * @param id 
	 * @param modelPath
	 * @param textureCache 
	 * @param animated 
	 * @return a {@link Model} loaded from the given path.
	 */
	public static Model loadModel(String id, String modelPath, TextureCache textureCache, boolean animated) {
		AIScene aiScene = AssimpFileUtils.readSceneFromMemory(modelPath, aiProcess_GenSmoothNormals | aiProcess_JoinIdenticalVertices |
				aiProcess_Triangulate | aiProcess_FixInfacingNormals | aiProcess_CalcTangentSpace | aiProcess_LimitBoneWeights |
				aiProcess_GenBoundingBoxes | (animated ? 0 : aiProcess_PreTransformVertices));

		List<AIMaterial> aiMaterials = AssimpFileUtils.readMaterials(aiScene);
		String modelDirectory = modelPath.substring(0, modelPath.lastIndexOf('/') + 1);
		List<Material> materials = aiMaterials.stream()
				.map(aiMaterial -> processSimpleMaterial(aiMaterial, modelDirectory, textureCache))
				.toList();

		List<AIMesh> aiMeshes = AssimpFileUtils.readMeshes(aiScene);
		List<Bone> bones = new ArrayList<>();
		SimpleMaterial defaultMaterial = new SimpleMaterial();
		for (AIMesh aiMesh : aiMeshes) {
			int materialIndex = aiMesh.mMaterialIndex();
			MeshData meshData = processMesh(aiMesh, bones);
			var mesh = animated ? new AnimatedMesh(meshData) : new StaticMesh(meshData);
			if (materialIndex >= 0 && materialIndex < materials.size()) {
				materials.get(materialIndex).addMesh(mesh);
			} else {
				defaultMaterial.addMesh(mesh);
			}
		}
		if (defaultMaterial.getAllMeshes().count() > 0) {
			materials.add(defaultMaterial);
		}

		List<AIAnimation> aiAnimations = AssimpFileUtils.readAnimations(aiScene);
		List<Animation> animations = new ArrayList<>();
		if (!aiAnimations.isEmpty()) {
			Node rootNode = buildNodesTree(aiScene.mRootNode(), null);
			Matrix4f globalInverseTransformation = toMatrix(aiScene.mRootNode().mTransformation()).invert();
			animations.addAll(processAnimations(aiScene, bones, rootNode, globalInverseTransformation));
		}

		aiReleaseImport(aiScene);

		return new Model(id, materials, animations);
	}
	
	private static Material processSimpleMaterial(AIMaterial aiMaterial, String modelDirectory, TextureCache textureCache) {
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
	
	private static Material processPBRMaterial(AIMaterial aiMaterial, String modelDirectory, TextureCache textureCache) {
		AIString texturePath = AIString.create();
		String albedoTexture = processMaterialTexture(aiMaterial, aiTextureType_BASE_COLOR, texturePath, modelDirectory, textureCache);
		String normalTexture = processMaterialTexture(aiMaterial, aiTextureType_NORMALS, texturePath, modelDirectory, textureCache);
		String metallicTexture = processMaterialTexture(aiMaterial, aiTextureType_METALNESS, texturePath, modelDirectory, textureCache);
		String roughnessTexture = processMaterialTexture(aiMaterial, aiTextureType_DIFFUSE_ROUGHNESS, texturePath, modelDirectory, textureCache);
		String aoTexture = processMaterialTexture(aiMaterial, aiTextureType_AMBIENT_OCCLUSION, texturePath, modelDirectory, textureCache);
		
		return new PBRMaterial(albedoTexture, normalTexture, metallicTexture, roughnessTexture, aoTexture);
	}
	
	private static Vector4f processMaterialColor(AIMaterial aiMaterial, String materialKeyColor, AIColor4D color) {
		int result = aiGetMaterialColor(aiMaterial, materialKeyColor, aiTextureType_NONE, 0, color);
		if (result == aiReturn_SUCCESS) {
			return new Vector4f(color.r(), color.g(), color.b(), color.a());
		}
		return DEFAULT_COLOR;
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
		FloatBuffer positions = processVertices(aiMesh);
		FloatBuffer normals = processNormals(aiMesh);
		FloatBuffer tangents = processTangents(aiMesh);
		FloatBuffer bitangents = processBitangents(aiMesh);
		FloatBuffer textureCoordinates = processTextureCoordinates(aiMesh);
		AnimMeshData animMeshData = processBones(aiMesh, bones);
		IntBuffer indices = processIndices(aiMesh);
		
		AABBf aabb = processAABB(aiMesh);
		
		int materialIndex = aiMesh.mMaterialIndex();
		
		return new MeshData(new VertexBuffers(positions, normals, tangents, bitangents, textureCoordinates, animMeshData.weights(), animMeshData.boneIds(), indices), aabb, materialIndex);
	}
	
	private static FloatBuffer processVertices(AIMesh aiMesh) {
		AIVector3D.Buffer buffer = aiMesh.mVertices();
		FloatBuffer data = memAllocFloat(buffer.remaining() * 3);
		buffer.stream().forEach(vertex -> data.put(vertex.x()).put(vertex.y()).put(vertex.z()));
		return data.flip();
	}
	
	private static FloatBuffer processNormals(AIMesh aiMesh) {
		if (aiMesh.isNull(AIMesh.MNORMALS)) {
			return memCallocFloat(aiMesh.mNumVertices() * 3);
		}
		AIVector3D.Buffer buffer = aiMesh.mNormals();
		var data = memAllocFloat(buffer.remaining() * 3);
		buffer.stream().forEach(normal -> data.put(normal.x()).put(normal.y()).put(normal.z()));
		
		return data.flip();
	}
	
	private static FloatBuffer processTangents(AIMesh aiMesh) {
		if (aiMesh.isNull(AIMesh.MTANGENTS)) {
			return memCallocFloat(aiMesh.mNumVertices() * 3);
		}
		AIVector3D.Buffer buffer = aiMesh.mTangents();
		var data = memAllocFloat(buffer.remaining() * 3);
		buffer.stream().forEach(tangent -> data.put(tangent.x()).put(tangent.y()).put(tangent.z()));
		
		return data.flip();
	}
	
	private static FloatBuffer processBitangents(AIMesh aiMesh) {
		if (aiMesh.isNull(AIMesh.MBITANGENTS)) {
			return memCallocFloat(aiMesh.mNumVertices() * 3);
		}
		AIVector3D.Buffer buffer = aiMesh.mBitangents();
		var data = memAllocFloat(buffer.remaining() * 3);
		buffer.stream().forEach(bitangent -> data.put(bitangent.x()).put(bitangent.y()).put(bitangent.z()));
		
		return data.flip();
	}
	
	private static FloatBuffer processTextureCoordinates(AIMesh aiMesh) {
		if (aiMesh.isNull(AIMesh.MTEXTURECOORDS)) {
			return memCallocFloat(aiMesh.mNumVertices() * 2);
		}
		AIVector3D.Buffer buffer = aiMesh.mTextureCoords(0);
		var data = memAllocFloat(buffer.remaining() * 2);
		buffer.stream().forEach(textureCoordinate -> data.put(textureCoordinate.x()).put(textureCoordinate.y()));
		
		return data.flip();
	}
	
	private static AnimMeshData processBones(AIMesh aiMesh, List<Bone> bones) {
		if (aiMesh.isNull(AIMesh.MBONES)) {
			return new AnimMeshData(memCallocFloat(aiMesh.mNumVertices() * MAX_WEIGHTS), memCallocInt(aiMesh.mNumVertices() * MAX_WEIGHTS));
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
				VertexWeight weight = new VertexWeight(bone.boneId(), aiWeight.mVertexId(), aiWeight.mWeight());
				weightMap.computeIfAbsent(weight.vertexId(), v -> new ArrayList<>()).add(weight);
			});
		}
		
		int numVertices = aiMesh.mNumVertices();
		FloatBuffer weights = memAllocFloat(numVertices * MAX_WEIGHTS);
		IntBuffer boneIds = memAllocInt(numVertices * MAX_WEIGHTS);
		for (int i = 0; i < numVertices; i++) {
			List<VertexWeight> vertexWeights = weightMap.get(i);
			int size = vertexWeights != null ? vertexWeights.size() : 0;
			for (int j = 0; j < MAX_WEIGHTS; j++) {
				if (j < size) {
					VertexWeight weight = vertexWeights.get(j);
					weights.put(weight.weight());
					boneIds.put(weight.boneId());
				} else {
					weights.put(0.0f);
					boneIds.put(0);
				}
			}
		}
		
		return new AnimMeshData(weights.flip(), boneIds.flip());
	}
	
	private static IntBuffer processIndices(AIMesh aiMesh) {
		int numFaces = aiMesh.mNumFaces();
		IntBuffer indices = memAllocInt(numFaces * 3);
		AIFace.Buffer aiFaces = aiMesh.mFaces();
		for (int i = 0; i < numFaces; i++) {
			AIFace aiFace = aiFaces.get(i);
			IntBuffer buffer = aiFace.mIndices();
			indices.put(buffer);
		}
		return indices.flip();
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
