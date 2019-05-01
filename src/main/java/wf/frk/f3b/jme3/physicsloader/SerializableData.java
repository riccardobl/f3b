package wf.frk.f3b.jme3.physicsloader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface SerializableData{
	public void write(OutputStream os) throws IOException ;
	public void read(InputStream is)throws IOException ;
}
