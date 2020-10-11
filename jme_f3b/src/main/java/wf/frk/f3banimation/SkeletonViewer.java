/*
 * Copyright (c) 2009-2012 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package wf.frk.f3banimation;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.shape.Sphere;

import wf.frk.f3banimation.AttachedToBoneControl.Attachment;

public class SkeletonViewer extends AbstractControl{
    public static class SkeletonMaterial{
        public Material head;
        public Material bone;
        public Material tail;
    }

    public static class SkeletonParts{
        public Spatial head;
        public Spatial bone;
        public Spatial tail;
    }

    protected Node debugRoot;
    protected SkeletonMaterial boneMat;
    protected SkeletonParts boneModel;

    public SkeletonViewer(AssetManager assetManager,boolean inFront,Node root){
        this(assetManager, null,null, root);
        if(inFront){
            boneMat.head.getAdditionalRenderState().setDepthTest(false);
            boneMat.bone.getAdditionalRenderState().setDepthTest(false);
            boneMat.tail.getAdditionalRenderState().setDepthTest(false);
        }
    }

    public SkeletonViewer(AssetManager assetManager,SkeletonMaterial boneMat,SkeletonParts boneModel,Node root){
        if(boneMat==null){
            boneMat=new SkeletonMaterial();
            boneMat.head=new Material(assetManager,"f3b_resources/MatCap.j3md");
            boneMat.head.setTexture("MatCap", assetManager.loadTexture("f3b_resources/matcap6.png"));

            boneMat.bone=new Material(assetManager,"f3b_resources/MatCap.j3md");
            boneMat.bone.setTexture("MatCap", assetManager.loadTexture("f3b_resources/matcap8.png"));
            

            boneMat.tail=new Material(assetManager,"f3b_resources/MatCap.j3md");
            boneMat.tail.setTexture("MatCap", assetManager.loadTexture("f3b_resources/matcap7.png"));
            
        }
        if(boneModel==null){
            boneModel=new SkeletonParts();

            Sphere s=new Sphere(16, 16, 0.03f);
            Geometry head=new Geometry("boneHead",s);

            boneModel.head=head;
            boneModel.head.setMaterial(boneMat.head);
            boneModel.head.setQueueBucket(Bucket.Transparent);

            boneModel.bone=assetManager.loadModel("f3b_resources/bone.f3b");
            boneModel.bone.setMaterial(boneMat.bone);
            boneModel.bone.setQueueBucket(Bucket.Transparent);
            
            s=new Sphere(16, 16, 0.02f);
            Geometry tail=new Geometry("boneTail",s);
            boneModel.tail=tail;
            boneModel.tail.setMaterial(boneMat.tail);
            boneModel.tail.setQueueBucket(Bucket.Transparent);

        }
        this.boneModel=boneModel;        
        this.boneMat=boneMat;
        this.debugRoot=root;
    }

    protected boolean hasChild(String name){
        for(Spatial s:debugRoot.getChildren()){
            if(s.getName()!=null&&s.getName().equals(name))return true;
        }
        return false;

    }

    @Override
    protected void controlUpdate(float tpf) {
        spatial.depthFirstTraversal(sx->{
            SkeletonControl skec=sx.getControl(SkeletonControl.class);
            if(skec==null)return;
            Skeleton spooky=skec.getSkeleton();
            for(int i=0;i<spooky.getBoneCount();i++){
                Bone b=spooky.getBone(i);
                String bname=b.getName();

                String id="bone_"+b.getName()+b.hashCode();
                String idhead=id+"_head";
                if(!hasChild(idhead)){


                    Node head=new Node(idhead);
                    head.attachChild(boneModel.head.clone());             
                    head.getChild(0).setLocalScale(b.getLength()); 
                    debugRoot.attachChild(head);
        
                    AttachedToBoneControl atb=new AttachedToBoneControl(bname,sx,Attachment.Head);
                    head.addControl(atb);


                    Node tail=new Node(id+"_tail");
                    tail.attachChild(boneModel.tail.clone());
                    tail.getChild(0).setLocalScale(b.getLength());
                    debugRoot.attachChild(tail);
        
                    atb=new AttachedToBoneControl(bname,sx,AttachedToBoneControl.Attachment.Tail);
                    tail.addControl(atb);


                    Node bone=new Node(id);
                    bone.attachChild(boneModel.bone.clone());
                    bone.getChild(0).setLocalScale(b.getLength());

                    debugRoot.attachChild(bone);
                    atb=new AttachedToBoneControl(bname,sx,Attachment.Head);
                    bone.addControl(atb);
                    bone.setMaterial(  boneMat.bone);
                }
            }

        });
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {

    }

    

}