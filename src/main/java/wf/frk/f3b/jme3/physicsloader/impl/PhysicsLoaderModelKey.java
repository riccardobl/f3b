package wf.frk.f3b.jme3.physicsloader.impl;

import com.jme3.asset.ModelKey;
import com.jme3.export.Savable;
import wf.frk.f3b.jme3.physicsloader.PhysicsCacher;
import wf.frk.f3b.jme3.physicsloader.PhysicsLoader;
import wf.frk.f3b.jme3.physicsloader.PhysicsLoaderSettings;
import wf.frk.f3b.jme3.physicsloader.impl.bullet.BulletPhysicsLoader;

@SuppressWarnings("unchecked")
public class PhysicsLoaderModelKey<RETURN_TYPE extends PhysicsLoaderSettings> extends ModelKey implements PhysicsLoaderSettings {
	protected  PhysicsLoader<? ,?> phyLoader;
	protected Object vhacdFactory;
	protected boolean	enhancedrbs=false,enhancedrgh=false;
	protected PhysicsCacher cacher;
	protected boolean static_vhacd=false;

	public PhysicsLoaderModelKey(){
		super();
	}
	
	public PhysicsLoaderModelKey(String path){
		super(path);
	}
	
	@Override
	public RETURN_TYPE usePhysics(boolean v){
		try{
			if(v)return usePhysics(new BulletPhysicsLoader());
			else return usePhysics(null);
		}catch(Throwable e){}
		return (RETURN_TYPE)this;
	}

	@Override
	public Object getVHACDFactory(){
		return vhacdFactory;
	}
	
	@Override
	public RETURN_TYPE useVHACD(Object factory){
		if(factory ==null){
			vhacdFactory=null;
			return (RETURN_TYPE)this;
		}
		try{
			Class<?> vhacd_class=Class.forName("com.jme3.bullet.vhacd.VHACDCollisionShapeFactory");
			if(factory.getClass().isAssignableFrom(vhacd_class)||factory instanceof Boolean){
				vhacdFactory=factory;
			}
		}catch(Throwable e){}
		return (RETURN_TYPE)this;
	}
	
	@Override
	public RETURN_TYPE usePhysics(PhysicsLoader<?,?> phyLoader){
		this.phyLoader= phyLoader;
		return (RETURN_TYPE)this;
	}
	
	@Override
	public PhysicsLoader<?,?>  getPhysicsLoader(){
		return phyLoader;
	}

	@Override
	public RETURN_TYPE useEnhancedRigidbodies(boolean v) {
		enhancedrbs=v;
		return (RETURN_TYPE)this;
	}

	@Override
	public RETURN_TYPE useEnhancedGhostbodies(boolean v) {
		enhancedrgh=v;
		return (RETURN_TYPE)this;
	}

	

	@Override
	public boolean useEnhancedRigidbodies() {
		return enhancedrbs;
	}

	@Override
	public boolean useEnhancedGhostbodies() {
		return enhancedrgh;
	}

	@Override
	public PhysicsLoaderSettings useCacher(PhysicsCacher cacher) {
		this.cacher=cacher;
		return this;
	}

	@Override
	public PhysicsCacher getCacher() {
		return this.cacher;
	}

	@Override
	public PhysicsLoaderSettings useVHACDForStaticCollisions(boolean v) {
		static_vhacd=v;
		return this;
	}

	@Override
	public boolean useVHACDForStaticCollisions() {
		return static_vhacd;
	}



}
