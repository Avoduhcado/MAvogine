package com.avogine.render.util.assimp;

import static org.lwjgl.system.MemoryUtil.*;

import java.nio.*;
import java.util.*;

import org.joml.*;
import org.joml.Math;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.system.*;

import com.avogine.render.data.*;
import com.avogine.render.util.TextureCache;
import com.avogine.util.ResourceUtils;

/**
 *
 */
public class StaticModelLoader {
	
	private StaticModelLoader() {
		
	}
	
	/**
	 * @param id 
	 * @param modelPath
	 * @param textureCache 
	 * @return a {@link Model} loaded from the given modelPath.
	 * @throws IllegalStateException if the model file could not be opened.
	 */
	public static Model loadModel(String id, String modelPath, TextureCache textureCache) {
		return loadModel(id, modelPath, textureCache, Assimp.aiProcess_GenSmoothNormals | Assimp.aiProcess_JoinIdenticalVertices |
				Assimp.aiProcess_Triangulate | Assimp.aiProcess_FixInfacingNormals | Assimp.aiProcess_CalcTangentSpace | Assimp.aiProcess_LimitBoneWeights |
				Assimp.aiProcess_GenBoundingBoxes);
	}
	
	/**
	 * @param id 
	 * @param modelPath
	 * @param textureCache 
	 * @param flags
	 * @return a {@link Model} loaded from the given modelPath.
	 * @throws IllegalStateException if the model file could not be opened.
	 */
	@SuppressWarnings({
		"java:S2095" // The actual AIFileIO instance holds very little of its own memory which should be fine to be GC'd and its AIFile proc's are being manually freed which provide the bulk of the memory footprint.
	})
	public static Model loadModel(String id, String modelPath, TextureCache textureCache, int flags) {
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
		List<Material> materials = new ArrayList<>();
		for (int i = 0; i < numMaterials; ++i) {
			AIMaterial aiMaterial = AIMaterial.create(materialsBuffer.get(i));
			materials.add(processMaterial(aiMaterial, modelDirectory, textureCache));
		}

		List<Mesh> meshes = processNode(aiScene.mRootNode(), aiScene);

		Assimp.aiReleaseImport(aiScene);
		
		return new Model(id, meshes, materials);
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
	
	protected static List<Mesh> processNode(AINode node, AIScene scene) {
		PointerBuffer meshesBuffer = scene.mMeshes();
		IntBuffer nodeMeshesBuffer = node.mMeshes();
		List<Mesh> meshes = new ArrayList<>();
		// process all the node's meshes (if any)
		for (int i = 0; i < node.mNumMeshes(); i++) {
			AIMesh mesh = AIMesh.create(meshesBuffer.get(nodeMeshesBuffer.get(i)));
			meshes.add(processMesh(mesh));
		}
		
		PointerBuffer nodeChildrenBuffer = node.mChildren();
		// then do the same for each of its children
		for (int i = 0; i < node.mNumChildren(); i++) {
			meshes.addAll(processNode(AINode.create(nodeChildrenBuffer.get(i)), scene));
		}
		return meshes;
	}
	
	protected static Mesh processMesh(AIMesh aiMesh) {
		try (var vertexData = new VertexData(processVertices(aiMesh), processNormals(aiMesh), processTangents(aiMesh), processBitangents(aiMesh), processTextureCoordinates(aiMesh), processIndices(aiMesh))) {
			int materialIndex = aiMesh.mMaterialIndex();
			AIAABB aabb = aiMesh.mAABB();
			Vector3f aabbMin = new Vector3f(aabb.mMin().x(), aabb.mMin().y(), aabb.mMin().z());
			Vector3f aabbMax = new Vector3f(aabb.mMax().x(), aabb.mMax().y(), aabb.mMax().z());

			return new Mesh(vertexData, materialIndex, aabbMin, aabbMax);
		}
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
	
}
