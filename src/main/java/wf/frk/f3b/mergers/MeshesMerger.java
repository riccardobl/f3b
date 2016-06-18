package wf.frk.f3b.mergers;

import org.slf4j.Logger;

import com.jme3.scene.Node;

import lombok.RequiredArgsConstructor;
import wf.frk.f3b.Merger;
import wf.frk.f3b.F3bContext;
import wf.frk.f3b.scene.XbufMesh;
import f3b.Datas.Data;

//@ExtensionMethod({jme3_ext_f3b.ext.XbufMeshExt.class})
@RequiredArgsConstructor
public class MeshesMerger implements Merger{
	final MaterialsMerger loader4Materials;
	
	@Override
	public void apply(Data src, Node root, F3bContext context, Logger log) {
		for(f3b.Meshes.Mesh g:src.getMeshesList())
			context.put(g.getId(),new XbufMesh(g, loader4Materials.newDefaultMaterial()));//g.toJME(context,log));
	}

}
