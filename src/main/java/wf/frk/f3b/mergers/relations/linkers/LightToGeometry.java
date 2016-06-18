package wf.frk.f3b.mergers.relations.linkers;

import static wf.frk.f3b.mergers.relations.LinkerHelpers.getRef1;
import static wf.frk.f3b.mergers.relations.LinkerHelpers.getRef2;

import org.slf4j.Logger;

import com.jme3.light.Light;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;

import wf.frk.f3b.mergers.RelationsMerger;
import wf.frk.f3b.mergers.relations.Linker;
import wf.frk.f3b.mergers.relations.RefData;
import wf.frk.f3b.scene.XbufLightControl;

public class LightToGeometry implements Linker{

	@Override
	public boolean doLink(RelationsMerger loader, RefData data, Logger log) {
		Light op1=getRef1(data,Light.class,log);
		Spatial op2=getRef2(data,Spatial.class,log);
		if(op1==null||op2==null) return false;
		if(op2 instanceof Geometry)	log.warn("Do you really want to add this light to a Geometry? [{}]",data.ref1);

		if(data.context.getSettings().useLightControls()){
			XbufLightControl f3blc=new XbufLightControl();
			f3blc.setLight(op1);
			op2.addControl(f3blc);
			data.root.addLight(op1);
		}else{
			op2.addLight(op1);
		}

		return true;
	}

}
