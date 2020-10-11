// Generated by delombok at Sat Jul 28 16:45:23 CEST 2018
package wf.frk.f3b.jme3.mergers;

import java.util.Collection;
import java.util.LinkedList;
import wf.frk.f3b.jme3.physicsloader.PhysicsShape;
import wf.frk.f3b.jme3.physicsloader.rigidbody.RigidBody;
import wf.frk.f3b.jme3.physicsloader.rigidbody.RigidBodyType;
import com.jme3.scene.Node;
import f3b.Datas.Data;
import f3b.Physics.Constraint;
import wf.frk.f3b.jme3.F3bContext;
import wf.frk.f3b.jme3.F3bKey;

public class PhysicsMerger implements Merger {
	@Override
	public void apply(Data src, Node root, F3bKey key) {
		F3bContext context=key.getContext();
		for (f3b.Physics.PhysicsData data : src.getPhysicsList()) {
			if (data.getRigidbody() != null) loadRB(data.getRigidbody(), context);
			if (data.getConstraint() != null) loadCT(data.getConstraint(), context);
		}
	}

	protected void loadCT(Constraint f3bct, F3bContext context) {
		Collection<Constraint> constraints = context.get("G~constraints");
		if (constraints == null) {
			constraints = new LinkedList<Constraint>();
			context.put("G~constraints", constraints);
		}
		constraints.add(f3bct);
		// do parsing during linking.
	}

	protected void loadRB(f3b.Physics.RigidBody f3brb, F3bContext context) {
		RigidBody rb = new RigidBody();
		rb.type = RigidBodyType.values()[f3brb.getType().ordinal()];
		rb.shape = PhysicsShape.values()[f3brb.getShape().ordinal()];
		rb.mass = f3brb.getMass();
		rb.friction = f3brb.getFriction();
		rb.angularDamping = f3brb.getAngularDamping();
		rb.linearDamping = f3brb.getLinearDamping();
		rb.margin = f3brb.getMargin();
		rb.restitution = f3brb.getRestitution();
		rb.angularFactor = wf.frk.f3b.jme3.ext.f3b.TypesExt.toJME(f3brb.getAngularFactor());
		rb.linearFactor = wf.frk.f3b.jme3.ext.f3b.TypesExt.toJME(f3brb.getLinearFactor());
		rb.isKinematic = f3brb.getIsKinematic();
		rb.collisionGroup = f3brb.getCollisionGroup();
		rb.collisionMask = f3brb.getCollisionMask();
		String id = f3brb.getId();
		context.put(id, rb);
	}
}