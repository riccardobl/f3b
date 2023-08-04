package wf.frk.f3b.jme3.mergers.relations.linkers;

import static wf.frk.f3b.jme3.mergers.relations.LinkerHelpers.getRef1;
import static wf.frk.f3b.jme3.mergers.relations.LinkerHelpers.getRef2;

import java.util.Collection;
import java.util.logging.Level;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import wf.frk.f3b.jme3.Const;
import wf.frk.f3b.jme3.F3bHeaders;
import wf.frk.f3b.jme3.F3bKey;
import wf.frk.f3b.jme3.F3bHeaders.CollectionData;
import wf.frk.f3b.jme3.mergers.RelationsMerger;
import wf.frk.f3b.jme3.mergers.relations.Linker;
import wf.frk.f3b.jme3.mergers.relations.RefData;

public class NodeToExternalCollection implements Linker{

	private static final java.util.logging.Logger LOGGER=java.util.logging.Logger.getLogger(NodeToExternalCollection.class.getName());
	
	@Override
	public boolean doLink(RelationsMerger loader, RefData data) {
		if(!data.ref2Ext) return false;

		AssetManager am=data.key.getAssetManager();
		F3bHeaders headers=data.key.getHeaders();

		Node op1=getRef1(data,Node.class);
		CollectionData col=headers.getCollectionFromId(data.ref2);
		if(col==null){
			System.out.println("Can't link "+data.ref2);
			LOGGER.log(Level.SEVERE,"Can't link {0}",data.ref2);
			return false;
		}

		F3bKey linkedK=(F3bKey)data.key.clone();
		linkedK.setName(col.scene);
		linkedK.resolveAllHeaders(false);

		LOGGER.log(Level.FINE,"Link {0}",linkedK);
		Spatial link=am.loadModel(col.scene);
		Collection<Spatial> sps=col.filter(link);
		for(Spatial sp:sps){
			op1.attachChild(sp);
			Boolean holdOut=op1.getUserData(Const.f3b_holdOut);
			if(holdOut==null)holdOut=false;
			if(holdOut){
				sp.depthFirstTraversal(sx->{
					if(!(sx instanceof Node)){
						sx.removeFromParent();
					}
				});
			}

		}

		return true;
	}
}
