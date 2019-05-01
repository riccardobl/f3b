package wf.frk.f3b.jme3.physicsloader.constraint;

import static wf.frk.f3b.jme3.physicsloader.SerializationHelper.readVec3;
import static wf.frk.f3b.jme3.physicsloader.SerializationHelper.writeVec3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.jme3.math.Vector3f;
import wf.frk.f3b.jme3.physicsloader.ConstraintData;
import wf.frk.f3b.jme3.physicsloader.PhysicsData;

public class GenericConstraint implements ConstraintData{
	public Vector3f pivotA=new Vector3f(),
	pivotB=new Vector3f();
	public Vector3f 
	upperLinearLimit=Vector3f.POSITIVE_INFINITY.clone(),
	lowerLinearLimit=Vector3f.NEGATIVE_INFINITY.clone(),
	upperAngularLimit=Vector3f.POSITIVE_INFINITY.clone(),
	lowerAngularLimit=Vector3f.NEGATIVE_INFINITY.clone();
	public boolean disableCollisionsBetweenLinkedNodes=false;
	
	@Override
	public void write(OutputStream os) throws IOException {	
		DataOutputStream dos=new DataOutputStream(os);
		writeVec3(pivotA,dos);
		writeVec3(pivotB,dos);
		writeVec3(upperLinearLimit,dos);
		writeVec3(lowerLinearLimit,dos);
		writeVec3(upperAngularLimit,dos);
		writeVec3(lowerAngularLimit,dos);
		dos.writeByte(disableCollisionsBetweenLinkedNodes?1:0);
	}

	@Override
	public void read(InputStream is) throws IOException {
		DataInputStream dis=new DataInputStream(is);
		pivotA=readVec3(dis);
		pivotB=readVec3(dis);
		upperLinearLimit=readVec3(dis);
		lowerLinearLimit=readVec3(dis);
		upperAngularLimit=readVec3(dis);
		lowerAngularLimit=readVec3(dis);
		disableCollisionsBetweenLinkedNodes=is.read()==1;
	}
	
	@Override
	public String toString(){
		StringBuilder out=new StringBuilder();
		out.append("GenericConstraint[ Pivots:");
		out.append(pivotA.toString());
		out.append(", ");
		out.append(pivotB.toString());
		out.append(" Linear limit: From: ");
		out.append(upperLinearLimit.toString());
		out.append(" To: ");
		out.append(lowerLinearLimit.toString());
		out.append(" Angular limit: From: ");
		out.append(upperAngularLimit.toString());
		out.append(" To: ");
		out.append(lowerAngularLimit.toString());		
		out.append(", disableCollisions: ").append(disableCollisionsBetweenLinkedNodes);
		return out.toString();		
	}

//	@Override
//	public void pivotA(Vector3f pivotA) {
//		this.pivotA=pivotA;		
//	}
//
//	@Override
//	public void pivotB(Vector3f pivotB) {
//		this.pivotB=pivotB;
//	}
}
