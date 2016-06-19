package wf.frk.f3b;


import com.jme3.scene.Node;

import f3b.Datas.Data;

public interface Merger{
	public void apply(Data src, Node root, F3bContext context);
}
