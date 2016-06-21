package wf.frk.f3b.jme3.mergers;

import java.util.HashMap;

import com.jme3.animation.Bone;
import com.jme3.animation.Skeleton;
import com.jme3.scene.Node;

import f3b.Datas.Data;
import f3b.Relations.Relation;
import f3b.Skeletons;
import lombok.experimental.ExtensionMethod;
import wf.frk.f3b.jme3.core.F3bContext;

@ExtensionMethod({wf.frk.f3b.jme3.ext.f3b.TypesExt.class})
public class SkeletonsMerger implements Merger{

	@Override
	public void apply(Data src, Node root, F3bContext context) {
		for(f3b.Skeletons.Skeleton e:src.getSkeletonsList()){
			// TODO manage parent hierarchy
			String id=e.getId();
			// TODO: merge with existing
			Skeleton child=makeSkeleton(e);
			context.put(id,child);
			// Skeleton child = (Skeleton)components.get(id);
		}
	}

	private Skeleton makeSkeleton(Skeletons.Skeleton e) {
		Bone[] bones=new Bone[e.getBonesCount()];
		HashMap<String,Bone> db=new HashMap<String,Bone>();
		for(int i=0;i<bones.length;i++){
			f3b.Skeletons.Bone src=e.getBones(i);
			Bone b=new Bone(src.getName());
			b.setBindTransforms(src.getTranslation().toJME(),src.getRotation().toJME(),src.getScale().toJME());
			db.put(src.getId(),b);
			bones[i]=b;
		}
		for(Relation r:e.getBonesGraphList()){
			Bone parent=db.get(r.getRef1());
			Bone child=db.get(r.getRef2());
			parent.addChild(child);
		}
		Skeleton sk=new Skeleton(bones);
		sk.setBindingPose();
		return sk;
	}
}
