package wf.frk.f3b.ext.f3b;

import java.util.List;

import f3b.Meshes.UintBuffer;

public class UintBufferExt{
	//TODO use an optim version: including a patch for no autoboxing : https://code.google.com/p/protobuf/issues/detail?id=464
	public static int[] array(UintBuffer src) {
		List<Integer> list=src.getValuesList();
		int arr[]=new int[list.size()];
		int i=0;
		for(Integer f:list)	arr[i++]=(int)f;
		return arr;
	}
}
