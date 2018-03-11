package wf.frk.f3b.jme3.mergers;

import java.util.Collection;
import java.util.LinkedList;

import com.jme3.scene.Node;

import f3b.Datas.Data;
import f3b.Relations.Relation;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import wf.frk.f3b.jme3.F3bContext;
import wf.frk.f3b.jme3.mergers.relations.Linker;
import wf.frk.f3b.jme3.mergers.relations.RefData;
import wf.frk.f3b.jme3.mergers.relations.linkers.AnimationToSpatial;
import wf.frk.f3b.jme3.mergers.relations.linkers.CustomParamToSpatial;
import wf.frk.f3b.jme3.mergers.relations.linkers.GeometryToNode;
import wf.frk.f3b.jme3.mergers.relations.linkers.LightToNode;
import wf.frk.f3b.jme3.mergers.relations.linkers.MaterialToGeometry;
import wf.frk.f3b.jme3.mergers.relations.linkers.NodeToNode;
import wf.frk.f3b.jme3.mergers.relations.linkers.PhysicsToSpatial;
import wf.frk.f3b.jme3.mergers.relations.linkers.SkeletonToSpatial;
@Log4j2
public class RelationsMerger implements Merger{
	protected @Getter final MaterialsMerger matMerger;
	protected @Getter final Collection<Linker> linkers;

	public RelationsMerger(MaterialsMerger mm){
		matMerger=mm;
		linkers=new LinkedList<Linker>();
		linkers.add(new AnimationToSpatial());
		linkers.add(new CustomParamToSpatial());
		linkers.add(new LightToNode());
		linkers.add(new MaterialToGeometry());
		linkers.add(new GeometryToNode());
		linkers.add(new SkeletonToSpatial());
		linkers.add(new NodeToNode());
		linkers.add(new PhysicsToSpatial());
	}

	public void apply(Data src, Node root, F3bContext components) {
		for(Relation r:src.getRelationsList()){
			merge(new RefData(r.getRef1(),r.getRef2(),src,root,components));
		}
	}

	protected void merge(RefData data) {
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

		LinkedList<String> refs2=new LinkedList<String>();
		refs2.add(r2);
		refs2.addAll(data.context.linkedRefs(r2));


		// Every possible combination
		for(String ref1:refs1){
			for(String ref2:refs2){
				data.ref1=ref1;
				data.ref2=ref2;
				for(Linker linker:linkers){
					if(linker.doLink(this,data)){
						linked=true;
						log.info("{} linked to {} with {} [original ref  {} -> {}]",data.ref1,data.ref2,linker.getClass(),r1,r2);
						break;
					}
				}
			}
		}
		if(!linked) log.warn("can't link:   {} -- {}\n",data.ref1,data.ref2);
	}
}
