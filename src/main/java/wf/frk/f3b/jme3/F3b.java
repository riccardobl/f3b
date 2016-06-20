package wf.frk.f3b.jme3;

import java.util.LinkedList;
import java.util.List;

import com.google.protobuf.ExtensionRegistry;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;

import f3b.Datas.Data;
import wf.frk.f3b.jme3.core.F3bContext;
import wf.frk.f3b.jme3.mergers.AnimationsMerger;
import wf.frk.f3b.jme3.mergers.CustomParamsMerger;
import wf.frk.f3b.jme3.mergers.LightsMerger;
import wf.frk.f3b.jme3.mergers.MaterialsMerger;
import wf.frk.f3b.jme3.mergers.Merger;
import wf.frk.f3b.jme3.mergers.MeshesMerger;
import wf.frk.f3b.jme3.mergers.NodesMerger;
import wf.frk.f3b.jme3.mergers.PhysicsMerger;
import wf.frk.f3b.jme3.mergers.RelationsMerger;
import wf.frk.f3b.jme3.mergers.SkeletonsMerger;

public class F3b{
	public final ExtensionRegistry extensions;
	public final List<Merger> mergers;

	
	public F3b(AssetManager assetManager){
		MaterialsMerger mat_merger=new MaterialsMerger(assetManager);
		mergers=new LinkedList<Merger>();
		mergers.add(new NodesMerger());
		mergers.add(new MeshesMerger(mat_merger));
		mergers.add(mat_merger);
		mergers.add(new LightsMerger());
		mergers.add(new SkeletonsMerger());
		mergers.add(new AnimationsMerger());
		mergers.add(new CustomParamsMerger());
		mergers.add(new PhysicsMerger());

		// relations should be the last because it reuse data provide by other (put in components)
		mergers.add( new RelationsMerger(mat_merger));

		extensions=ExtensionRegistry.newInstance();
	}

	public void merge(Data src, Node root, F3bContext context) {
		for(Merger m:mergers){
			m.apply(src,root,context);
		}
	}
}
