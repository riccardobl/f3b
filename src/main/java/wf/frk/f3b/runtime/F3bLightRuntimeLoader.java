package wf.frk.f3b.runtime;

import com.jme3.light.Light;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;

import wf.frk.f3b.debug.Debug;
import wf.frk.f3b.scene.F3bLightControl;

public class F3bLightRuntimeLoader{
	public static void load(final Spatial rootNode, Spatial scene) {
		scene.depthFirstTraversal(new SceneGraphVisitor(){
			@Override
			public void visit(Spatial s) {
				for(Light l:s.getLocalLightList()){
					Spatial debug=Debug.makeCube(0.4f,ColorRGBA.Red);
					debug.setLocalTranslation(s.getWorldTranslation());
					
					s.removeLight(l);
					s.addControl(new F3bLightControl(l));
					rootNode.addLight(l);
				}
			}
		});
	}
}
