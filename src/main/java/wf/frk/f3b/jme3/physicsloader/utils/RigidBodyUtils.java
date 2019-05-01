package wf.frk.f3b.jme3.physicsloader.utils;

import java.util.logging.Logger;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.joints.PhysicsJoint;
import com.jme3.bullet.joints.SixDofJoint;

import wf.frk.f3b.jme3.F3bKey;
import wf.frk.f3b.jme3.F3bPhysicsLoaderSettings;
import wf.frk.f3b.jme3.physicsloader.ConstraintData;
import wf.frk.f3b.jme3.physicsloader.constraint.GenericConstraint;
import wf.frk.f3b.jme3.physicsloader.impl.bullet.CollisionShapeUtils;
import wf.frk.f3b.jme3.physicsloader.rigidbody.RigidBody;
import wf.frk.f3b.jme3.physicsloader.rigidbody.RigidBodyType;
import com.jme3.scene.Spatial;

public class RigidBodyUtils{
	public static PhysicsJoint applyRBConstraint(final F3bPhysicsLoaderSettings settings, final RigidBodyControl a,  final RigidBodyControl b, final ConstraintData data,org.apache.logging.log4j.Logger  logger) {
		if(data instanceof GenericConstraint){
			GenericConstraint generic_constraint=(GenericConstraint)data;
			SixDofJoint joint=new SixDofJoint(a,b,generic_constraint.pivotA,generic_constraint.pivotB,true/*??*/);
			joint.setAngularUpperLimit(generic_constraint.upperAngularLimit);
			joint.setAngularLowerLimit(generic_constraint.lowerAngularLimit);
			joint.setLinearUpperLimit(generic_constraint.upperLinearLimit);
			joint.setLinearLowerLimit(generic_constraint.lowerLinearLimit);
			joint.setCollisionBetweenLinkedBodys(!generic_constraint.disableCollisionsBetweenLinkedNodes);
			return joint;
		}
		 
		return null;
	}
	
	public static GhostControl loadGhost(final F3bPhysicsLoaderSettings settings, final Spatial spatial, final RigidBody data,boolean useCompoundCapsule,org.apache.logging.log4j.Logger  logger) {
		CollisionShape collisionShape=CollisionShapeUtils.buildCollisionShape(settings,spatial,data.shape,	data.type==RigidBodyType.DYNAMIC,useCompoundCapsule,logger);
		if(collisionShape==null) return null;
		collisionShape.setMargin(data.margin);
		GhostControl ghost=new GhostControl(collisionShape);
		return ghost;
	}
	
	public static RigidBodyControl loadRB(final F3bPhysicsLoaderSettings settings, final Spatial spatial, final RigidBody data, boolean useCompoundCapsule, org.apache.logging.log4j.Logger  logger) {
		if(data.type==RigidBodyType.NONE) return null;
		CollisionShape collisionShape=CollisionShapeUtils.buildCollisionShape(settings,spatial,data.shape,data.type==RigidBodyType.DYNAMIC,useCompoundCapsule,logger);
		if(collisionShape==null) return null;
		collisionShape.setMargin(data.margin);

		float mass=data.type==RigidBodyType.STATIC?0:data.mass;
		RigidBodyControl rigidbody=new RigidBodyControl(collisionShape,mass);
			rigidbody.setFriction(data.friction);
			rigidbody.setAngularDamping(data.angularDamping);
			rigidbody.setLinearDamping(data.linearDamping);
			rigidbody.setLinearFactor(data.linearFactor);
			rigidbody.setAngularFactor(data.angularFactor);
			rigidbody.setKinematic(data.isKinematic);
			rigidbody.setRestitution(data.restitution);
			rigidbody.setCollisionGroup(data.collisionGroup);
			rigidbody.setCollideWithGroups(data.collisionMask);
		return rigidbody;

	}

}
