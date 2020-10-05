package wf.frk.f3banimation;

import com.jme3.math.Matrix4f;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 * BoneAttachmentControl
 */
public class BoneAttachmentControl extends AbstractControl{

    public BoneAttachmentControl(){

    }

    private SkeletonControl skeleton;
    private Bone bone;

    public BoneAttachmentControl(Spatial target,String bone){
        findBone(target,bone);
    }

    public boolean findBone(Spatial sp,String bone){
        SkeletonControl sk=sp.getControl(SkeletonControl.class);
        if(sk!=null){
            Bone bn=sk.getSkeleton().getBone(bone);
            if(bn!=null){
                this.bone=bn;
                this.skeleton=sk;
                return true;
            }
        }
        if(sp instanceof Node){
            Node nn=(Node)sp;
            for(Spatial c:nn.getChildren()){
                if(findBone(c,bone)){
                    return true;
                }
            }
        }
        return false;
    }

    final static Transform tmp_Tr=new Transform();
    final static Matrix4f tmp_M4=new Matrix4f();

    @Override
    public void controlUpdate(float tpf) {
      
        // this.spatial.getWorldTranslation():
        // System.out.println(this.spatial);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        if(bone == null || skeleton == null) return;

        this.spatial.getParent().getWorldTransform().toTransformMatrix(tmp_M4);
        tmp_M4.invertLocal();
        tmp_Tr.fromTransformMatrix(tmp_M4);

        Transform tr=this.spatial.getLocalTransform();
        tr.set(bone.getModelSpaceTransform());
        tr.combineWithParent(skeleton.getSpatial().getWorldTransform());
        tr.combineWithParent(tmp_Tr);
        this.spatial.setLocalTransform(tr);
        // Vector3f pos=skeleton.getSpatial().localToWorld(bone.getModelSpaceTransform().getTranslation(),null);
        

        // pos= this.spatial.worldToLocal(pos,null);
        // this.spatial.setLocalTranslation(bone.getModelSpaceTransform().getTranslation());
    }


    
}