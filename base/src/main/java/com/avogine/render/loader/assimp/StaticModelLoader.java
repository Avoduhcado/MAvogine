package com.avogine.render.loader.assimp;

import java.nio.*;
import java.util.*;

import org.joml.Vector3f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.system.MemoryUtil;

import com.avogine.render.data.material.*;
import com.avogine.render.data.mesh.*;
import com.avogine.render.loader.texture.TextureLoader;
import com.avogine.util.resource.*;

/**
 *
 */
public class StaticModelLoader {
	
	private static final Map<String, Texture> textureCache = new HashMap<>();
	
	/**
	 * Maximum number of diffuse textures to load from a model.
	 * </p>
	 * This is to facilitate a manageable shader uniform for activating specific texture material types.
	 */
	public static final int MAX_DIFFUSE_TEXTURES = 4;
	/**
	 * Maximum number of specular textures to load from a model.
	 * </p>
	 * This is to facilitate a manageable shader uniform for activating specific texture material types.
	 */
	public static final int MAX_SPECULAR_TEXTURES = 4;
	
	private StaticModelLoader() {
		
	}
	
	/**
	 * @param resourcePath
	 * @param texturesDir
	 * @return
	 */
	public static Model load(String resourcePath, String texturesDir) {
		//return load(resourcePath, texturesDir, Assimp.aiProcess_JoinIdenticalVertices | Assimp.aiProcess_Triangulate | Assimp.aiProcess_FixInfacingNormals, 1);
		return load(resourcePath, texturesDir, Assimp.aiProcess_Triangulate, 1);
	}
	
	/**
	 * @param resourcePath
	 * @param texturesDir
	 * @param flags
	 * @param numberOfInstances
	 * @return
	 */
	public static Model load(String resourcePath, String texturesDir, int flags, int numberOfInstances) {
		AIFileIO fileIo = AIFileIO.create()
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

		AIScene aiScene = Assimp.aiImportFileEx(ResourceConstants.MODEL_PATH + resourcePath, flags, fileIo);
		
		fileIo.OpenProc().free();
		fileIo.CloseProc().free();
		
		if (aiScene == null) {
			throw new RuntimeException("Error loading model: " + resourcePath);
		}

		int numMaterials = aiScene.mNumMaterials();
		PointerBuffer aiMaterials = aiScene.mMaterials();
		List<Material> materials = new ArrayList<>();
		for (int i = 0; i < numMaterials; i++) {
			AIMaterial aiMaterial = AIMaterial.create(aiMaterials.get(i));
			processMaterial(aiMaterial, materials);
		}
		
		List<Mesh> meshes = new ArrayList<>();
		processNode(aiScene.mRootNode(), aiScene, meshes, materials, texturesDir);

		Assimp.aiReleaseImport(aiScene);

		var model = new Model(meshes, materials);

		return model;
	}
	
	protected static void processNode(AINode node, AIScene scene, List<Mesh> meshes, List<Material> materials, String texturesDir) {
		// process all the node's meshes (if any)
		for (int i = 0; i < node.mNumMeshes(); i++) {
			AIMesh mesh = AIMesh.create(scene.mMeshes().get(node.mMeshes().get(i)));
			meshes.add(processMesh(mesh, scene, materials, texturesDir));
		}
		
		// then do the same for each of its children
		for (int i = 0; i < node.mNumChildren(); i++) {
			processNode(AINode.create(node.mChildren().get(i)), scene, meshes, materials, texturesDir);
		}
	}
	
	protected static Mesh processMesh(AIMesh aiMesh, AIScene scene, List<Material> materials, String texturesDir) {
		FloatBuffer vertexData = processVertices(aiMesh);
		IntBuffer indexData = processIndices(aiMesh);
		List<Texture> textures = processTextures(aiMesh, scene, texturesDir);

		//		Material material = new Material();
		int materialIdx = aiMesh.mMaterialIndex();
		// TODO Store material cache to load from?
		//		if (materialIdx >= 0 && materialIdx < materials.size()) {
		//			material = materials.get(materialIdx);
		//		}
		//		mesh.material = material;

		Mesh mesh = new Mesh(vertexData, indexData, materialIdx);

		MemoryUtil.memFree(vertexData);
		MemoryUtil.memFree(indexData);
		return mesh;
	}
	
	protected static List<Texture> processTextures(AIMesh mesh, AIScene scene, String texturesDir) {
		AIMaterial aiMaterial = AIMaterial.create(scene.mMaterials().get(mesh.mMaterialIndex()));
		List<Texture> textures = new ArrayList<>();
		
		int diffuseCount = Assimp.aiGetMaterialTextureCount(aiMaterial, Assimp.aiTextureType_DIFFUSE);
		for (int i = 0; i < Math.min(diffuseCount, MAX_DIFFUSE_TEXTURES); i++) {
			textures.add(processTexture(aiMaterial, texturesDir, TextureType.DIFFUSE, Assimp.aiTextureType_DIFFUSE, i));
		}
		int specularCount = Assimp.aiGetMaterialTextureCount(aiMaterial, Assimp.aiTextureType_SPECULAR);
		for (int i = 0; i < Math.min(specularCount, MAX_SPECULAR_TEXTURES); i++) {
			textures.add(processTexture(aiMaterial, texturesDir, TextureType.SPECULAR, Assimp.aiTextureType_SPECULAR, i));
		}
		
		return textures;
	}

	protected static Texture processTexture(AIMaterial aiMaterial, String texturesDir, TextureType textureType, int assimpTextureType, int index) {
		AIString path = AIString.calloc();
		Assimp.aiGetMaterialTexture(aiMaterial, assimpTextureType, index, path, (IntBuffer) null, null, null, null, null, null);
		String textPath = path.dataString();
		Texture texture = null;
		
		if (textPath != null && !textPath.isBlank()) {
			String textureFile = "";
			if (texturesDir != null && !texturesDir.isBlank()) {
				textureFile += texturesDir + "/";
			}
			textureFile += textPath;
			textureFile = textureFile.replace("//", "/");
			// TODO Reimplement texture cache with "new" Texture class
			texture = textureCache.computeIfAbsent(textureFile, filePath -> new Texture(TextureLoader.loadTexturePro(filePath), textureType));
		}
		return texture;
	}
	
	protected static void processMaterial(AIMaterial aiMaterial, List<Material> materials) {
		AIColor4D color = AIColor4D.create();
		
		Vector3f ambient = Material.DEFAULT_COLOR;
		int result = Assimp.aiGetMaterialColor(aiMaterial, Assimp.AI_MATKEY_COLOR_AMBIENT, Assimp.aiTextureType_NONE, 0, color);
		if (result == 0) {
			ambient = new Vector3f(color.r(), color.g(), color.b());
		}
		
		Vector3f diffuse = Material.DEFAULT_COLOR;
		result = Assimp.aiGetMaterialColor(aiMaterial, Assimp.AI_MATKEY_COLOR_DIFFUSE, Assimp.aiTextureType_NONE, 0, color);
		if (result == 0) {
			diffuse = new Vector3f(color.r(), color.g(), color.b());
		}

		Vector3f specular = Material.DEFAULT_COLOR;
		result = Assimp.aiGetMaterialColor(aiMaterial, Assimp.AI_MATKEY_COLOR_SPECULAR, Assimp.aiTextureType_NONE, 0, color);
		if (result == 0) {
			specular = new Vector3f(color.r(), color.g(), color.b());
		}

		var material = new PBRMaterial(ambient, diffuse, specular, 32f, 1.0f);
		materials.add(material);
	}

	private static FloatBuffer processVertices(AIMesh aiMesh) {
		AIVector3D.Buffer aiPositions = aiMesh.mVertices();
		AIVector3D.Buffer aiNormals = !aiMesh.isNull(AIMesh.MNORMALS) ? aiMesh.mNormals() : null;
		// XXX Potentially support multiple texture coordinates per mesh?
		AIVector3D.Buffer aiTextureCoordinates = !aiMesh.isNull(AIMesh.MTEXTURECOORDS) ? aiMesh.mTextureCoords(0) : null;

		var vertexData = MemoryUtil.memAllocFloat(aiMesh.mNumVertices() * 8);
		
		while (aiPositions.remaining() > 0) {
			AIVector3D aiPosition = aiPositions.get();
			vertexData.put(aiPosition.x());
			vertexData.put(aiPosition.y());
			vertexData.put(aiPosition.z());
			
			if (aiNormals != null) {
				AIVector3D aiNormal = aiNormals.get();
				vertexData.put(aiNormal.x());
				vertexData.put(aiNormal.y());
				vertexData.put(aiNormal.z());
			} else {
				vertexData.put(0f);
				vertexData.put(0f);
				vertexData.put(0f);
			}
			
			if (aiTextureCoordinates != null)  {
				AIVector3D aiTextureCoordinate = aiTextureCoordinates.get();
				vertexData.put(aiTextureCoordinate.x());
				vertexData.put(aiTextureCoordinate.y());
			} else {
				vertexData.put(0f);
				vertexData.put(0f);
			}
		}
		vertexData.flip();
		
		return vertexData;
	}
	
	private static IntBuffer processIndices(AIMesh aiMesh) {
		// As long as the aiProcess_Triangulate flag is being used we should be good to assume that each face is a triangle
		int numFaces = aiMesh.mNumFaces();
		AIFace.Buffer aiFaces = aiMesh.mFaces();
		
		// TODO Investigate whether mNumIndices works on the buffer itself, or if each AIFace has its own value.
		var indexBuffer = MemoryUtil.memAllocInt(numFaces * aiFaces.mNumIndices());

		for (int i = 0; i < numFaces; i++) {
			AIFace aiFace = aiFaces.get(i);
			IntBuffer buffer = aiFace.mIndices();
			indexBuffer.put(buffer);
		}
		indexBuffer.flip();

		return indexBuffer;
	}
	
}
