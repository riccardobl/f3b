package wf.frk.f3b;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;

import com.google.protobuf.ExtensionRegistry;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;

import wf.frk.f3b.mergers.AnimationsMerger;
import wf.frk.f3b.mergers.CustomParamsMerger;
import wf.frk.f3b.mergers.LightsMerger;
import wf.frk.f3b.mergers.MaterialsMerger;
import wf.frk.f3b.mergers.MeshesMerger;
import wf.frk.f3b.mergers.NodesMerger;
import wf.frk.f3b.mergers.PhysicsMerger;
import wf.frk.f3b.mergers.RelationsMerger;
import wf.frk.f3b.mergers.SkeletonsMerger;
import f3b.Datas.Data;
import f3b.AnimationsKf;
import f3b.CustomParams;

public class F3b{
	public final ExtensionRegistry registry;
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

		registry=ExtensionRegistry.newInstance();
		setupExtensionRegistry(registry);
	}


	protected ExtensionRegistry setupExtensionRegistry(ExtensionRegistry r) {
		CustomParams.registerAllExtensions(r);
		AnimationsKf.registerAllExtensions(r);
		return r;
	}

	public void merge(Data src, Node root, F3bContext context, Logger log) {
		for(Merger m:mergers){
			m.apply(src,root,context,log);
		}
	}

}
