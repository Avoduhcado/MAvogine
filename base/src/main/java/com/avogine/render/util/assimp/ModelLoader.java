package com.avogine.render.util.assimp;

import static org.lwjgl.system.MemoryUtil.*;

import java.nio.*;
import java.util.*;
import java.util.stream.Collectors;

import org.joml.*;
import org.joml.Math;
import org.joml.primitives.AABBf;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.system.*;

import com.avogine.render.data.*;
import com.avogine.render.data.mesh.*;
import com.avogine.render.data.model.*;
import com.avogine.render.util.TextureCache;
import com.avogine.util.ResourceUtils;

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
	private static final Matrix4f IDENTITY_MATRIX = new Matrix4f();
	
	/**
	 * @param weights
	 * @param boneIds
	 */
	public record AnimMeshData(FloatBuffer weights, IntBuffer boneIds) {}

	private record Bone(int boneId, String boneName, Matrix4f offsetMatrix) {}

	private record VertexWeight(int boneId, int vertexId, float weight) {}
	
	private record ModelData(Map<Material, List<MeshData>> materialMeshMap, List<Animation> animations) {}

	private ModelLoader() {
		
	}
	
	/**
	 * @param id
	 * @param modelPath
	 * @param textureCache
	 * @return an {@link AnimatedModel} loaded from the given modelPath.
	 * @throws IllegalStateException if the model file could not be opened.
	 */
	public static AnimatedModel loadAnimatedModel(String id, String modelPath, TextureCache textureCache) {
		ModelData modelData = loadModel(modelPath, textureCache, Assimp.aiProcess_GenSmoothNormals | Assimp.aiProcess_JoinIdenticalVertices |
				Assimp.aiProcess_Triangulate | Assimp.aiProcess_FixInfacingNormals | Assimp.aiProcess_CalcTangentSpace | Assimp.aiProcess_LimitBoneWeights |
				Assimp.aiProcess_GenBoundingBoxes);
		
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
		ModelData modelData = loadModel(modelPath, textureCache, Assimp.aiProcess_GenSmoothNormals | Assimp.aiProcess_JoinIdenticalVertices |
				Assimp.aiProcess_Triangulate | Assimp.aiProcess_FixInfacingNormals | Assimp.aiProcess_CalcTangentSpace | Assimp.aiProcess_LimitBoneWeights |
				Assimp.aiProcess_GenBoundingBoxes | Assimp.aiProcess_PreTransformVertices);
		
		Map<Material, List<StaticMesh>> materialMeshMap = modelData.materialMeshMap().entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().stream().map(StaticMesh::new).toList()));
		return new StaticModel(id, materialMeshMap);
	}
	
	/**
	 * @param id 
	 * @param modelPath
	 * @param textureCache 
	 * @param flags
	 * @return {@link ModelData} containing the parsed model file data.
	 * @throws IllegalStateException if the model file could not be opened.
	 */
	@SuppressWarnings({
		"java:S2095" // The actual AIFileIO instance holds very little of its own memory which should be fine to be GC'd and its AIFile proc's are being manually freed which provide the bulk of the memory footprint.
	})
	private static ModelData loadModel(String modelPath, TextureCache textureCache, int flags) {
		AIFileIO fileIo = AIFileIO.create()
				.OpenProc((pFileIO, fileName, openMode) -> {
					String fileNameUtf8 = memUTF8(fileName);
					ByteBuffer data = ResourceUtils.readResourceToBuffer(fileNameUtf8);

					return AIFile.create()
							.ReadProc((pFile, pBuffer, size, count) -> {
								long max = Math.min(data.remaining() / size, count);
								memCopy(memAddress(data), pBuffer, max * size);
								data.position(data.position() + (int) (max * size));
								return max;
							})
							.SeekProc((pFile, offset, origin) -> {
								if (origin == Assimp.aiOrigin_CUR) {
									data.position(data.position() + (int) offset);
								} else if (origin == Assimp.aiOrigin_SET) {
									data.position((int) offset);
								} else if (origin == Assimp.aiOrigin_END) {
									data.position(data.limit() + (int) offset);
								}
								return 0;
							})
							.FileSizeProc(pFile -> data.limit())
							.address();
				})
				.CloseProc((pFileIO, pFile) -> {
					AIFile aiFile = AIFile.create(pFile);

					aiFile.ReadProc().free();
					aiFile.SeekProc().free();
					aiFile.FileSizeProc().free();
				});
		
		AIScene aiScene = Assimp.aiImportFileEx(modelPath, flags, fileIo);

		fileIo.OpenProc().free();
		fileIo.CloseProc().free();

		if (aiScene == null) {
			throw new IllegalStateException(Assimp.aiGetErrorString());
		}

		String modelDirectory = modelPath.substring(0, modelPath.lastIndexOf('/') + 1);

		int numMaterials = aiScene.mNumMaterials();
		PointerBuffer materialsBuffer = aiScene.mMaterials();
		SequencedMap<Material, List<MeshData>> materials = new LinkedHashMap<>();
		for (int i = 0; i < numMaterials; i++) {
			AIMaterial aiMaterial = AIMaterial.create(materialsBuffer.get(i));
			materials.putLast(processMaterial(aiMaterial, modelDirectory, textureCache), new ArrayList<>());
		}
		
		int numMeshes = aiScene.mNumMeshes();
		PointerBuffer aiMeshes = aiScene.mMeshes();
		Map.Entry<Material, List<MeshData>> defaultMaterial = Map.entry(new Material(), new ArrayList<>());
		List<Bone> bones = new ArrayList<>();
		for (int i = 0; i < numMeshes; i++) {
			AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
			MeshData mesh = processMesh(aiMesh, bones);
			int materialIndex = aiMesh.mMaterialIndex();
			if (materialIndex >= 0 && materialIndex < materials.size()) {
				materials.sequencedValues().stream()
				.skip(materialIndex)
				.findFirst()
				.ifPresent(meshList -> meshList.add(mesh));
			} else {
				defaultMaterial.getValue().add(mesh);
			}
		}
		
		if (!defaultMaterial.getValue().isEmpty()) {
			materials.putLast(defaultMaterial.getKey(), defaultMaterial.getValue());
		}
		
		List<Animation> animations = new ArrayList<>();
		int numAnimations = aiScene.mNumAnimations();
		if (numAnimations > 0) {
			Node rootNode = buildNodesTree(aiScene.mRootNode(), null);
			Matrix4f globalInverseTransformation = toMatrix(aiScene.mRootNode().mTransformation()).invert();
			animations = processAnimations(aiScene, bones, rootNode, globalInverseTransformation);
		}

		Assimp.aiReleaseImport(aiScene);
		
		return new ModelData(materials, animations);
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
	
	/**
	 * <a href="https://github.com/Avoduhcado/MAvogine/issues/41">PBR materials #41</a>
	 */
	protected static Material processMaterial(AIMaterial aiMaterial, String modelDirectory, TextureCache textureCache) {
		// TODO#41 Use a real default
		Material material = new Material(textureCache.getDefaultTexture());
		try (MemoryStack stack = MemoryStack.stackPush()) {
			AIColor4D color = AIColor4D.create();
	
			int result = Assimp.aiGetMaterialColor(aiMaterial, Assimp.AI_MATKEY_COLOR_DIFFUSE, Assimp.aiTextureType_NONE, 0, color);
			if (result == Assimp.aiReturn_SUCCESS) {
				material.setDiffuseColor(new Vector4f(color.r(), color.g(), color.b(), color.a()));
			}
	
			result = Assimp.aiGetMaterialColor(aiMaterial, Assimp.AI_MATKEY_COLOR_AMBIENT, Assimp.aiTextureType_NONE, 0, color);
			if (result == Assimp.aiReturn_SUCCESS) {
				material.setAmbientColor(new Vector4f(color.r(), color.g(), color.b(), color.a()));
			}
			
			result = Assimp.aiGetMaterialColor(aiMaterial, Assimp.AI_MATKEY_COLOR_SPECULAR, Assimp.aiTextureType_NONE, 0, color);
			if (result == Assimp.aiReturn_SUCCESS) {
				material.setSpecularColor(new Vector4f(color.r(), color.g(), color.b(), color.a()));
			}

			// TODO#41 Add reflectance to Material
//			float reflectance = 0.0f;
//			float[] shininessFactor = new float[] { 0.0f };
//			int[] pMax = new int[] { 1 };
//			result = Assimp.aiGetMaterialFloatArray(aiMaterial, Assimp.AI_MATKEY_SHININESS_STRENGTH, Assimp.aiTextureType_NONE, 0, shininessFactor, pMax);
//			if (result != Assimp.aiReturn_SUCCESS) {
//				reflectance = shininessFactor[0];
//			}
//			material.setReflectance(reflectance);
			
			AIString aiTexturePath = AIString.calloc(stack);
			Assimp.aiGetMaterialTexture(aiMaterial, Assimp.aiTextureType_DIFFUSE, 0, aiTexturePath, (IntBuffer) null, null, null, null, null, null);
			String texturePath = aiTexturePath.dataString();
			if (texturePath != null && !texturePath.isBlank()) {
				material.setDiffuseTexture(textureCache.getTexture(modelDirectory + texturePath));
				material.setDiffuseColor(Material.DEFAULT_COLOR);
			}
			
			// TODO#41 Add normal maps and potentially ambient/specular mapping
			
			return material;
		}
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
	
	private static MeshData processMesh(AIMesh aiMesh, List<Bone> bones) {
		FloatBuffer positions = processVertices(aiMesh);
		FloatBuffer normals = processNormals(aiMesh);
		FloatBuffer tangents = processTangents(aiMesh);
		FloatBuffer bitangents = processBitangents(aiMesh);
		FloatBuffer textureCoordinates = processTextureCoordinates(aiMesh);
		AnimMeshData animMeshData = processBones(aiMesh, bones);
		IntBuffer indices = processIndices(aiMesh);
		AABBf aabb = processAABB(aiMesh);

		return new MeshData(new VertexBuffers(positions, normals, tangents, bitangents, textureCoordinates, animMeshData.weights(), animMeshData.boneIds(), indices), aabb);
	}
	
	private static AnimMeshData processBones(AIMesh aiMesh, List<Bone> bones) {
		if (aiMesh.isNull(AIMesh.MBONES)) {
			return new AnimMeshData(MemoryUtil.memCallocFloat(aiMesh.mNumVertices() * MAX_WEIGHTS), MemoryUtil.memCallocInt(aiMesh.mNumVertices() * MAX_WEIGHTS));
		}
		
		Map<Integer, List<VertexWeight>> weightMap = new HashMap<>();
		int numBones = aiMesh.mNumBones();
		PointerBuffer aiBones = aiMesh.mBones();
		for (int i = 0; i < numBones; i++) {
			AIBone aiBone = AIBone.create(aiBones.get(i));
			int id = bones.size();
			Bone bone = new Bone(id, aiBone.mName().dataString(), toMatrix(aiBone.mOffsetMatrix()));
			bones.add(bone);
			int numWeights = aiBone.mNumWeights();
			AIVertexWeight.Buffer aiWeights = aiBone.mWeights();
			for (int j = 0; j < numWeights; j++) {
				AIVertexWeight aiWeight = aiWeights.get(j);
				VertexWeight weight = new VertexWeight(bone.boneId(), aiWeight.mVertexId(), aiWeight.mWeight());
				List<VertexWeight> vertexWeights = weightMap.get(weight.vertexId());
				if (vertexWeights == null) {
					vertexWeights = new ArrayList<>();
					weightMap.put(weight.vertexId(), vertexWeights);
				}
				vertexWeights.add(weight);
			}
		}
		
		int numVertices = aiMesh.mNumVertices();
		FloatBuffer weights = MemoryUtil.memAllocFloat(numVertices * MAX_WEIGHTS);
		IntBuffer boneIds = MemoryUtil.memAllocInt(numVertices * MAX_WEIGHTS);
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
	
	private static FloatBuffer processVertices(AIMesh aiMesh) {
		AIVector3D.Buffer buffer = aiMesh.mVertices();
		FloatBuffer data = MemoryUtil.memAllocFloat(buffer.remaining() * 3);
		buffer.stream().forEach(vertex -> data.put(vertex.x()).put(vertex.y()).put(vertex.z()));
		return data.flip();
	}
	
	private static FloatBuffer processNormals(AIMesh aiMesh) {
		if (aiMesh.isNull(AIMesh.MNORMALS)) {
			return MemoryUtil.memCallocFloat(aiMesh.mNumVertices() * 3);
		}
		AIVector3D.Buffer buffer = aiMesh.mNormals();
		var data = MemoryUtil.memAllocFloat(buffer.remaining() * 3);
		buffer.stream().forEach(normal -> data.put(normal.x()).put(normal.y()).put(normal.z()));
		
		return data.flip();
	}
	
	private static FloatBuffer processTangents(AIMesh aiMesh) {
		if (aiMesh.isNull(AIMesh.MTANGENTS)) {
			return MemoryUtil.memCallocFloat(aiMesh.mNumVertices() * 3);
		}
		AIVector3D.Buffer buffer = aiMesh.mTangents();
		var data = MemoryUtil.memAllocFloat(buffer.remaining() * 3);
		buffer.stream().forEach(tangent -> data.put(tangent.x()).put(tangent.y()).put(tangent.z()));
		
		return data.flip();
	}
	
	private static FloatBuffer processBitangents(AIMesh aiMesh) {
		if (aiMesh.isNull(AIMesh.MBITANGENTS)) {
			return MemoryUtil.memCallocFloat(aiMesh.mNumVertices() * 3);
		}
		AIVector3D.Buffer buffer = aiMesh.mBitangents();
		var data = MemoryUtil.memAllocFloat(buffer.remaining() * 3);
		buffer.stream().forEach(bitangent -> data.put(bitangent.x()).put(bitangent.y()).put(bitangent.z()));
		
		return data.flip();
	}
	
	private static FloatBuffer processTextureCoordinates(AIMesh aiMesh) {
		if (aiMesh.isNull(AIMesh.MTEXTURECOORDS)) {
			return MemoryUtil.memCallocFloat(aiMesh.mNumVertices() * 2);
		}
		AIVector3D.Buffer buffer = aiMesh.mTextureCoords(0);
		var data = MemoryUtil.memAllocFloat(buffer.remaining() * 2);
		buffer.stream().forEach(textureCoordinate -> data.put(textureCoordinate.x()).put(textureCoordinate.y()));
		
		return data.flip();
	}
	
	private static IntBuffer processIndices(AIMesh aiMesh) {
		int numFaces = aiMesh.mNumFaces();
		IntBuffer indices = MemoryUtil.memAllocInt(numFaces * 3);
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
