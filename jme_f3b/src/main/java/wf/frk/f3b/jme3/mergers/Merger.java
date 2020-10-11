package wf.frk.f3b.jme3.mergers;

import com.jme3.scene.Node;

import f3b.Datas.Data;
import wf.frk.f3b.jme3.F3bContext;
import wf.frk.f3b.jme3.F3bKey;

public interface Merger{
	public void apply(Data src, Node root, F3bKey key);
}
