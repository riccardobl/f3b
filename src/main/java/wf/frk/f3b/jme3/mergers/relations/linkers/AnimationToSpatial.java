package wf.frk.f3b.jme3.mergers.relations.linkers;

import static wf.frk.f3b.jme3.mergers.relations.LinkerHelpers.getRef1;
import static wf.frk.f3b.jme3.mergers.relations.LinkerHelpers.getRef2;

import wf.frk.f3banimation.AnimControl;
import wf.frk.f3banimation.SkeletonControl;
import com.jme3.scene.Spatial;

import wf.frk.f3b.jme3.animations.F3bAnimation;
import wf.frk.f3b.jme3.mergers.RelationsMerger;
import wf.frk.f3b.jme3.mergers.relations.Linker;
import wf.frk.f3b.jme3.mergers.relations.RefData;

public class AnimationToSpatial implements Linker{
	@Override
	public boolean doLink(RelationsMerger loader,RefData data) {
		F3bAnimation op1=getRef1(data,F3bAnimation.class);
		Spatial op2=getRef2(data,Spatial.class);
		if(op1==null||op2==null)return false;
		AnimControl c=op2.getControl(AnimControl.class);
		if(c==null){
			SkeletonControl sc=op2.getControl(SkeletonControl.class);
			c=sc!=null?new AnimControl(sc.getSkeleton()):new AnimControl();
			op2.addControl(c);
		}
		c.addAnim(op1.toJME(c.getSkeleton()));
		return true;
	}

}
