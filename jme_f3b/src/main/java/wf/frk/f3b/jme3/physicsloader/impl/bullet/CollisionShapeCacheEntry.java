package wf.frk.f3b.jme3.physicsloader.impl.bullet;

import java.io.IOException;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;

public class CollisionShapeCacheEntry implements Savable{
	public Savable savable;
	public boolean dynamic,useCompoundCapsule;
	public int type;
	@Override
	public void write(JmeExporter ex) throws IOException {
		OutputCapsule c=ex.getCapsule(this);
		c.write(savable,"savable",null);
		c.write(dynamic,"dynamic",false);
		c.write(useCompoundCapsule,"useCompoundCapsule",false);
		c.write(type,"type",0);

	}
	
	@Override
	public void read(JmeImporter im) throws IOException {
		InputCapsule c=im.getCapsule(this);
		savable=(Savable)c.readSavable("savable",null);
		dynamic=c.readBoolean("dynamic",false);
		useCompoundCapsule=c.readBoolean("useCompoundCapsule",false);
		type=c.readInt("type",0);
	}
}
