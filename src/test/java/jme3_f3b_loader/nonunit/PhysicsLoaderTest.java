package jme3_f3b_loader.nonunit;

import java.io.File;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.joints.PhysicsJoint;
import com.jme3.bullet.vhacd.VHACDCollisionShapeFactory;
import com.jme3.bullet.vhacd.cache.PersistentByBuffersCaching;
import com.jme3.math.Vector3f;
import com.jme3.physicsloader.impl.bullet.BulletPhysicsLoader;
import com.jme3.scene.Spatial;

import vhacd.VHACDParameters;

public class PhysicsLoaderTest extends SimpleApplication{
//
//
//
	@Override
	public void simpleInitApp() {
//		cam.setLocation(new Vector3f(0,100,100));
//		cam.lookAt(Vector3f.ZERO,Vector3f.UNIT_Y);
//		Commons.initApp(this);
//		assetManager.registerLoader(F3bLoader.class,"xbuf");
//
//		BulletAppState bullet=stateManager.getState(BulletAppState.class);
//		bullet.setDebugEnabled(true);
//
//		// #############
//		new File("cache").mkdirs();
//
//		VHACDCollisionShapeFactory vhacd=new VHACDCollisionShapeFactory();
//		PersistentByBuffersCaching caching=new PersistentByBuffersCaching("cache");
//		vhacd.cachingQueue().add(caching);
//		
//		VHACDParameters vhacd_params=new VHACDParameters();
//		vhacd_params.setMaxVerticesPerHull(10);
//		vhacd.setParameters(vhacd_params);
//		F3bKey key=new F3bKey("models/physicsScene2/physicsScene2P.xbuf").usePhysics(new BulletPhysicsLoader().useCompoundCapsule(true))
//				.useVHACD(vhacd)
//				.useEnhancedRigidbodies(true)
//				.useLightControls(true);
//		Spatial xbuf_scene=assetManager.loadModel(key);
//		F3bPhysicsLoader.load(key,xbuf_scene,bullet.getPhysicsSpace());
//
//		rootNode.attachChild(xbuf_scene);
	}
//
	public static void main(String[] args) {
		new PhysicsLoaderTest().start();
	}
}
