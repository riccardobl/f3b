package wf.frk.f3b;

import com.jme3.physicsloader.PhysicsLoader;
import com.jme3.physicsloader.constraint.GenericConstraint;
import com.jme3.physicsloader.impl.PhysicsLoaderModelKey;
import com.jme3.physicsloader.impl.bullet.BulletPhysicsLoader;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class F3bKey extends PhysicsLoaderModelKey<F3bKey> implements F3bPhysicsLoaderSettings{

	public F3bKey(){}

	public F3bKey(String s){
		super(s);
	}

	protected boolean useLightControls = false;
	public F3bKey useLightControls(boolean x){
		useLightControls=x;
		return this;
	}

	@Override
	public F3bKey usePhysics(PhysicsLoader<?,?> l){
		if(l!=null&&!(l instanceof BulletPhysicsLoader)){
			log.warn("Cannot use {}, physicsloader not supported",l.getClass());
			return this;
		}
		super.usePhysics(l);
		return this;
	}


	public boolean useLightControls(){
		return useLightControls;
	}

	private final static Class<?>[] supportedConstraints={
			GenericConstraint.class
	};

	@Override
	public Class<?>[] getSupportedConstraints() {
		return supportedConstraints;
	}

}
