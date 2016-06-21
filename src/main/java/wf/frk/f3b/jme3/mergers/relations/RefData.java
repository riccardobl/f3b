package wf.frk.f3b.jme3.mergers.relations;

import com.jme3.scene.Node;

import f3b.Datas.Data;
import lombok.AllArgsConstructor;
import wf.frk.f3b.jme3.core.F3bContext;

@AllArgsConstructor
public class RefData {
	public String ref1,ref2;
	public Data src;
	public Node root;
	public F3bContext context;
}