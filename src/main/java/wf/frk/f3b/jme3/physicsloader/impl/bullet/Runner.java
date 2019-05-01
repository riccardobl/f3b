package wf.frk.f3b.jme3.physicsloader.impl.bullet;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * Runner
 */
public interface Runner {

    public <V>  V run(Callable<V> task,boolean wait);

    
}