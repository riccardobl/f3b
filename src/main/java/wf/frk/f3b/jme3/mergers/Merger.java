package wf.frk.f3b.jme3.mergers;


import com.jme3.scene.Node;

import f3b.Datas.Data;
import wf.frk.f3b.jme3.F3bContext;

public interface Merger{
	public void apply(Data src, Node root, F3bContext context);
}
