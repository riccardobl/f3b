package wf.frk.f3b.jme3.mergers.relations.linkers;
import static wf.frk.f3b.jme3.mergers.relations.LinkerHelpers.getRef1;
import static wf.frk.f3b.jme3.mergers.relations.LinkerHelpers.getRef2;

import com.jme3.scene.Node;

import wf.frk.f3b.jme3.mergers.RelationsMerger;
import wf.frk.f3b.jme3.mergers.relations.Linker;
import wf.frk.f3b.jme3.mergers.relations.RefData;

public class NodeToNode implements Linker{

	@Override
	public boolean doLink(RelationsMerger loader, RefData data) {
		if(data.ref2Ext)return false;
		Node op1=getRef1(data,Node.class);
		Node op2=getRef2(data,Node.class);
		if(op1==null||op2==null) return false;
		op1.attachChild(op2);
		return true;
	}
}
