package wf.frk.f3b.mergers.relations;

import org.slf4j.Logger;

import wf.frk.f3b.mergers.RelationsMerger;

public interface Linker{
	public boolean doLink(RelationsMerger loader,RefData data, Logger log);


}
