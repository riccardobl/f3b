package wf.frk.f3b.runtime;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import wf.frk.f3b.F3bKey;

public class F3bRuntimeLoader{
	
	protected PhysicsSpace physicsSpace;

	public F3bRuntimeLoader attachPhysicsTo(PhysicsSpace p) {
		physicsSpace=p;
		return this;
	}

	protected Node rootNode;
	public F3bRuntimeLoader attachSceneTo(Node n) {
		rootNode=n;
		return this;
	}

	protected Node lightsTargetNode;
	public F3bRuntimeLoader attachLightsTo(Node n) {
		lightsTargetNode=n;
		return this;
	}
	
	protected F3bKey key;
	protected F3bRuntimeLoader(F3bKey k) {
		key=k;
	}
	
	
	public static F3bRuntimeLoader instance(F3bKey k) {
		return new F3bRuntimeLoader(k);
	}


	public Spatial load(Spatial spatialToLoad) {
		if(physicsSpace!=null) F3bPhysicsRuntimeLoader.load(key,spatialToLoad,physicsSpace);
		if(lightsTargetNode!=null) F3bLightRuntimeLoader.load(lightsTargetNode,spatialToLoad);
		if(rootNode!=null){
			if(!rootNode.hasChild(spatialToLoad))	rootNode.attachChild(spatialToLoad);
		}
		return spatialToLoad;
	}

	public Spatial load(AssetManager assetManager) {
		Spatial sp=assetManager.loadModel(key);
		load(sp);
		if(rootNode!=null)rootNode.attachChild(sp);
		return sp;
	}

}
