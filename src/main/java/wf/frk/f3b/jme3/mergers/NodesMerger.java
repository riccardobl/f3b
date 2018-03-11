package wf.frk.f3b.jme3.mergers;


import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import f3b.Datas.Data;
import f3b.Tobjects.TObject;
import lombok.experimental.ExtensionMethod;
import wf.frk.f3b.jme3.F3bContext;

@ExtensionMethod({wf.frk.f3b.jme3.ext.f3b.TypesExt.class})
public class NodesMerger implements Merger{

	@Override
	public void apply(Data src, Node root, F3bContext context) {
			for(TObject n:src.getTobjectsList()){
				String id=n.getId();
				Spatial child=(Spatial)context.get(id);
				if(child==null){
					child=context.getSettings().getNodeBuilder().build(n.hasName()?n.getName():n.getId());
					root.attachChild(child);
					context.put(id,child);
				}
				child.setLocalRotation(n.getRotation().toJME());
				child.setLocalTranslation(n.getTranslation().toJME());
				child.setLocalScale(n.getScale().toJME());
			}
	}
}
