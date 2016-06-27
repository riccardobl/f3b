package wf.frk.f3b.jme3.mergers;


import com.jme3.scene.Node;

import f3b.Datas.Data;
import lombok.RequiredArgsConstructor;
import wf.frk.f3b.jme3.F3bContext;
import wf.frk.f3b.jme3.scene.F3bMesh;

@RequiredArgsConstructor
public class MeshesMerger implements Merger{
	final MaterialsMerger loader4Materials;
	
	@Override
	public void apply(Data src, Node root, F3bContext context) {
		for(f3b.Meshes.Mesh g:src.getMeshesList())
			context.put(g.getId(),new F3bMesh(g, loader4Materials.getDefaultMaterial()));
	}

}