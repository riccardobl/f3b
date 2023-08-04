package wf.frk.f3b.jme3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;

import f3b.Datas;
import f3b.Datas.Data;
import f3b.HeaderOuterClass.Header;
import wf.frk.f3b.jme3.mergers.AnimationsMerger;
import wf.frk.f3b.jme3.mergers.AudioMerger;
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


	public final ExtensionRegistryLite extensions;
	public final List<Merger> mergers;
	public final RelationsMerger relations;


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
		mergers.add(new AudioMerger(assetManager));

		// relations should be the last because it reuse data provide by other (put in components)
		relations= new RelationsMerger(mat_merger);
		// mergers.add(relations);//

		extensions=ExtensionRegistry.newInstance();
	}


	public F3bHeaders.F3bHeader  merge(Header src,String headerPath, final F3bKey key){
		F3bHeaders.F3bHeader header=new F3bHeaders.F3bHeader ();
		header.path=headerPath;
		System.out.println(header.path);
		header.scene=header.path;

		int sx=header.path.lastIndexOf("/");
		if(sx!=-1){
			header.scene=header.scene.substring(0,sx)+"/";
			
		}
		else header.scene="";

		header.scene+=src.getSceneRelPath();

		for(int i=0;i<src.getCollectionsCount();i++){
			f3b.HeaderOuterClass.Collection  c=src.getCollections(i);
			String id=c.getId();
			Collection<String> objs=header.collections.get(id);
			if(objs==null)header.collections.put(id,objs=new ArrayList<String>());
			for(int j=0;j<c.getObjectsCount();j++){
				String obj=c.getObjects(j);
				objs.add(obj);
			}
		}

		key.getHeaders().headers.put(headerPath,header);
		return header;
	}

	public void merge(final Data src, final Node root, final F3bKey key) {
		// Collection<Future> wait_for=new ArrayList<Future>();
		for(int i=0;i<mergers.size()-1;i++){
			final Merger merger=mergers.get(i);
			// Runnable r=new Runnable(){
			// 	@Override
			// 	public void run() {
			merger.apply(src,root,key);
				// }
			// };
			// if(executor!=null){
			// 	Future f=executor.submit(r);
			// 	wait_for.add(f);
			// }else{
			// 	r.run();
			// }
		}
		

		relations.apply(src,root,key);
		

		// if(executor!=null){
		// 	while(true){
		// 		try{
		// 			for(Future f:wait_for){
		// 				f.get();
		// 			}
		// 		}catch(Exception e){
		// 			e.printStackTrace();
		// 		}
		// 		break;
		// 	}
		// }
		mergers.get(mergers.size()-1).apply(src,root,key);
	}


	
}
