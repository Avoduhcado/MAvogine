package com.avogine.render.loader.assimp;

import java.lang.Math;
import java.nio.*;
import java.util.*;

import org.joml.*;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.system.MemoryUtil;

import com.avogine.logging.AvoLog;
import com.avogine.render.data.material.*;
import com.avogine.render.data.mesh.*;
import com.avogine.render.data.texture.Texture;
import com.avogine.render.loader.texture.TextureCache;
import com.avogine.util.resource.*;

/**
 *
 */
public class StaticModelLoader {
	
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
			processMaterial(aiMaterial, texturesDir, materials);
		}
		
		List<Mesh> meshes = new ArrayList<>();
		processNode(aiScene.mRootNode(), aiScene, meshes, materials);

		Assimp.aiReleaseImport(aiScene);

		var model = new Model(meshes, materials);

		return model;
	}
	
	protected static void processNode(AINode node, AIScene scene, List<Mesh> meshes, List<Material> materials) {
		// process all the node's meshes (if any)
		for (int i = 0; i < node.mNumMeshes(); i++) {
			AIMesh mesh = AIMesh.create(scene.mMeshes().get(node.mMeshes().get(i)));
			meshes.add(processMesh(mesh, scene, materials));
		}
		
		// then do the same for each of its children
		for (int i = 0; i < node.mNumChildren(); i++) {
			processNode(AINode.create(node.mChildren().get(i)), scene, meshes, materials);
		}
	}
	
	protected static Mesh processMesh(AIMesh aiMesh, AIScene scene, List<Material> materials) {
		FloatBuffer vertexData = processVertices(aiMesh);
		IntBuffer indexData = processIndices(aiMesh);

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
			textures.add(processTexture(aiMaterial, texturesDir, Assimp.aiTextureType_DIFFUSE, i));
		}
		int specularCount = Assimp.aiGetMaterialTextureCount(aiMaterial, Assimp.aiTextureType_SPECULAR);
		for (int i = 0; i < Math.min(specularCount, MAX_SPECULAR_TEXTURES); i++) {
			textures.add(processTexture(aiMaterial, texturesDir, Assimp.aiTextureType_SPECULAR, i));
		}
		
		return textures;
	}

	protected static Texture processTexture(AIMaterial aiMaterial, String texturesDir, int assimpTextureType, int index) {
		AIString path = AIString.calloc();
		Texture texture = null;
		if (Assimp.aiGetMaterialTexture(aiMaterial, assimpTextureType, index, path, (IntBuffer) null, null, null, null, null, null) == Assimp.aiReturn_SUCCESS) {
			String textPath = path.dataString();
			
			if (textPath != null && !textPath.isBlank()) {
				String textureFile = "";
				if (texturesDir != null && !texturesDir.isBlank()) {
					textureFile += texturesDir + "/";
				}
				textureFile += textPath;
				textureFile = textureFile.replace("//", "/");
				texture = TextureCache.getInstance().getTexture(textureFile);
			}
		}
		return texture;
	}
	
	protected static void processMaterial(AIMaterial aiMaterial, String texturesDirectory, List<Material> materials) {
		AIColor4D color = AIColor4D.create();
		int result;

		Texture diffuseTexture = null;
		if (Assimp.aiGetMaterialTextureCount(aiMaterial, Assimp.aiTextureType_DIFFUSE) > 0) {
			diffuseTexture = processTexture(aiMaterial, texturesDirectory, Assimp.aiTextureType_DIFFUSE, 0);
		}
		
		Vector3f diffuse = Material.DEFAULT_COLOR;
		result = Assimp.aiGetMaterialColor(aiMaterial, Assimp.AI_MATKEY_COLOR_DIFFUSE, Assimp.aiTextureType_NONE, 0, color);
		if (result == Assimp.aiReturn_SUCCESS) {
			diffuse = new Vector3f(color.r(), color.g(), color.b());
		}
		
		Texture specularTexture = null;
		if (Assimp.aiGetMaterialTextureCount(aiMaterial, Assimp.aiTextureType_SPECULAR) > 0) {
			specularTexture = processTexture(aiMaterial, texturesDirectory, Assimp.aiTextureType_SPECULAR, 0);
		}
		
		Vector3f specular = Material.DEFAULT_COLOR;
		result = Assimp.aiGetMaterialColor(aiMaterial, Assimp.AI_MATKEY_COLOR_SPECULAR, Assimp.aiTextureType_NONE, 0, color);
		if (result == Assimp.aiReturn_SUCCESS) {
			specular = new Vector3f(color.r(), color.g(), color.b());
		}

		Vector3f ambient = Material.DEFAULT_COLOR;
		result = Assimp.aiGetMaterialColor(aiMaterial, Assimp.AI_MATKEY_COLOR_AMBIENT, Assimp.aiTextureType_NONE, 0, color);
		if (result == Assimp.aiReturn_SUCCESS) {
			ambient = new Vector3f(color.r(), color.g(), color.b());
		}
		
		Texture normalMap = null;
		if (Assimp.aiGetMaterialTextureCount(aiMaterial, Assimp.aiTextureType_NORMALS) > 0) {
			normalMap = processTexture(aiMaterial, texturesDirectory, Assimp.aiTextureType_NORMALS, 0);
		}

		AIUVTransform uvTransform = AIUVTransform.calloc();
		Matrix3f uvMatrix = new Matrix3f();
		if (Assimp.aiGetMaterialUVTransform(aiMaterial, Assimp._AI_MATKEY_UVTRANSFORM_BASE, Assimp.aiTextureType_DIFFUSE, 0, uvTransform) == Assimp.aiReturn_SUCCESS) {
			uvMatrix.rotateZ(uvTransform.mRotation()).scale(1 / uvTransform.mScaling().x(), 1 / uvTransform.mScaling().y(), 1);
			uvMatrix.setColumn(2, uvTransform.mTranslation().x(), uvTransform.mTranslation().y(), 1.0f);
		}
		
		float shininess = 32f;
		float reflectance = 1.0f;
		
//		PointerBuffer properties = aiMaterial.mProperties(); // array of pointers to AIMaterialProperty structs
//		for ( int j = 0; j < properties.remaining(); j++ ) {
//			AIMaterialProperty prop = AIMaterialProperty.create(properties.get(j));
//			readProperty(prop);
//		}

		PointerBuffer shinyPointer = MemoryUtil.memAllocPointer(1);
		Assimp.aiGetMaterialProperty(aiMaterial, Assimp.AI_MATKEY_SHININESS, shinyPointer); // Shininess
		if (shinyPointer != null) {
			AIMaterialProperty shininessProperty = AIMaterialProperty.create(shinyPointer.get());
			shininess = shininessProperty.mData().asFloatBuffer().get();
		}
		PointerBuffer reflectPointer = MemoryUtil.memAllocPointer(1);
		Assimp.aiGetMaterialProperty(aiMaterial, Assimp.AI_MATKEY_REFLECTIVITY, reflectPointer); // Reflectance
		if (reflectPointer != null) {
			AIMaterialProperty reflectivityProperty = AIMaterialProperty.create(reflectPointer.get());
			reflectance = reflectivityProperty.mData().asFloatBuffer().get();
		}

		var material = new PBRMaterial(diffuseTexture, diffuse, specularTexture, specular, ambient, shininess, reflectance, normalMap, uvMatrix);
		materials.add(material);
	}

	private static FloatBuffer processVertices(AIMesh aiMesh) {
		AIVector3D.Buffer aiPositions = aiMesh.mVertices();
		AIVector3D.Buffer aiNormals = !aiMesh.isNull(AIMesh.MNORMALS) ? aiMesh.mNormals() : null;
		// XXX Potentially support multiple texture coordinates per mesh?
		AIVector3D.Buffer aiTextureCoordinates = !aiMesh.isNull(AIMesh.MTEXTURECOORDS) ? aiMesh.mTextureCoords(0) : null;

		var vertexData = MemoryUtil.memAllocFloat(aiMesh.mNumVertices() * Mesh.VERTEX_SIZE);
		
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
	
	private static FloatBuffer processVerticesPro(AIMesh aiMesh) {
		AIVector3D.Buffer aiPositions = aiMesh.mVertices();
		AIVector3D.Buffer aiNormals = !aiMesh.isNull(AIMesh.MNORMALS) ? aiMesh.mNormals() : null;
//		AIVector3D.Buffer aiTangents = !aiMesh.isNull(AIMesh.MTANGENTS) ? aiMesh.mTangents() : null; 
		// XXX Potentially support multiple texture coordinates per mesh?
		AIVector3D.Buffer aiTextureCoordinates = !aiMesh.isNull(AIMesh.MTEXTURECOORDS) ? aiMesh.mTextureCoords(0) : null;

		List<Vector3f> positions = new ArrayList<>();
		List<Vector3f> normals = new ArrayList<>();
		List<Vector2f> uvs = new ArrayList<>();
		
		while (aiPositions.remaining() > 0) {
			AIVector3D aiPosition = aiPositions.get();
			positions.add(new Vector3f(aiPosition.x(), aiPosition.y(), aiPosition.z()));
			
			if (aiNormals != null) {
				AIVector3D aiNormal = aiNormals.get();
				normals.add(new Vector3f(aiNormal.x(), aiNormal.y(), aiNormal.z()));
			} else {
				normals.add(new Vector3f(0f));
			}
			
			if (aiTextureCoordinates != null)  {
				AIVector3D aiTextureCoordinate = aiTextureCoordinates.get();
				uvs.add(new Vector2f(aiTextureCoordinate.x(), aiTextureCoordinate.y()));
			} else {
				uvs.add(new Vector2f(0f));
			}
		}
		
		// Compute tangent basis
		for (int i = 0; i < positions.size(); i += 3) {
			var v0 = positions.get(i);
			var v1 = positions.get(i + 1);
			var v2 = positions.get(i + 2);
			
			var uv0 = uvs.get(i);
			var uv1 = uvs.get(i + 1);
			var uv2 = uvs.get(i + 2);
			
			// Edges of the triangle : position delta
			Vector3f deltaPos1 = v1.sub(v0, new Vector3f());
			Vector3f deltaPos2 = v2.sub(v0, new Vector3f());
			
			// UV Delta
			Vector2f deltaUV1 = uv1.sub(uv0, new Vector2f());
			Vector2f deltaUV2 = uv2.sub(uv0, new Vector2f());
			
			float r = 1.0f / (deltaUV1.x * deltaUV2.y - deltaUV1.y * deltaUV2.x);
			Vector3f tangent = (deltaPos1.mul(deltaUV2.y, new Vector3f()).sub(deltaPos2.mul(deltaUV1.y, new Vector3f()), new Vector3f())).mul(r, new Vector3f());
			Vector3f bitangent = (deltaPos2.mul(deltaUV1.x, new Vector3f()).sub(deltaPos1.mul(deltaUV2.x, new Vector3f()), new Vector3f())).mul(r, new Vector3f());
		}

		var vertexData = MemoryUtil.memAllocFloat(aiMesh.mNumVertices() * (Mesh.VERTEX_SIZE - 3));
		
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
	
	protected static void readProperty(AIMaterialProperty prop) {
		switch (prop.mType()) {
		case Assimp.aiPTI_Float -> {
			FloatBuffer data = prop.mData().asFloatBuffer();
			float[] values = new float[prop.mDataLength() / Float.BYTES];
			for (int i = 0; i < values.length; i++) {
				values[i] = data.get();
			}
			AvoLog.log().debug("Prop: {} Float: {}", prop.mKey().dataString(), values);
		}
		case Assimp.aiPTI_Double -> {
			DoubleBuffer data = prop.mData().asDoubleBuffer();
			double[] values = new double[prop.mDataLength() / Double.BYTES];
			for (int i = 0; i < values.length; i++) {
				values[i] = data.get();
			}
			AvoLog.log().debug("Prop: {} Double: {}", prop.mKey().dataString(), values);
		}
		case Assimp.aiPTI_String -> {
			AIString data = AIString.calloc();
			data.data(prop.mData());
			AvoLog.log().debug("Prop: {} String: {}", prop.mKey().dataString(), data.dataString());
		}
		case Assimp.aiPTI_Integer -> {
			IntBuffer data = prop.mData().asIntBuffer();
			int[] values = new int[prop.mDataLength() / Integer.BYTES];
			for (int i = 0; i < values.length; i++) {
				values[i] = data.get();
			}
			AvoLog.log().debug("Prop: {} Int: {}", prop.mKey().dataString(), values);
		}
		case Assimp.aiPTI_Buffer -> {
			AvoLog.log().debug("Prop: {} Buffer: Length: {}", prop.mKey().dataString(), prop.mDataLength());
		}
		default -> throw new IllegalArgumentException("Unexpected value: " + prop.mType());
		};
	}
	
}
