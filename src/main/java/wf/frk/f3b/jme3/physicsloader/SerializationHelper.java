package wf.frk.f3b.jme3.physicsloader;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.jme3.math.Vector3f;

public class SerializationHelper{
	public static void writeVec3(Vector3f v,DataOutputStream out) throws IOException{
		out.writeFloat(v.x);
		out.writeFloat(v.y);
		out.writeFloat(v.z);
	}
	
	public static Vector3f readVec3(DataInputStream is) throws IOException{
		Vector3f out=new Vector3f();
		out.x=is.readFloat();
		out.y=is.readFloat();
		out.z=is.readFloat();
		return out;
	}
}
