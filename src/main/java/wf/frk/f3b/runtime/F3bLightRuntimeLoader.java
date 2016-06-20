package wf.frk.f3b.runtime;

import java.util.ArrayList;

import com.jme3.light.Light;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;

import lombok.extern.log4j.Log4j2;
import wf.frk.f3b.scene.F3bLightControl;

@Log4j2
public class F3bLightRuntimeLoader{
	public static void unload(final Spatial rootNode, Spatial scene) {
		final ArrayList<F3bLightControl> controls=new ArrayList<F3bLightControl>();
		scene.depthFirstTraversal(new SceneGraphVisitor(){
			@Override
			public void visit(Spatial s) {
				F3bLightControl c=s.getControl(F3bLightControl.class);
				if(c!=null) controls.add(c);
			}
		});
		for(Light l:rootNode.getLocalLightList()){
			for(F3bLightControl c:controls){
				if(c.getLight()==l){
					rootNode.removeLight(l);
				}
			}
		}
	}

	public static void load(final Spatial rootNode, Spatial scene) {
		log.debug("Load lights for {}",scene);
		scene.depthFirstTraversal(new SceneGraphVisitor(){
			@Override
			public void visit(Spatial s) {
				for(Light l:s.getLocalLightList()){
					//					Spatial debug=Debug.makeCube(0.4f,ColorRGBA.Red);
					//					debug.setLocalTranslation(s.getWorldTranslation());

					s.removeLight(l);
					s.addControl(new F3bLightControl(l));
					rootNode.addLight(l);
				}
			}
		});
	}
}
