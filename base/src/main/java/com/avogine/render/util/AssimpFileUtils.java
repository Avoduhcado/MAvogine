package com.avogine.render.util;

import static org.lwjgl.system.MemoryUtil.*;

import java.nio.ByteBuffer;
import java.util.*;

import org.joml.Math;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import com.avogine.util.ResourceUtils;

/**
 *
 */
public class AssimpFileUtils {

	private AssimpFileUtils() {}
	
	/**
	 * @param modelPath
	 * @param flags
	 * @return an {@link AIScene} read from the given modelPath, loaded into memory.
	 */
	@SuppressWarnings({
		"java:S2095" // The actual AIFileIO instance holds very little of its own memory which should be fine to be GC'd and its AIFile proc's are being manually freed which provide the bulk of the memory footprint.
	})
	public static AIScene readSceneFromMemory(String modelPath, int flags) {
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
		
		return aiScene;
	}
	
	/**
	 * @param aiScene
	 * @return a list of {@link AIMaterial} data read from the given scene.
	 */
	public static List<AIMaterial> readMaterials(AIScene aiScene) {
		int numMaterials = aiScene.mNumMaterials();
		PointerBuffer materialsBuffer = aiScene.mMaterials();
		List<AIMaterial> aiMaterials = new ArrayList<>(numMaterials);
		for (int i = 0; i < numMaterials; i++) {
			AIMaterial aiMaterial = AIMaterial.create(materialsBuffer.get(i));
			aiMaterials.add(aiMaterial);
		}
		return aiMaterials;
	}
	
	/**
	 * @param aiScene
	 * @return a list of {@link AIMesh} data read from the given scene.
	 */
	public static List<AIMesh> readMeshes(AIScene aiScene) {
		int numMeshes = aiScene.mNumMeshes();
		PointerBuffer meshesBuffer = aiScene.mMeshes();
		List<AIMesh> aiMeshes = new ArrayList<>(numMeshes);
		for (int i = 0; i < numMeshes; i++) {
			AIMesh aiMesh = AIMesh.create(meshesBuffer.get(i));
			aiMeshes.add(aiMesh);
		}
		return aiMeshes;
	}
	
	/**
	 * @param aiScene
	 * @return a list of {@link AIAnimation} data read from the given scene.
	 */
	public static List<AIAnimation> readAnimations(AIScene aiScene) {
		int numAnimations = aiScene.mNumAnimations();
		PointerBuffer animationsBuffer = aiScene.mAnimations();
		List<AIAnimation> aiAnimations = new ArrayList<>();
		for (int i = 0; i < numAnimations; i++) {
			AIAnimation aiAnimation = AIAnimation.create(animationsBuffer.get(i));
			aiAnimations.add(aiAnimation);
		}
		return aiAnimations;
	}
	
}
