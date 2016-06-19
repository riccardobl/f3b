package wf.frk.f3b;

import com.jme3.physicsloader.PhysicsLoader;
import com.jme3.physicsloader.constraint.GenericConstraint;
import com.jme3.physicsloader.impl.PhysicsLoaderModelKey;
import com.jme3.physicsloader.impl.bullet.BulletPhysicsLoader;

import lombok.extern.log4j.Log4j2;


@Log4j2
public class F3bKey extends PhysicsLoaderModelKey<F3bKey> implements F3bPhysicsLoaderSettings{

	public F3bKey(){}

	public F3bKey(String s){
		super(s);
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

	private final static Class<?>[] supportedConstraints={
			GenericConstraint.class
	};

	@Override
	public Class<?>[] getSupportedConstraints() {
		return supportedConstraints;
	}

}
