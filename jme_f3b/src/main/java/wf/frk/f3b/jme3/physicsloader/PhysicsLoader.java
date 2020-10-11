package wf.frk.f3b.jme3.physicsloader;

import java.util.concurrent.Callable;

import com.jme3.export.Savable;
import com.jme3.scene.Spatial;

public interface PhysicsLoader<PHYSICS_OBJECT extends Savable,PHYSICS_JOINT extends Savable>{
	public PHYSICS_OBJECT load(PhysicsLoaderSettings settings, Spatial spatial, PhysicsData data);
	public void attachToSpatial(PHYSICS_OBJECT obj,Spatial spatial);

	public PHYSICS_JOINT loadConstraint(PhysicsLoaderSettings settings, Object a, Object b, ConstraintData constraint_data);
	
}
 