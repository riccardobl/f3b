package wf.frk.f3b.mergers.relations;

import com.jme3.scene.Node;

import lombok.AllArgsConstructor;
import wf.frk.f3b.F3bContext;
import f3b.Datas.Data;

@AllArgsConstructor
public class RefData {
	public String ref1,ref2;
	public Data src;
	public Node root;
	public F3bContext context;
}