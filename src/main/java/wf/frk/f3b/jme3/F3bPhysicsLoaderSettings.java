package wf.frk.f3b.jme3;

import com.jme3.physicsloader.PhysicsLoaderSettings;

public interface F3bPhysicsLoaderSettings extends PhysicsLoaderSettings{
	public Class<?>[] getSupportedConstraints();
}
