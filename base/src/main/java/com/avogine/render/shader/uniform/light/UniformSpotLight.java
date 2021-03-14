package com.avogine.render.shader.uniform.light;

import com.avogine.entity.light.*;
import com.avogine.render.shader.uniform.*;

/**
 *
 */
public class UniformSpotLight extends UniformLight<SpotLight> {

	private final UniformVec3 position = new UniformVec3();
	private final UniformVec3 direction = new UniformVec3();
	
	private final UniformFloat innerCutOff = new UniformFloat();
	private final UniformFloat outerCutOff = new UniformFloat();

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
		direction.storeUniformLocation(programID, name + ".direction");
		
		innerCutOff.storeUniformLocation(programID, name + ".innerCutOff");
		outerCutOff.storeUniformLocation(programID, name + ".outerCutOff");
		
		constant.storeUniformLocation(programID, name + ".constant");
		linear.storeUniformLocation(programID, name + ".linear");
		quadratic.storeUniformLocation(programID, name + ".quadratic");
	}
	
	@Override
	public void loadLight(SpotLight light) {
		this.position.loadVec3(light.getPosition());
		this.direction.loadVec3(light.getDirection());
		
		this.innerCutOff.loadFloat(light.getInnerCutOffCosineRadians());
		this.outerCutOff.loadFloat(light.getOuterCutOffCosineRadians());
		
		this.ambient.loadVec3(light.getAmbient());
		this.diffuse.loadVec3(light.getDiffuse());
		this.specular.loadVec3(light.getSpecular());
		
		constant.loadFloat(light.getConstant());
		linear.loadFloat(light.getLinear());
		quadratic.loadFloat(light.getQuadratic());
	}
	
}
