// Generated by delombok at Sat Jul 28 16:45:23 CEST 2018
package wf.frk.f3b.jme3.mergers.relations;

import java.util.logging.Level;

import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;

import wf.frk.f3b.jme3.Const;
import wf.frk.f3b.jme3.scene.F3bMesh;

public class LinkerHelpers {
	@java.lang.SuppressWarnings("all")
	private static final java.util.logging.Logger log =java.util.logging.Logger.getLogger(LinkerHelpers.class.getName());

	public static <T> T getRef1(RefData data, Class<T> as) {
		return getRef(false, data, as);
	}

	public static <T> T getRef2(RefData data, Class<T> as) {
		return getRef(true, data, as);
	}

	@SuppressWarnings("unchecked")
	private static <T> T getRef(boolean id, RefData data, Class<T> as) {
		Object op1_o=data.key.getContext().get(!id?data.ref1:data.ref2);
		if(op1_o==null||!(as.isInstance(op1_o))){
			// If we are picking a mesh as if it were a geometry, return a geometry automagically.
			if(op1_o instanceof F3bMesh&&(as.isAssignableFrom(Spatial.class)||as.isAssignableFrom(Geometry.class))){
				op1_o=getGeometry(data,(F3bMesh)op1_o);
			}else op1_o=null;
		}

		return (T) op1_o;
	}

	public static Geometry getGeometry( RefData data,F3bMesh f3bm){
		Geometry geo = (Geometry) data.key.getContext().get("G~" + f3bm.getId());

		if (geo == null) {		
			log.log(Level.FINE,"Geometry {0} is not cached. Generate...", f3bm.getName());
			geo = new Geometry(f3bm.getName());
			geo.setMaterial(f3bm.material);
			geo.setMesh(f3bm.toJME());
			geo.setUserData(Const.f3b_id,f3bm.getId());
			data.key.getContext().put("G~" + f3bm.getId(), geo);
		} 
		return geo;
	}


	// public static Geometry getGeometry1(RefData data) {
	// 	return getGeometry(false, data);
	// }

	// public static Geometry getGeometry2(RefData data) {
	// 	return getGeometry(true, data);
	// }

	// private static Geometry getGeometry(boolean id, RefData data) {
	// 	String ref = !id ? data.ref1 : data.ref2;
	// 	// Generate geometry form mesh and keep it cached in the context

	// 	Object m = data.context.get(ref);
	// 	if (m == null || !(m instanceof F3bMesh)) return null;

	// 	Geometry geo = (Geometry) data.context.get("G~" + ref);
	// 	F3bMesh f3bm = (F3bMesh) m;

	// 	if (geo == null) {		
	// 		log.info("Geometry {} is not cached. Generate...", f3bm.getName());
	// 		geo = new Geometry(f3bm.getName());
	// 		geo.setMaterial(f3bm.material);
	// 		data.context.put("G~" + ref, geo);
	// 	} else {
	// 		log.debug("Geometry {} is cached. ", geo.getName());
	// 	}

	// 	if(!geo.hasLodLevel(f3bm.getLodLevel())){
	// 		log.debug("Mesh  {} is not cached. Generate..", geo.getName());
	// 		geo.setLodMesh(f3bm.getLodLevel(), f3bm.toJME());
	// 	}else{
	// 		log.debug("Mesh {} is cached. ", geo.getName());
	// 	}

	// 	for(int i=1;i<4;i++){
	// 		Object lodm=data.context.get(ref+"_lod"+i);
	// 		if(lodm !=null&& lodm instanceof F3bMesh){
	// 			f3bm = (F3bMesh) lodm;
	// 			log.info("Lod lod {}...", i);
	// 			geo.setLodMesh(i, f3bm.toJME());
	// 		}else{ 
	// 			break;
	// 		}
	// 	}

	// 	return geo;
	// }
}
