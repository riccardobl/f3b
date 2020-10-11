package wf.frk.f3b.jme3.physicsloader.rigidbody;

import static wf.frk.f3b.jme3.physicsloader.SerializationHelper.readVec3;
import static wf.frk.f3b.jme3.physicsloader.SerializationHelper.writeVec3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.jme3.math.Vector3f;
import wf.frk.f3b.jme3.physicsloader.PhysicsData;
import wf.frk.f3b.jme3.physicsloader.PhysicsShape;

public class RigidBody implements PhysicsData{

	public RigidBodyType type=RigidBodyType.NONE;
	public PhysicsShape shape=PhysicsShape.MESH;
	public float mass=1.f,friction=1.f,
			angularDamping=0f, 
			linearDamping=0f,
			margin=0f,restitution=0f;
	public Vector3f angularFactor=new Vector3f(1.0f,1.0f,1.0f),
			linearFactor=new Vector3f(1.0f,1.0f,1.0f);
	public boolean isKinematic=false;
	public int collisionMask=1,collisionGroup=1;
	
	@Override
	public void write(OutputStream os) throws IOException {
		DataOutputStream dos=new DataOutputStream(os);
//		ByteBuffer bbf=ByteBuffer.allocate(1*2+4*6+(4*3*2)+1+4*2);
		dos.write((byte)type.ordinal());
		dos.write((byte)shape.ordinal());
		dos.writeFloat(mass);
		dos.writeFloat(friction);
		dos.writeFloat(angularDamping);
		dos.writeFloat(linearDamping);
		dos.writeFloat(margin);
		dos.writeFloat(restitution);
		
		writeVec3(angularFactor,dos);
	
		writeVec3(linearFactor,dos);

		dos.write((byte)(isKinematic?1:0));		
		dos.writeInt(collisionMask);
		dos.writeInt(collisionGroup);
	}
	
	@Override
	public void read(InputStream is) throws IOException  {
		DataInputStream dis=new DataInputStream(is);
        type=RigidBodyType.values()[dis.readByte()];
        shape=PhysicsShape.values()[dis.readByte()];
        mass=dis.readFloat();
        friction=dis.readFloat();
        angularDamping=dis.readFloat();
        linearDamping=dis.readFloat();
        margin=dis.readFloat();
        restitution=dis.readFloat();
        
        angularFactor=	readVec3(dis);

        linearFactor=	readVec3(dis);
        
        isKinematic=dis.readByte()==1;
        collisionMask=dis.readInt();
        collisionGroup=dis.readInt();
	}

}
