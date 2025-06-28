package com.avogine.render.data;

import java.util.*;

/**
 * @param materialMeshMap 
 * @param animations 
 */
public record ModelData(Map<Material, List<MeshData>> materialMeshMap, List<Animation> animations) {

}
