package wf.frk.f3b.jme3.physicsloader;

import java.nio.ByteBuffer;

import com.jme3.export.Savable;

public interface PhysicsCacher{
	public void store(Savable key,Savable data);
	public <T extends Savable> T  load(Savable key);
	public void delete(Savable key);
}
