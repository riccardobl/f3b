package wf.frk.f3b.mergers.relations.linkers;
import static wf.frk.f3b.mergers.relations.LinkerHelpers.getRef1;
import static wf.frk.f3b.mergers.relations.LinkerHelpers.getRef2;

import org.slf4j.Logger;

import com.jme3.scene.Node;

import wf.frk.f3b.mergers.RelationsMerger;
import wf.frk.f3b.mergers.relations.Linker;
import wf.frk.f3b.mergers.relations.RefData;

public class NodeToNode implements Linker{

	@Override
	public boolean doLink(RelationsMerger loader, RefData data, Logger log) {
		Node op1=getRef1(data,Node.class,log);
		Node op2=getRef2(data,Node.class,log);
		if(op1==null||op2==null) return false;
		op1.attachChild(op2);
		return true;
	}
}
