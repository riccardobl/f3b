package wf.frk.f3b.jme3.mergers.relations.linkers;

import static wf.frk.f3b.jme3.mergers.relations.LinkerHelpers.getRef1;
import static wf.frk.f3b.jme3.mergers.relations.LinkerHelpers.getRef2;
import static wf.frk.f3b.jme3.mergers.relations.LinkerHelpers.getGeometry;

import java.util.concurrent.atomic.AtomicInteger;

import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

import wf.frk.f3b.jme3.mergers.RelationsMerger;
import wf.frk.f3b.jme3.mergers.relations.Linker;
import wf.frk.f3b.jme3.mergers.relations.RefData;
import wf.frk.f3b.jme3.scene.F3bMesh;

public class MeshLinker implements Linker{
	protected long LAST_CLONE_ID=0;
	private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(MeshLinker.class);


	


	private void linkMeshToNode(RelationsMerger loader, RefData data,F3bMesh mesh,Node node) {
		Geometry op1=getGeometry(data,mesh);

		if(op1.getParent()!=null){
			op1=op1.clone(false);
			data.key.getContext().put("G~"+data.ref1+"~cloned~"+(LAST_CLONE_ID++),op1,data.ref1);
		}

		node.attachChild(op1);
	}

	private void linkMeshToMesh(RelationsMerger loader, RefData data,F3bMesh meshA,F3bMesh meshB) {
		// Disabled for now since it needs changes in the core
		// Geometry geoA=getGeometry(data,meshA);
		// Geometry geoB=getGeometry(data,meshB);
		// geoB.setLodMesh(meshA.getLodLevel(),geoA.getMesh());
	}
	
	@Override
	public boolean doLink(RelationsMerger loader, RefData data) {
		F3bMesh op1=getRef1(data,F3bMesh.class);
		Node op2_node=getRef2(data,Node.class);
		F3bMesh op2_mesh=getRef2(data,F3bMesh.class);


		if(op1==null||(op2_node==null&&op2_mesh==null)) return false;

		if(op2_node!=null){
			linkMeshToNode(loader,data,op1,op2_node);
		}else{
			linkMeshToMesh(loader,data,op1,op2_mesh);
		}

		// If already attached, clone geom, and add it to the context. Create also a link from the original one, this is used for other relations. (material for instance)
	

		return true;
	}

}
