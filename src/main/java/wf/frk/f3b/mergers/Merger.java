package wf.frk.f3b.mergers;


import com.jme3.scene.Node;

import f3b.Datas.Data;
import wf.frk.f3b.core.F3bContext;

public interface Merger{
	public void apply(Data src, Node root, F3bContext context);
}
