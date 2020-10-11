package wf.frk.f3b.jme3.ext.f3b;


import java.util.List;

import f3b.Meshes.FloatBuffer;

public class FloatBufferExt{

	//	//TODO use an optim version: including a patch for no autoboxing : https://code.google.com/p/protobuf/issues/detail?id=464
	public static float[] array(FloatBuffer src) {
		List<Float> list=src.getValuesList();
		float arr[]=new float[list.size()];
		int i=0;
		for(Float f:list)	arr[i++]=(float)f;
		return arr;
	}


}
