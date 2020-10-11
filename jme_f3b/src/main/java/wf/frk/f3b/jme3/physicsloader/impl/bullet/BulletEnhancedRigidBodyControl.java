package wf.frk.f3b.jme3.physicsloader.impl.bullet;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import wf.frk.f3b.jme3.physicsloader.EnhancedRigidBodyControl;
import wf.frk.f3b.jme3.physicsloader.PhysicsLoader;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;

public class BulletEnhancedRigidBodyControl extends RigidBodyControl implements EnhancedRigidBodyControl{
	/** Features
	  Allow spatial to be moved with setLocal* methods when detached from ps, useful when contained in nodes
	  Prevent bulletappstate from resetting gravity settings applied before the rb was attached to ps
	  Allow kinematic rb to be moved with setLocal* methods.
	 */
	protected AtomicBoolean locationUpdatedFromPhysicsK=new AtomicBoolean(false),rotationUpdatedFromPhysicsK=new AtomicBoolean(false);
	protected boolean updateGravity=true,updateTransformFromSpatial=true;
	protected PhysicsLoader loader;

	public BulletEnhancedRigidBodyControl(){
		super();
	}

	public BulletEnhancedRigidBodyControl(PhysicsLoader loader,float mass){
		super(mass);
		this.loader=loader;
	}

	public BulletEnhancedRigidBodyControl(PhysicsLoader loader,CollisionShape shape){
		super(shape);
		this.loader=loader;
	}

	public BulletEnhancedRigidBodyControl(PhysicsLoader loader,CollisionShape shape,float mass){
		super(shape,mass);
		this.loader=loader;
	}

	@Override
	public BulletEnhancedRigidBodyControl jmeClone() {
		try{
 
							BulletEnhancedRigidBodyControl control=new BulletEnhancedRigidBodyControl(loader,collisionShape,mass);
					control.locationUpdatedFromPhysicsK=new AtomicBoolean(locationUpdatedFromPhysicsK.get());
					control.rotationUpdatedFromPhysicsK=new AtomicBoolean(rotationUpdatedFromPhysicsK.get());
					//		control.updateGravity=true;
					//		control.updateTransformFromSpatial=true;
			
					control.spatial=spatial;
					control.setAngularFactor(getAngularFactor());
					control.setAngularSleepingThreshold(getAngularSleepingThreshold());
					control.setCcdMotionThreshold(getCcdMotionThreshold());
					control.setCcdSweptSphereRadius(getCcdSweptSphereRadius());
					control.setCollideWithGroups(getCollideWithGroups());
					control.setCollisionGroup(getCollisionGroup());
					control.setDamping(getLinearDamping(),getAngularDamping());
					control.setFriction(getFriction());
					control.setGravity(getGravity());
					control.setKinematic(isKinematic());
					control.setKinematicSpatial(isKinematicSpatial());
					control.setLinearSleepingThreshold(getLinearSleepingThreshold());
					control.setPhysicsLocation(getPhysicsLocation(null));
					control.setPhysicsRotation(getPhysicsRotationMatrix(null));
					control.setRestitution(getRestitution());
					if(mass>0){
						control.setAngularVelocity(getAngularVelocity());
						control.setLinearVelocity(getLinearVelocity());
					}
					control.setApplyPhysicsLocal(isApplyPhysicsLocal());
					return control;

				

		}catch(Exception e){
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}



	@Override
	public Control cloneForSpatial(final Spatial spatial) {
		throw new UnsupportedOperationException();

	}

	protected Vector3f GRAVITY=null;

	public boolean hasCustomGravity() {
		return GRAVITY!=null;
	}

	@Override
	public void setPhysicsSpace(PhysicsSpace space) {
		super.setPhysicsSpace(space);
		if(space!=null&&spatial!=null){
			updateTransformFromSpatial=true;
			updateGravity=true;
		}
	}

	@Override
	public Vector3f getGravity() {
		if(space==null) return GRAVITY;
		else return super.getGravity();
	}

	@Override
	public void setGravity(Vector3f force) {
		GRAVITY=force;
		if(space!=null) super.setGravity(force);
	}

	private Vector3f getSpatialTranslation() {
		if(motionState.isApplyPhysicsLocal()){ return spatial.getLocalTranslation(); }

		return spatial.getWorldTranslation();
	}

	private Quaternion getSpatialRotation() {
		if(motionState.isApplyPhysicsLocal()){ return spatial.getLocalRotation(); }
		return spatial.getWorldRotation();
	}

	@Override
	public void setPhysicsLocation(Vector3f v) {
		super.setPhysicsLocation(v);
		if(isKinematic()){
			locationUpdatedFromPhysicsK.set(true);
		}
	}

	@Override
	public void setPhysicsRotation(Quaternion r) {
		super.setPhysicsRotation(r);
		if(isKinematic()){
			rotationUpdatedFromPhysicsK.set(true);
		}
	}

	@Override
	public void setPhysicsRotation(Matrix3f r) {
		super.setPhysicsRotation(r);

		if(isKinematic()){
			rotationUpdatedFromPhysicsK.set(true);
		}
	}
	
	Vector3f phy_update_gravity;
	Quaternion phy_update_sprotation;
	Vector3f phy_update_splocation;

	@Override
	public void update(float tpf) {
		if(space==null) return;

		

		if(enabled&&spatial!=null){
			if(updateGravity){
				if(GRAVITY!=null){
					phy_update_gravity=(GRAVITY);

			
				}
				updateGravity=false;
			}

			if(updateTransformFromSpatial){
				try{
					phy_update_sprotation=getSpatialRotation();
					phy_update_splocation=getSpatialTranslation();

				}catch(Exception e){
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				updateTransformFromSpatial=false;
				return;
			}

			if(isKinematic()&&kinematicSpatial){
				boolean lfp=locationUpdatedFromPhysicsK.getAndSet(false);
				boolean rfp=rotationUpdatedFromPhysicsK.getAndSet(false);
				if(!lfp) 	phy_update_sprotation=getSpatialRotation();
				if(!rfp) phy_update_splocation=getSpatialTranslation();
				if(lfp||rfp) getMotionState().applyTransform(spatial);
			}else{
				getMotionState().applyTransform(spatial);
			}


			try{
				if(phy_update_gravity!=null||phy_update_splocation!=null||phy_update_sprotation!=null){
		
							if(phy_update_gravity!=null) setGravity(phy_update_gravity);
							if(phy_update_splocation!=null) setPhysicsLocation(phy_update_splocation);
							if(phy_update_sprotation!=null) setPhysicsRotation(phy_update_sprotation);
							phy_update_gravity=null;
							phy_update_splocation=null;
							phy_update_sprotation=null;
						
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}

		
	}

	@Override
	public void write(JmeExporter ex) throws IOException {
		super.write(ex);
		OutputCapsule oc=ex.getCapsule(this);
		oc.write(enabled,"enabled",true);
		oc.write(motionState.isApplyPhysicsLocal(),"applyLocalPhysics",false);
		oc.write(kinematicSpatial,"kinematicSpatial",true);
		oc.write(spatial,"spatial",null);
	}

	@Override
	public void read(JmeImporter im) throws IOException {
		super.read(im);
		InputCapsule ic=im.getCapsule(this);
		enabled=ic.readBoolean("enabled",true);
		kinematicSpatial=ic.readBoolean("kinematicSpatial",true);
		spatial=(Spatial)ic.readSavable("spatial",null);
		motionState.setApplyPhysicsLocal(ic.readBoolean("applyLocalPhysics",false));
		setUserObject(spatial);
	}
}
