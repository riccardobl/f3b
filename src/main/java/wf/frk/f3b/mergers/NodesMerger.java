package wf.frk.f3b.mergers;

import org.slf4j.Logger;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import lombok.experimental.ExtensionMethod;
import wf.frk.f3b.Merger;
import wf.frk.f3b.F3bContext;
import f3b.Datas.Data;
import f3b.Primitives.Transform;
import f3b.Tobjects.TObject;

@ExtensionMethod({wf.frk.f3b.ext.PrimitiveExt.class})
public class NodesMerger implements Merger{

	@Override
	public void apply(Data src, Node root, F3bContext context, Logger log) {
			for(TObject n:src.getTobjectsList()){
				String id=n.getId();
				Spatial child=(Spatial)context.get(id);
				if(child==null){
					child=new Node("");
					root.attachChild(child);
					context.put(id,child);
				}
				child.setName(n.hasName()?n.getName():n.getId());
				Transform transform=n.getTransform();
				child.setLocalRotation(transform.getRotation().toJME());
				child.setLocalTranslation(transform.getTranslation().toJME());
				child.setLocalScale(transform.getScale().toJME());
			}
	}
}
