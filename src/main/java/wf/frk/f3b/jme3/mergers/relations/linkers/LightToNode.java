package wf.frk.f3b.jme3.mergers.relations.linkers;

import static wf.frk.f3b.jme3.mergers.relations.LinkerHelpers.getRef1;
import static wf.frk.f3b.jme3.mergers.relations.LinkerHelpers.getRef2;

import com.jme3.light.Light;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;

import lombok.experimental.ExtensionMethod;
import lombok.extern.log4j.Log4j2;
import wf.frk.f3b.jme3.mergers.RelationsMerger;
import wf.frk.f3b.jme3.mergers.relations.Linker;
import wf.frk.f3b.jme3.mergers.relations.RefData;
import wf.frk.f3b.jme3.scene.F3bLightControl;

@Log4j2
@ExtensionMethod({wf.frk.f3b.jme3.ext.jme3.LightExt.class})

public class LightToNode implements Linker{

	@Override
	public boolean doLink(RelationsMerger loader, RefData data) {
		Light op1=getRef1(data,Light.class);
		Spatial op2=getRef2(data,Spatial.class);
		if(op1==null||op2==null) return false;
		if(op2 instanceof Geometry) log.warn("Do you really want to add this light to a Geometry? [{}]",data.ref1);
		//		if(data.context.getSettings().useLightControls()){
		////			F3bLightControl lc=new F3bLightControl();
		////			lc.setLight(op1);
		////			op2.addControl(lc);
		//			data.root.addLight(op1);
		//		}else{
		op1.setPosition(op2.getWorldTranslation());
		op1.setDirection(op2.getWorldRotation().mult(Vector3f.UNIT_Z));
		op2.addLight(op1);
		//		}
		return true;
	}

}
