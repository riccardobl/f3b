package wf.frk.f3b.jme3.physicsloader.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.WeakHashMap;

import com.jme3.export.Savable;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.export.binary.BinaryImporter;
import wf.frk.f3b.jme3.physicsloader.PhysicsCacher;

public abstract class BasicPhysicsCacher implements PhysicsCacher{
	protected Map<Savable,Savable> CACHE=new WeakHashMap<Savable,Savable>();
	protected final BinaryExporter _EXPORTER=BinaryExporter.getInstance();
	protected final BinaryImporter _IMPORTER=BinaryImporter.getInstance();

	public abstract OutputStream openOutputStream(Savable key);

	public abstract InputStream openInputStream(Savable key);

	@Override
	public void store(Savable key, Savable data) {
		try{
			OutputStream os=openOutputStream(key);
			if(os==null)return;
			_EXPORTER.save(data,os);
			os.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public <T extends Savable> T load(Savable key) {
		try{
			InputStream is=openInputStream(key);
			if(is==null)return null;
			T out= (T)_IMPORTER.load(is);
			is.close();
			return out;
		}catch(IOException e){
			e.printStackTrace();
			return null;
		}

	}

}
