package wf.frk.f3b.jme3.physicsloader.impl.bullet;

import java.util.concurrent.Callable;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import wf.frk.f3b.jme3.physicsloader.EnhancedGhostControl;
import wf.frk.f3b.jme3.physicsloader.PhysicsLoader;
import wf.frk.f3b.jme3.physicsloader.impl.bullet.BulletPhysicsLoader;

/**
 * BulletEnhancedGhostControl
 */
public class BulletEnhancedGhostControl  extends GhostControl  implements EnhancedGhostControl{
    protected PhysicsLoader loader;
    public BulletEnhancedGhostControl() {
    }

    public BulletEnhancedGhostControl(PhysicsLoader loader){
        this.loader=loader;
    }
    public BulletEnhancedGhostControl(PhysicsLoader loader,CollisionShape shape){
        super(shape);
        this.loader=loader;
    }
    
    protected Vector3f phy_splocation;
    protected Quaternion phy_sprotation;
    
    protected Vector3f getSpatialTranslation() {
        if (applyLocal) {
            return spatial.getLocalTranslation();
        }
        return spatial.getWorldTranslation();
    }

    protected Quaternion getSpatialRotation() {
        if (applyLocal) {
            return spatial.getLocalRotation();
        }
        return spatial.getWorldRotation();
    }


    public void update(float tpf) {
        if(!enabled){ return; }
        try{
            phy_splocation=getSpatialTranslation();
             phy_sprotation=getSpatialRotation();
            
			        setPhysicsLocation(phy_splocation);
			        setPhysicsRotation(phy_sprotation);
				}catch(Exception e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
}