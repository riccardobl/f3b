package wf.frk.f3b.mergers;

import java.util.Collection;
import java.util.LinkedList;

import org.slf4j.Logger;

import com.jme3.scene.Node;

import lombok.Getter;
import wf.frk.f3b.Merger;
import wf.frk.f3b.F3bContext;
import wf.frk.f3b.mergers.relations.Linker;
import wf.frk.f3b.mergers.relations.RefData;
import wf.frk.f3b.mergers.relations.linkers.AnimationToSpatial;
import wf.frk.f3b.mergers.relations.linkers.CustomParamToSpatial;
import wf.frk.f3b.mergers.relations.linkers.GeometryToNode;
import wf.frk.f3b.mergers.relations.linkers.LightToGeometry;
import wf.frk.f3b.mergers.relations.linkers.MaterialToGeometry;
import wf.frk.f3b.mergers.relations.linkers.NodeToNode;
import wf.frk.f3b.mergers.relations.linkers.PhysicsToSpatial;
import wf.frk.f3b.mergers.relations.linkers.SkeletonToSpatial;
import f3b.Datas.Data;
import f3b.Relations.Relation;

public class RelationsMerger implements Merger{
	protected @Getter final MaterialsMerger matMerger;
	protected @Getter final Collection<Linker> linkers;

	public RelationsMerger(MaterialsMerger mm){
		matMerger=mm;
		linkers=new LinkedList<Linker>();
		linkers.add(new AnimationToSpatial());
		linkers.add(new CustomParamToSpatial());
		linkers.add(new LightToGeometry());
		linkers.add(new MaterialToGeometry());
		linkers.add(new GeometryToNode());
		linkers.add(new SkeletonToSpatial());
		linkers.add(new NodeToNode());
		linkers.add(new PhysicsToSpatial());
	}

	public void apply(Data src, Node root, F3bContext components, Logger log) {
		for(Relation r:src.getRelationsList()){
			merge(new RefData(r.getRef1(),r.getRef2(),src,root,components),log);
		}
	}

	protected void merge(RefData data, Logger log) {
		if(data.ref1.equals(data.ref2)){
			log.warn("can't link {} to itself",data.ref1);
			return;
		}
		boolean linked=false;
		String r1=data.ref1;
		String r2=data.ref2;
		// Linkers work with one relation per time, we want to process also linked generated relations, so we will do this:
		LinkedList<String> refs1=new LinkedList<String>();
		refs1.add(r1);
		refs1.addAll(data.context.linkedRefs(r1));

		LinkedList<String> refs2=new LinkedList<String>();
		refs2.add(r2);
		refs2.addAll(data.context.linkedRefs(r2));


		// Every possible combination
		for(String ref1:refs1){
			for(String ref2:refs2){
				data.ref1=ref1;
				data.ref2=ref2;
				for(Linker linker:linkers){
					if(linker.doLink(this,data,log)){
						linked=true;
						log.info("{} linked to {} with {}",data.ref1,data.ref2,linker.getClass());
						break;
					}
				}
			}
		}
		if(!linked) log.warn("can't link:   {} -- {}\n",data.ref1,data.ref2);
	}
}
