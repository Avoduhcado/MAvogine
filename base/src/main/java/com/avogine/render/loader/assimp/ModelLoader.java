package com.avogine.render.loader.assimp;

import static org.lwjgl.assimp.Assimp.*;

import java.nio.*;
import java.util.*;

import org.joml.Vector4f;
import org.lwjgl.assimp.*;
import org.lwjgl.system.*;

import com.avogine.render.data.experimental.*;
import com.avogine.render.loader.texture.TextureCache;
import com.avogine.util.resource.*;

/**
 *
 */
public class ModelLoader {

	private ModelLoader() {
		// Utility Class
	}

	/**
	 * @param modelId 
	 * @param modelPath 
	 * @return
	 */
	public static AModel load(String modelId, String modelPath) {
		return load(modelId, modelPath, aiProcess_GenSmoothNormals | aiProcess_JoinIdenticalVertices |
				aiProcess_Triangulate | aiProcess_FixInfacingNormals | aiProcess_CalcTangentSpace | aiProcess_LimitBoneWeights |
				aiProcess_PreTransformVertices);
	}
	
	/**
	 * @param modelId 
	 * @param modelPath 
	 * @param flags
	 * @return
	 */
	public static AModel load(String modelId, String modelPath, int flags) {
		AIFileIO fileIo = AIFileIO.calloc()
				.OpenProc((pFileIO, fileName, openMode) -> {
					ByteBuffer data;
					String fileNameUtf8 = MemoryUtil.memUTF8(fileName);
					data = ResourceFileReader.ioResourceToByteBuffer(fileNameUtf8, 8 * 1024);

					return AIFile.create()
							.ReadProc((pFile, pBuffer, size, count) -> {
								long max = Math.min(data.remaining(), size * count);
								MemoryUtil.memCopy(MemoryUtil.memAddress(data) + data.position(), pBuffer, max);
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

		AIScene aiScene = Assimp.aiImportFileEx(ResourceConstants.MODEL_PATH + modelPath, flags, fileIo);

		fileIo.OpenProc().free();
		fileIo.CloseProc().free();

		if (aiScene == null) {
			throw new IllegalStateException("Error loading model: " + modelPath);
		}

		int numMaterials = aiScene.mNumMaterials();
		List<AMaterial> materials = new ArrayList<>();
		for (int i = 0; i < numMaterials; i++) {
			AIMaterial aiMaterial = AIMaterial.create(aiScene.mMaterials().get(i));
			materials.add(processMaterial(aiMaterial, modelPath));
		}

		AMaterial defaultMaterial = new AMaterial();
		processNode(aiScene.mRootNode(), aiScene, materials, defaultMaterial);
		if (!defaultMaterial.getMeshes().isEmpty()) {
			materials.add(defaultMaterial);
		}

		Assimp.aiReleaseImport(aiScene);

		return new AModel(modelId, materials);
	}
	
	private static AMaterial processMaterial(AIMaterial aiMaterial, String modelDir) {
		var material = new AMaterial();
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			var color = AIColor4D.create();
			
			int result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_DIFFUSE, aiTextureType_NONE, 0, color);
			if (result == aiReturn_SUCCESS) {
				material.setDiffuseColor(new Vector4f(color.r(), color.g(), color.b(), color.a()));
			}
			
			var aiTexturePath = AIString.calloc(stack);
			aiGetMaterialTexture(aiMaterial, aiTextureType_DIFFUSE, 0, aiTexturePath, (IntBuffer) null, null, null, null, null, null);
			String texturePath = aiTexturePath.dataString();
			if (texturePath != null && texturePath.length() > 0) {
//				material.setDiffuseTexturePath(modelDir + File.separator + new File(texturePath).getName());
				material.setDiffuseTexturePath(texturePath);
				TextureCache.getInstance().getTexture(material.getDiffuseTexturePath());
				material.setDiffuseColor(AMaterial.DEFAULT_COLOR);
			}
		}
		
		return material;
	}

	protected static void processNode(AINode node, AIScene scene, List<AMaterial> materials, AMaterial defaultMaterial) {
		// process all the node's meshes (if any)
		for (int i = 0; i < node.mNumMeshes(); i++) {
			var aiMesh = AIMesh.create(scene.mMeshes().get(node.mMeshes().get(i)));
			AMesh mesh = processMesh(aiMesh);
			
			int materialIndex = aiMesh.mMaterialIndex();
			AMaterial material;
			if (materialIndex >= 0 && materialIndex < materials.size()) {
				material = materials.get(materialIndex);
			} else {
				material = defaultMaterial;
			}
			material.getMeshes().add(mesh);
		}
		
		// then do the same for each of its children
		for (int i = 0; i < node.mNumChildren(); i++) {
			processNode(AINode.create(node.mChildren().get(i)), scene, materials, defaultMaterial);
		}
	}
	
	private static AMesh processMesh(AIMesh aiMesh) {
		float[] vertices = processVertices(aiMesh);
		float[] textureCoordinates = processTextureCoordinates(aiMesh);
		float[] normals = processNormals(aiMesh);
		int[] indices = processIndices(aiMesh);
		
		return new AMesh(vertices, textureCoordinates, normals, indices);
	}
	
	private static int[] processIndices(AIMesh aiMesh) {
		List<Integer> indices = new ArrayList<>();
		int numFaces = aiMesh.mNumFaces();
		AIFace.Buffer aiFaces = aiMesh.mFaces();
		for (int i = 0; i < numFaces; i++) {
			AIFace aiFace = aiFaces.get(i);
			IntBuffer buffer = aiFace.mIndices();
			while (buffer.remaining() > 0) {
				indices.add(buffer.get());
			}
		}
		return indices.stream().mapToInt(Integer::intValue).toArray();
	}
	
	private static float[] processVertices(AIMesh aiMesh) {
		AIVector3D.Buffer buffer = aiMesh.mVertices();
		float[] data = new float[buffer.remaining() * 3];
		int pos = 0;
		while (buffer.remaining() > 0) {
			AIVector3D vertex = buffer.get();
			data[pos++] = vertex.x();
			data[pos++] = vertex.y();
			data[pos++] = vertex.z();
		}
		return data;
	}
	
	private static float[] processNormals(AIMesh aiMesh) {
		AIVector3D.Buffer buffer = aiMesh.mNormals();
		if (buffer == null) {
			return new float[aiMesh.mNumVertices() * 3];
		}
		float[] data = new float[buffer.remaining() * 3];
		int pos = 0;
		while (buffer.remaining() > 0) {
			AIVector3D normal = buffer.get();
			data[pos++] = normal.x();
			data[pos++] = normal.y();
			data[pos++] = normal.z();
		}
		return data;
	}

	private static float[] processTangents(AIMesh aiMesh) {
		AIVector3D.Buffer buffer = aiMesh.mTangents();
		if (buffer == null) {
			return new float[aiMesh.mNumVertices() * 3];
		}
		float[] data = new float[buffer.remaining() * 3];
		int pos = 0;
		while (buffer.remaining() > 0) {
			AIVector3D tangent = buffer.get();
			data[pos++] = tangent.x();
			data[pos++] = tangent.y();
			data[pos++] = tangent.z();
		}
		return data;
	}

	private static float[] processBitangents(AIMesh aiMesh) {
		AIVector3D.Buffer buffer = aiMesh.mNormals();
		if (buffer == null) {
			return new float[aiMesh.mNumVertices() * 3];
		}
		float[] data = new float[buffer.remaining() * 3];
		int pos = 0;
		while (buffer.remaining() > 0) {
			AIVector3D bitangent = buffer.get();
			data[pos++] = bitangent.x();
			data[pos++] = bitangent.y();
			data[pos++] = bitangent.z();
		}
		return data;
	}
	
	private static float[] processTextureCoordinates(AIMesh aiMesh) {
		// We only process the first index of texture coordinates, this method will need modified
		// if models are expected to use multiple texture coordinate indices.
		AIVector3D.Buffer buffer = aiMesh.mTextureCoords(0);
		if (buffer == null) {
			return new float[aiMesh.mNumVertices() * 2];
		}
		float[] data = new float[buffer.remaining() * 2];
		int pos = 0;
		while (buffer.remaining() > 0) {
			AIVector3D textureCoordinate = buffer.get();
			data[pos++] = textureCoordinate.x();
			data[pos++] = textureCoordinate.y();
		}
		return data;
	}
	
}
