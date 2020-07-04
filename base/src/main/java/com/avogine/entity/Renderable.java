package com.avogine.entity;

import com.avogine.experimental.annotation.InDev;
import com.avogine.render.data.Mesh;

@InDev
public interface Renderable {

	public boolean isInsideFrustum();

	public Mesh[] getMeshes();
	
	public void cleanup();
	
}
