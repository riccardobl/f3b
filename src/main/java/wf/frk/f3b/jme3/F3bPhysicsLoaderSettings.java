package wf.frk.f3b.jme3;

import wf.frk.f3b.jme3.physicsloader.PhysicsLoaderSettings;

public interface F3bPhysicsLoaderSettings extends PhysicsLoaderSettings{
	public Class<?>[] getSupportedConstraints();
}
