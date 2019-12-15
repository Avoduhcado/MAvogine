package com.avogine.core.entity;

import com.avogine.core.render.Mesh;

public interface Renderable {

	public boolean isInsideFrustum();

	public Mesh[] getMeshes();
	
	public void cleanup();
	
}
