package wf.frk.f3b.jme3.scene;

import com.jme3.light.Light;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod({wf.frk.f3b.jme3.ext.jme3.LightExt.class})
@RequiredArgsConstructor
public class F3bLightControl extends AbstractControl{
	protected @Getter final Light light;

	@Override
	public void controlUpdate(float tpf) {
		if(spatial!=null&&light!=null){
			light.setPosition(spatial.getWorldTranslation());
			light.setDirection(spatial.getWorldRotation().mult(Vector3f.UNIT_Z));
		}
	}
//
//	Spatial debug;

	
	@Override
	public void setSpatial(Spatial sp) {
		super.setSpatial(sp);
	
//		debug=Debug.makeCube(0.4f,ColorRGBA.Red);
	}

	@Override
	protected void controlRender(RenderManager rm, ViewPort vp) {

	}

}
