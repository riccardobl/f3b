// Generated by delombok at Sat Jul 28 16:45:23 CEST 2018
package wf.frk.f3b.jme3;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import wf.frk.f3b.jme3.physicsloader.PhysicsLoader;
import wf.frk.f3b.jme3.physicsloader.constraint.GenericConstraint;
import wf.frk.f3b.jme3.physicsloader.impl.PhysicsLoaderModelKey;
import wf.frk.f3b.jme3.physicsloader.impl.bullet.BulletPhysicsLoader;

public class F3bKey extends PhysicsLoaderModelKey<F3bKey> implements F3bPhysicsLoaderSettings {
	@java.lang.SuppressWarnings("all")
	private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(F3bKey.class);
	protected NodeBuilder NODE_BUILDER = new DefaultNodeBuilder();

	public F3bKey(){}
	


	public F3bKey(String s) {
		super(s);
	}


	public F3bKey useNodeBuilder(NodeBuilder b) {
		NODE_BUILDER = b;
		return this;
	}

	public NodeBuilder getNodeBuilder() {
		return NODE_BUILDER;
	}

	@Override
	public F3bKey usePhysics(PhysicsLoader<?, ?> l) {
		if (l != null && !(l instanceof BulletPhysicsLoader)) {
			log.warn("Cannot use {}, physicsloader not supported", l.getClass());
			return this;
		}
		super.usePhysics(l);
		return this;
	}

	private static final Class<?>[] supportedConstraints = {GenericConstraint.class};

	@Override
	public Class<?>[] getSupportedConstraints() {
		return supportedConstraints;
	}
}
