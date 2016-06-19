package wf.frk.f3b.mergers.relations.linkers;

import static wf.frk.f3b.mergers.relations.LinkerHelpers.getRef1;
import static wf.frk.f3b.mergers.relations.LinkerHelpers.getRef2;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.jme3.export.Savable;
import com.jme3.physicsloader.ConstraintData;
import com.jme3.physicsloader.PhysicsLoader;
import com.jme3.physicsloader.constraint.GenericConstraint;
import com.jme3.physicsloader.rigidbody.RigidBody;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;

import f3b.Physics.Constraint;
import f3b.Physics.ConstraintGeneric;
import lombok.experimental.ExtensionMethod;
import lombok.extern.log4j.Log4j2;
import wf.frk.f3b.F3bContext;
import wf.frk.f3b.mergers.RelationsMerger;
import wf.frk.f3b.mergers.relations.Linker;
import wf.frk.f3b.mergers.relations.RefData;
import wf.frk.f3b.physics.F3bPhysicsLoader;


@Log4j2
@ExtensionMethod({wf.frk.f3b.ext.PrimitiveExt.class})
public class PhysicsToSpatial implements Linker{

	@Override
	public boolean doLink(RelationsMerger rloader, RefData data) {
		if(loadRB(rloader,data)) return true;
		return false;
	}

	protected boolean loadRB(RelationsMerger rloader, RefData data) {
		RigidBody op1=getRef1(data,RigidBody.class);
		Spatial op2=getRef2(data,Spatial.class);
		if(op1==null||op2==null) return false;
		PhysicsLoader<?,?> loader=data.context.getSettings().getPhysicsLoader();
		if(loader!=null){
			Savable pc=loader.load(data.context.getSettings(),op2,op1);
			log.debug("Load rigidbody {}",data.ref1);
			if(pc!=null&&pc instanceof Control){
				op2.addControl((Control)pc);
				String linkRef="G~slink4phy~"+System.currentTimeMillis()+"~"+data.ref1;
				data.context.put(linkRef,op2,data.ref1);
				applyCTs(op2,data.ref1,data.context,data.root);
			}
		}
		return true;
	}

	protected void applyCTs(Spatial phy1S, String phy1Ref, F3bContext ctx, Node root) {
		Collection<Constraint> constraints=ctx.get("G~constraints");
		if(constraints!=null){
			for(byte i=0;i<2;i++){
//				for(Constraint c:constraints){
				for(Iterator<Constraint> c_i=constraints.iterator();c_i.hasNext();){
					Constraint c=c_i.next();
					if(!(i==0?c.getARef():c.getBRef()).equals(phy1Ref)) continue;
					String phy2Ref=i==1?c.getARef():c.getBRef();
					if(ctx.get(phy2Ref)==null){
						log.debug("Found constraint element {}, second element is missing... skip...",phy1Ref);
						continue;
					}
					log.debug("Found constraint elements {} - {}",phy1Ref,phy2Ref);

					List<String> linked=ctx.linkedRefs(phy2Ref);
					for(String l:linked){
						if(l.startsWith("G~slink4phy~")){
							Spatial phy2S=ctx.get(l);
							Spatial a, b;
							if(i==1){// Reorder
								a=phy2S;
								b=phy1S;
							}else{
								a=phy1S;
								b=phy2S;
							}
							applyCT(c,a,b,ctx,root);
							// Remove consumed constraint (Good idea??)
							c_i.remove();
							break;
						}
					}
				}
			}
		}else{
			log.debug("Constraints map not found.");
		}
	}

	protected void applyCT(Constraint ct, Spatial a, Spatial b, F3bContext ctx, Node root) {
		// Parse constraint
		ConstraintData ct_data=null;
		if(ct.hasGeneric()){
			ConstraintGeneric f3b_generic_ct=ct.getGeneric();

			GenericConstraint generic_ct=new GenericConstraint();
			generic_ct.pivotA=f3b_generic_ct.getPivotA().toJME();
			generic_ct.pivotB=f3b_generic_ct.getPivotB().toJME();
			generic_ct.pivotA=f3b_generic_ct.getPivotA().toJME();

			generic_ct.upperAngularLimit=f3b_generic_ct.getUpperAngularLimit().toJME();
			generic_ct.lowerAngularLimit=f3b_generic_ct.getLowerAngularLimit().toJME();

			generic_ct.upperLinearLimit=f3b_generic_ct.getUpperLinearLimit().toJME();
			generic_ct.lowerLinearLimit=f3b_generic_ct.getLowerLinearLimit().toJME();
			generic_ct.disableCollisionsBetweenLinkedNodes=f3b_generic_ct.getDisableCollisions();
			ct_data=generic_ct;
		} // else if ... [Only one type.]


		if(ct_data==null){
			log.warn("Constraint {} not supported",ct);
			return;
		}
		try{
			log.debug("Store constraint {} [{}-{}] in scene",ct_data,a,b);
			Spatial constraint_node=F3bPhysicsLoader.storeConstraintInScene(ctx.getSettings(),a,b,ct_data);
			root.attachChild(constraint_node);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
