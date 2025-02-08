package com.avogine.render.loader.assimp;

import static org.lwjgl.system.MemoryUtil.*;

import java.lang.Math;
import java.nio.*;
import java.util.*;

import org.joml.*;
import org.lwjgl.assimp.*;
import org.lwjgl.system.MemoryStack;

import com.avogine.render.TextureCache;
import com.avogine.render.data.*;
import com.avogine.util.ResourceUtil;

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
	 * @return
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
	 * @return
	 */
	public static Model loadModel(String id, String modelPath, TextureCache textureCache, int flags) {
		AIFileIO fileIo = AIFileIO.create()
				.OpenProc((pFileIO, fileName, openMode) -> {
					String fileNameUtf8 = memUTF8(fileName);
					ByteBuffer data = ResourceUtil.readResourceToBuffer(fileNameUtf8);

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
		List<Material> materials = new ArrayList<>();
		for (int i = 0; i < numMaterials; ++i) {
			AIMaterial aiMaterial = AIMaterial.create(aiScene.mMaterials().get(i));
			materials.add(processMaterial(aiMaterial, modelDirectory, textureCache));
		}
		
		List<Mesh> meshes = processNode(aiScene.mRootNode(), aiScene);
		
		Assimp.aiReleaseImport(aiScene);
		
		return new Model(id, meshes, materials);
	}

	protected static Material processMaterial(AIMaterial aiMaterial, String modelDirectory, TextureCache textureCache) {
		// TODO Use a real default
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
			
			float reflectance = 0.0f;
			float[] shininessFactor = new float[] { 0.0f };
			int[] pMax = new int[] { 1 };
			result = Assimp.aiGetMaterialFloatArray(aiMaterial, Assimp.AI_MATKEY_SHININESS_STRENGTH, Assimp.aiTextureType_NONE, 0, shininessFactor, pMax);
			if (result != Assimp.aiReturn_SUCCESS) {
				reflectance = shininessFactor[0];
			}
			// TODO Add reflectance to Material
//			material.setReflectance(reflectance);
			
			AIString aiTexturePath = AIString.calloc(stack);
			Assimp.aiGetMaterialTexture(aiMaterial, Assimp.aiTextureType_DIFFUSE, 0, aiTexturePath, (IntBuffer) null, null, null, null, null, null);
			String texturePath = aiTexturePath.dataString();
			if (texturePath != null && !texturePath.isBlank()) {
				material.setDiffuseTexture(textureCache.getTexture(modelDirectory + texturePath));
				material.setDiffuseColor(Material.DEFAULT_COLOR);
			}
			
			// TODO Add normal maps and potentially ambient/specular mapping
			
			return material;
		}
	}
	
	protected static List<Mesh> processNode(AINode node, AIScene scene) {
		List<Mesh> meshes = new ArrayList<>();
		// process all the node's meshes (if any)
		for (int i = 0; i < node.mNumMeshes(); i++) {
			AIMesh mesh = AIMesh.create(scene.mMeshes().get(node.mMeshes().get(i)));
			meshes.add(processMesh(mesh));
		}
		
		// then do the same for each of its children
		for (int i = 0; i < node.mNumChildren(); i++) {
			meshes.addAll(processNode(AINode.create(node.mChildren().get(i)), scene));
		}
		return meshes;
	}
	
	protected static Mesh processMesh(AIMesh aiMesh) {
		float[] vertices = processVertices(aiMesh);
		float[] normals = processNormals(aiMesh);
		float[] tangents = processTangents(aiMesh);
		float[] bitangents = processBitangents(aiMesh);
		float[] textureCoordinates = processTextureCoordinates(aiMesh);
		int[] indices = processIndices(aiMesh);

		int materialIndex = aiMesh.mMaterialIndex();

		AIAABB aabb = aiMesh.mAABB();
		Vector3f aabbMin = new Vector3f(aabb.mMin().x(), aabb.mMin().y(), aabb.mMin().z());
		Vector3f aabbMax = new Vector3f(aabb.mMax().x(), aabb.mMax().y(), aabb.mMax().z());

		return new Mesh(vertices, normals, tangents, bitangents, textureCoordinates, indices, materialIndex, aabbMin, aabbMax);
	}
	
	private static float[] processVertices(AIMesh aiMesh) {
		AIVector3D.Buffer buffer = aiMesh.mVertices();
		float[] data = new float[buffer.remaining() * 3];
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
		if (aiMesh.isNull(AIMesh.MNORMALS)) {
			return new float[aiMesh.mNumVertices() * 3];
		}
		
		AIVector3D.Buffer buffer = aiMesh.mNormals();
		float[] data = new float[buffer.remaining() * 3];
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
		float[] data = new float[buffer.remaining() * 3];
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
		float[] data = new float[buffer.remaining() * 3];
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
		float[] data = new float[buffer.remaining() * 2];
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
	
}
