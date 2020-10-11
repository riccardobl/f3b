package wf.frk.f3banimation;
/**
 * Copyright (c) 2020, Riccardo Balbo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
import com.jme3.math.Matrix4f;
import com.jme3.math.Transform;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
/**
 * A control to attach a spatial to a bone
 * 
 * @author Riccardo Balbo
 */
public class AttachedToBoneControl extends AbstractControl{
	protected String boneName;
    protected final Transform tmpTransform=new Transform();
    protected final Transform tmpTransform2=new Transform();
    protected final Matrix4f tmpMatrix=new Matrix4f();
    protected final Spatial target;
    protected final Attachment attachment;

    public static enum Attachment{
        Head,
        Tail
    }

    public AttachedToBoneControl(String boneName,Spatial target,Attachment attachment){
        this.boneName=boneName;
        this.target=target;
        this.attachment=attachment;
    }

    public AttachedToBoneControl(String boneName,Attachment attachment){
        this(boneName,null,attachment);
    }

    public AttachedToBoneControl(String boneName){
        this(boneName,null,Attachment.Head);
    }
    @Override
    protected void controlUpdate(float tpf) {
        Spatial parent=spatial.getParent();
        assert parent!=null;
        if(parent==null)return;
        Spatial target=this.target;
        if(target==null)target=parent;
        target.depthFirstTraversal(s->{
            SkeletonControl skec=s.getControl(SkeletonControl.class);
            if(skec!=null){
                Skeleton spooky=skec.getSkeleton();
                Bone b=spooky.getBone(boneName);
                if(b==null)return;


                // Get bone transform in worldspace
                tmpTransform.getRotation().loadIdentity();;
                tmpTransform.getScale().set(1,1,1);
                
                if(attachment==Attachment.Head){
                    tmpTransform.getTranslation().set(0,0,0);
                }else{
                    tmpTransform.getTranslation().set(0,0,-b.getLength());
                }

                tmpTransform.combineWithParent(b.getModelSpaceTransform());
                tmpTransform.combineWithParent(s.getWorldTransform());

                // To local space
                parent.getWorldTransform().toTransformMatrix(tmpMatrix);
                tmpMatrix.invertLocal();
                tmpTransform2.fromTransformMatrix(tmpMatrix);
                tmpTransform.combineWithParent(tmpTransform2);
                
                spatial.setLocalTransform(tmpTransform);
            }
        });
        
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        // TODO Auto-generated method stub

    }
}