package wf.frk.f3b.jme3.mergers.relations.linkers;
import static wf.frk.f3b.jme3.mergers.relations.LinkerHelpers.getRef1;
import static wf.frk.f3b.jme3.mergers.relations.LinkerHelpers.getRef2;

import java.util.Optional;

import com.jme3.animation.SkeletonControl;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;

import wf.frk.f3b.jme3.mergers.RelationsMerger;
import wf.frk.f3b.jme3.mergers.relations.Linker;
import wf.frk.f3b.jme3.mergers.relations.RefData;


public class MaterialToGeometry  implements Linker{
	protected long LAST_CLONE_ID=0;

	@Override
	public boolean doLink(RelationsMerger loader, RefData data) {
		Material op1=getRef1(data,Material.class);
		Geometry op2=getRef2(data,Geometry.class);
		if(op1==null||op2==null) return false;
		if(op2.getControl(SkeletonControl.class)!=null){
			op1=op1.clone();
			data.context.put("G~"+data.ref1+"~cloned~"+(LAST_CLONE_ID++),op1,data.ref1);
		}else{
			String refusage="G~usage~"+data.ref1;
			int n=(int)Optional.ofNullable(data.context.get(refusage)).orElse(0);
			data.context.put(refusage,n++);
		}

		op2.setMaterial(op1);
		Number bucket=((Number)data.context.get("G~"+data.ref1+"~RenderBucket"));
		if(bucket!=null){
			int b=bucket.intValue();
			switch(b){
				case 0: op2.setQueueBucket(Bucket.Opaque); break;
				case 1: op2.setQueueBucket(Bucket.Translucent); break;
				case 2: {					
					op2.setQueueBucket(Bucket.Transparent); 
					op1.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
					break;
				}
				case 3: op2.setQueueBucket(Bucket.Sky); break;
			}
		}
		
		return true;
	}
}
