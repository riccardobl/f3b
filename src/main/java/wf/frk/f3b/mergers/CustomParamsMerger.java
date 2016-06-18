package wf.frk.f3b.mergers;

import org.slf4j.Logger;

import com.jme3.scene.Node;

import wf.frk.f3b.Merger;
import wf.frk.f3b.F3bContext;
import f3b.Datas.Data;
import f3b.CustomParams;

public class CustomParamsMerger implements Merger{

	@Override
	public void apply(Data src, Node root, F3bContext context, Logger log) {
		for(CustomParams.CustomParamList srccp:src.getCustomParamsList()){
			// TODO merge with existing
			context.put(srccp.getId(),srccp);
		}
	}

}
