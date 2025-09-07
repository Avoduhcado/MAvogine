package com.avogine.render.opengl.shader.uniform.light;

import com.avogine.entity.light.*;
import com.avogine.render.opengl.shader.uniform.*;

/**
 *
 */
public class UniformPointLight extends UniformLight<PointLight> {

	private final UniformVec3 position = new UniformVec3();
	
	/**
	 * @see <a href="http://wiki.ogre3d.org/tiki-index.php?page=-Point+Light+Attenuation">Ogre3D Wiki</a>
	 */
	private final UniformFloat constant = new UniformFloat();
	private final UniformFloat linear = new UniformFloat();
	private final UniformFloat quadratic = new UniformFloat();
	
	@Override
	public void storeUniformLocation(int programID, String name) {
		super.storeUniformLocation(programID, name);
		position.storeUniformLocation(programID, name + ".position");
		
		constant.storeUniformLocation(programID, name + ".constant");
		linear.storeUniformLocation(programID, name + ".linear");
		quadratic.storeUniformLocation(programID, name + ".quadratic");
	}
	
	@Override
	public void loadLight(PointLight light) {
		position.loadVec3(light.getTransformPosition());
		
		ambient.loadVec3(light.getAmbient());
		diffuse.loadVec3(light.getDiffuse());
		specular.loadVec3(light.getSpecular());
		
		constant.loadFloat(light.getConstant());
		linear.loadFloat(light.getLinear());
		quadratic.loadFloat(light.getQuadratic());
	}

}
