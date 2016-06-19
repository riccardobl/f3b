package wf.frk.f3b.mergers;


import com.jme3.scene.Node;

import f3b.CustomParams;
import f3b.Datas.Data;
import wf.frk.f3b.F3bContext;
import wf.frk.f3b.Merger;

public class CustomParamsMerger implements Merger{

	@Override
	public void apply(Data src, Node root, F3bContext context) {
		for(CustomParams.CustomParamList srccp:src.getCustomParamsList()){
			// TODO merge with existing
			context.put(srccp.getId(),srccp);
		}
	}

}
