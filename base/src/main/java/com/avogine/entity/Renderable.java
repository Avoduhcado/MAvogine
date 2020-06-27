package com.avogine.entity;

import com.avogine.render.data.Mesh;

public interface Renderable {

	public boolean isInsideFrustum();

	public Mesh[] getMeshes();
	
	public void cleanup();
	
}
