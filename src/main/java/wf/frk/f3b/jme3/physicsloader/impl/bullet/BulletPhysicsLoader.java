package wf.frk.f3b.jme3.physicsloader.impl.bullet;

import static wf.frk.f3b.jme3.physicsloader.impl.bullet.RigidBodyUtils.*;

import java.util.concurrent.Callable;

import com.jme3.app.AppTask;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.PhysicsControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.joints.PhysicsJoint;
import wf.frk.f3b.jme3.physicsloader.ConstraintData;
import wf.frk.f3b.jme3.physicsloader.PhysicsData;
import wf.frk.f3b.jme3.physicsloader.PhysicsLoader;
import wf.frk.f3b.jme3.physicsloader.PhysicsLoaderSettings;
import wf.frk.f3b.jme3.physicsloader.rigidbody.RigidBody;
import wf.frk.f3b.jme3.physicsloader.rigidbody.RigidBodyType;
import com.jme3.scene.Spatial;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BulletPhysicsLoader implements PhysicsLoader<PhysicsControl,PhysicsJoint>{
	private static final Logger logger=LogManager.getLogger(BulletPhysicsLoader.class);

	protected boolean useCompoundCapsule=false;
	protected Runner runner;

	/**
	 * Enqueue operations to physics space, useful when using detached threading model.
	 */
	public BulletPhysicsLoader useRunner(Runner ps) {
		runner=ps;
		return this;
	}

	@Override
	public void attachToSpatial(final PhysicsControl obj, final Spatial spatial) {
			
					spatial.addControl(obj);
				
	}

	

	/**
	 *   Use a compound shape instead of CapsuleCollisionShape. See https://hub.jmonkeyengine.org/t/btcapsuleshape-location-isnt-accurate-at-all/35752/15 for more info.
	 * @param v
	 * @return
	 */
	public BulletPhysicsLoader useCompoundCapsule(boolean v) {
		useCompoundCapsule=v;
		return this;
	}

	public boolean useCompoundCapsule() {
		return useCompoundCapsule;
	}

	@Override
	public PhysicsJoint loadConstraint(final PhysicsLoaderSettings settings, final Object a, final Object b, final ConstraintData ct) {
		if(a instanceof RigidBodyControl&&b instanceof RigidBodyControl){
			
						return applyRBConstraint(settings,(RigidBodyControl)a,(RigidBodyControl)b,ct,logger);

					

			
		}
		return null;
	}

	@Override
	public PhysicsControl load(final PhysicsLoaderSettings settings, final Spatial spatial, final PhysicsData data) {
		if(data instanceof RigidBody){
						RigidBody rb=(RigidBody)data;
						if(rb.type==RigidBodyType.GHOST) return loadGhost(settings,spatial,rb,useCompoundCapsule,logger);
						else return loadRB(settings,spatial,rb,useCompoundCapsule,logger);
			
		}
		return null;
	}



}
