package com.avogine.render.loader.assimp;

import java.lang.Math;
import java.lang.invoke.MethodHandles;
import java.nio.*;
import java.util.*;

import org.joml.*;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

import com.avogine.logging.LogUtil;
import com.avogine.render.data.Material;
import com.avogine.render.data.Mesh;
import com.avogine.render.data.Texture;
import com.avogine.render.loader.texture.TextureCache;
import com.avogine.util.resource.ResourceConstants;
import com.avogine.util.resource.ResourceFileReader;

public class StaticMeshesLoader {

	private static final Logger logger = LogUtil.requestLogger(MethodHandles.lookup().lookupClass().getSimpleName());
	
	/**
	 * A custom {@link AIFileIO} implementation to support loading from jar files.
	 */
	protected static final AIFileIO fileIo;

	static {
		fileIo = AIFileIO.create();
		AIFileOpenProcI fileOpenProc = new AIFileOpenProc() {
			public long invoke(long pFileIO, long fileName, long openMode) {
				AIFile aiFile = AIFile.create();
				final ByteBuffer data;
				String fileNameUtf8 = MemoryUtil.memUTF8(fileName);
				//data = ResourceFileReader.readResourceToByteBuffer(fileNameUtf8);
				data = ResourceFileReader.ioResourceToByteBuffer(fileNameUtf8, 8 * 1024);
				//data = IOUtil.ioResourceToByteBuffer(fileNameUtf8, 8 * 1024);
				AIFileReadProcI fileReadProc = new AIFileReadProc() {
					public long invoke(long pFile, long pBuffer, long size, long count) {
						long max = Math.min(data.remaining(), size * count);
						MemoryUtil.memCopy(MemoryUtil.memAddress(data) + data.position(), pBuffer, max);
						return max;
					}
				};
				AIFileSeekI fileSeekProc = new AIFileSeek() {
					public int invoke(long pFile, long offset, int origin) {
						if (origin == Assimp.aiOrigin_CUR) {
							data.position(data.position() + (int) offset);
						} else if (origin == Assimp.aiOrigin_SET) {
							data.position((int) offset);
						} else if (origin == Assimp.aiOrigin_END) {
							data.position(data.limit() + (int) offset);
						}
						return 0;
					}
				};
				AIFileTellProcI fileTellProc = new AIFileTellProc() {
					public long invoke(long pFile) {
						return data.limit();
					}
				};
				aiFile.ReadProc(fileReadProc);
				aiFile.SeekProc(fileSeekProc);
				aiFile.FileSizeProc(fileTellProc);
				return aiFile.address();
			}
		};
		AIFileCloseProcI fileCloseProc = new AIFileCloseProc() {
			public void invoke(long pFileIO, long pFile) {
				/* Nothing to do */
			}
		};
		fileIo.set(fileOpenProc, fileCloseProc, 0);
	}
	
	public static Mesh[] load(String resourcePath, String texturesDir) {
		return load(resourcePath, texturesDir, Assimp.aiProcess_JoinIdenticalVertices | Assimp.aiProcess_Triangulate | Assimp.aiProcess_FixInfacingNormals, 1);
	}
	
	public static Mesh[] load(String resourcePath, String texturesDir, int numberOfInstances) {
		return load(resourcePath, texturesDir, Assimp.aiProcess_JoinIdenticalVertices | Assimp.aiProcess_Triangulate | Assimp.aiProcess_FixInfacingNormals, numberOfInstances);
	}

	public static Mesh[] load(String resourcePath, String texturesDir, int flags, int numberOfInstances) {
		logger.debug("Now loading: {}", resourcePath);
		AIScene aiScene = Assimp.aiImportFileEx(ResourceConstants.MODEL_PATH + resourcePath, flags, fileIo);
		if (aiScene == null) {
			throw new RuntimeException("Error loading model: " + resourcePath);
		}

		int numMaterials = aiScene.mNumMaterials();
		PointerBuffer aiMaterials = aiScene.mMaterials();
		List<Material> materials = new ArrayList<>();
		for (int i = 0; i < numMaterials; i++) {
			AIMaterial aiMaterial = AIMaterial.create(aiMaterials.get(i));
			processMaterial(aiMaterial, materials, texturesDir);
		}

		int numMeshes = aiScene.mNumMeshes();
		PointerBuffer aiMeshes = aiScene.mMeshes();
		Mesh[] meshes = new Mesh[numMeshes];
		for (int i = 0; i < numMeshes; i++) {
			AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
			Mesh mesh = processMesh(aiMesh, materials, numberOfInstances);
			meshes[i] = mesh;
		}

		return meshes;
	}

	protected static void processMaterial(AIMaterial aiMaterial, List<Material> materials, String texturesDir) {
		AIColor4D color = AIColor4D.create();
		
		AIString path = AIString.calloc();
		Assimp.aiGetMaterialTexture(aiMaterial, Assimp.aiTextureType_DIFFUSE, 0, path, (IntBuffer) null, null, null, null, null, null);
		String textPath = path.dataString();
		Texture texture = null;
		if (textPath != null && textPath.length() > 0) {
			TextureCache textCache = TextureCache.getInstance();
			String textureFile = "";
			if (texturesDir != null && texturesDir.length() > 0) {
				textureFile += texturesDir + "/";
			}
			textureFile += textPath;
			textureFile = textureFile.replace("//", "/");
			texture = textCache.getTexture(textureFile);
		}

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

		Material material = new Material(ambient, diffuse, specular, 32f);
		material.setDiffuse(texture);
		materials.add(material);
	}

	private static Mesh processMesh(AIMesh aiMesh, List<Material> materials, int numberOfInstances) {
		FloatBuffer vertexData = processData(aiMesh);
		IntBuffer indexData = processIndexData(aiMesh);
		
		Mesh mesh = new Mesh(vertexData, indexData);
		
		// TODO Bounding box
//		Vector3f min = new Vector3f();
//		Vector3f max = new Vector3f();
//		
//		for (int i = 0; i < vertices.size(); i += 3) {
//			min.x = Math.min(vertices.get(i), min.x);
//			min.y = Math.min(vertices.get(i + 1), min.y);
//			min.z = Math.min(vertices.get(i + 2), min.z);
//			
//			max.x = Math.max(vertices.get(i), max.x);
//			max.y = Math.max(vertices.get(i + 1), max.y);
//			max.z = Math.max(vertices.get(i + 2), max.z);
//		}

		// TODO support multiple materials per mesh
		Material material;
		int materialIdx = aiMesh.mMaterialIndex();
		if (materialIdx >= 0 && materialIdx < materials.size()) {
			material = materials.get(materialIdx);
		} else {
			material = new Material();
		}
		mesh.setMaterial(material);

		return mesh;
	}

	protected static void processVertices(AIMesh aiMesh, List<Float> vertices) {
		AIVector3D.Buffer aiVertices = aiMesh.mVertices();
		while (aiVertices.remaining() > 0) {
			AIVector3D aiVertex = aiVertices.get();
			vertices.add(aiVertex.x());
			vertices.add(aiVertex.y());
			vertices.add(aiVertex.z());
		}
	}

	protected static void processNormals(AIMesh aiMesh, List<Float> normals) {
		AIVector3D.Buffer aiNormals = aiMesh.mNormals();
		while (aiNormals != null && aiNormals.remaining() > 0) {
			AIVector3D aiNormal = aiNormals.get();
			normals.add(aiNormal.x());
			normals.add(aiNormal.y());
			normals.add(aiNormal.z());
		}
	}

	protected static void processTextCoords(AIMesh aiMesh, List<Float> textures) {
		AIVector3D.Buffer textCoords = aiMesh.mTextureCoords(0);
		int numTextCoords = textCoords != null ? textCoords.remaining() : 0;
		for (int i = 0; i < numTextCoords; i++) {
			AIVector3D textCoord = textCoords.get();
			textures.add(textCoord.x());
			// TODO Hum, don't inverse this?
			textures.add(1 - textCoord.y());
		}
	}

	protected static void processIndices(AIMesh aiMesh, List<Integer> indices) {
		int numFaces = aiMesh.mNumFaces();
		AIFace.Buffer aiFaces = aiMesh.mFaces();
		for (int i = 0; i < numFaces; i++) {
			AIFace aiFace = aiFaces.get(i);
			IntBuffer buffer = aiFace.mIndices();
			while (buffer.remaining() > 0) {
				indices.add(buffer.get());
			}
		}
	}
	
	protected static IntBuffer processIndexData(AIMesh aiMesh) {
		int numFaces = aiMesh.mNumFaces();
		AIFace.Buffer aiFaces = aiMesh.mFaces();
		
		IntBuffer indexData = null;
		try {
			// As long as the aiProcess_Triangulate flag is being used we should be good to assume that each face is a triangle
			indexData = MemoryUtil.memAllocInt(numFaces * aiFaces.mNumIndices());
			
			for (int i = 0; i < numFaces; i++) {
				AIFace aiFace = aiFaces.get(i);
				IntBuffer buffer = aiFace.mIndices();
				while (buffer.remaining() > 0) {
					indexData.put(buffer.get());
				}
			}
			indexData.flip();
			
			return indexData;
		} finally {
			if (indexData != null) {
				MemoryUtil.memFree(indexData);
			}
		}
	}
	
	/**
	 * 
	 * @param aiMesh
	 * @return
	 */
	protected static FloatBuffer processData(AIMesh aiMesh) {
		FloatBuffer verticesBuffer = null;
		try {
			verticesBuffer = MemoryUtil.memAllocFloat(aiMesh.mNumVertices() * (3 + 3 + 2));
			AIVector3D.Buffer positions = aiMesh.mVertices();
			AIVector3D.Buffer normals = aiMesh.mNormals();
			// TODO Potentially support multiple texture coordinates per mesh?
			AIVector3D.Buffer textureCoordinates = aiMesh.mTextureCoords().hasRemaining() ? aiMesh.mTextureCoords(0) : null;
			
			for (int i = 0; i < aiMesh.mNumVertices(); i++) {
				// Vertex positions
				AIVector3D vector = positions.get();
				verticesBuffer.put(vector.x());
				verticesBuffer.put(vector.y());
				verticesBuffer.put(vector.z());
				
				// Vertex normals
				vector = normals.get();
				verticesBuffer.put(vector.x());
				verticesBuffer.put(vector.y());
				verticesBuffer.put(vector.z());
				
				// Vertex texture coordinates
				if (textureCoordinates != null) {
					vector = textureCoordinates.get();
					verticesBuffer.put(vector.x());
					verticesBuffer.put(vector.y());
				} else {
					verticesBuffer.put(0.0f);
					verticesBuffer.put(0.0f);
				}
			}
			verticesBuffer.flip();
			
			return verticesBuffer;
		} finally {
			if (verticesBuffer != null) {
				MemoryUtil.memFree(verticesBuffer);
			}
		}
	}
}
