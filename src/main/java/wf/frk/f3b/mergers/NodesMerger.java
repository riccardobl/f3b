package wf.frk.f3b.mergers;


import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import f3b.Datas.Data;
import f3b.Tobjects.TObject;
import lombok.experimental.ExtensionMethod;
import wf.frk.f3b.F3bContext;
import wf.frk.f3b.Merger;

@ExtensionMethod({wf.frk.f3b.ext.PrimitiveExt.class})
public class NodesMerger implements Merger{

	@Override
	public void apply(Data src, Node root, F3bContext context) {
			for(TObject n:src.getTobjectsList()){
				String id=n.getId();
				Spatial child=(Spatial)context.get(id);
				if(child==null){
					child=new Node("");
					root.attachChild(child);
					context.put(id,child);
				}
				child.setName(n.hasName()?n.getName():n.getId());
				child.setLocalRotation(n.getRotation().toJME());
				child.setLocalTranslation(n.getTranslation().toJME());
				child.setLocalScale(n.getScale().toJME());
			}
	}
}
