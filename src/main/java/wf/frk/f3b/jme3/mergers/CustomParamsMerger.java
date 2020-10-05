package wf.frk.f3b.jme3.mergers;

import com.jme3.scene.Node;

import f3b.CustomParams;
import f3b.Datas.Data;
import wf.frk.f3b.jme3.F3bContext;
import wf.frk.f3b.jme3.F3bKey;

public class CustomParamsMerger implements Merger{

	@Override
	public void apply(Data src, Node root,  F3bKey key) {
		F3bContext context=key.getContext();
		for(CustomParams.CustomParamList srccp:src.getCustomParamsList()){
			// TODO merge with existing
			context.put(srccp.getId(),srccp);
		}
	}

}
