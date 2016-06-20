package wf.frk.f3b.runtime;

import java.util.Map;
import java.util.WeakHashMap;

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
	
	protected F3bKey key;

	protected Node lightsTargetNode;
	public F3bRuntimeLoader attachLightsTo(Node n) {
		lightsTargetNode=n;
		return this;
	}
	
	protected F3bRuntimeLoader() {
	}
	
	public static F3bRuntimeLoader instance() {
		return new F3bRuntimeLoader();
	}

	
	protected static Map<Spatial,F3bRuntimeLoader>  loaded=new WeakHashMap<Spatial,F3bRuntimeLoader> ();
	public void unload(Spatial s){
		synchronized(loaded){
			F3bRuntimeLoader rt=loaded.get(s);
			rt.i_unload(s);
		}		
	}

	public Spatial load(F3bKey key,Spatial spatialToLoad) {
		this.key=key;
		if(physicsSpace!=null) F3bPhysicsRuntimeLoader.load(key,spatialToLoad,physicsSpace);
		if(lightsTargetNode!=null) F3bLightRuntimeLoader.load(lightsTargetNode,spatialToLoad);
		if(rootNode!=null){
			if(!rootNode.hasChild(spatialToLoad))	rootNode.attachChild(spatialToLoad);
		}
		synchronized(loaded){
			loaded.put(spatialToLoad,this);
		}
		return spatialToLoad;
	}

	public Spatial load(AssetManager assetManager,F3bKey key) {
		Spatial sp=assetManager.loadModel(key);
		load(key,sp);	
		return sp;
	}

	
	protected void i_unload(Spatial spatialToLoad) {
		if(physicsSpace!=null) F3bPhysicsRuntimeLoader.unload(spatialToLoad,physicsSpace);
		if(lightsTargetNode!=null) F3bLightRuntimeLoader.unload(lightsTargetNode,spatialToLoad);
		if(rootNode!=null){
			if(rootNode.hasChild(spatialToLoad))	rootNode.detachChild(spatialToLoad);
		}
	}

}
