/*
 * Copyright (c) 2009-2018 jMonkeyEngine
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

import com.jme3.export.*;
import com.jme3.material.MatParamOverride;
import com.jme3.math.*;
import com.jme3.scene.*;
import com.jme3.shader.VarType;
import com.jme3.util.SafeArrayList;
import com.jme3.util.TempVars;
import com.jme3.util.clone.Cloner;
import com.jme3.util.clone.JmeCloneable;

import java.io.IOException;
import java.util.ArrayList;

/**
 * <code>Bone</code> describes a bone in the bone-weight skeletal animation
 * system. A bone contains a name and an index, as well as relevant
 * transformation data.
 * 
 * A bone has 3 sets of transforms :
 * 1. The bind transforms, that are the transforms of the bone when the skeleton
 * is in its rest pose (also called bind pose or T pose in the literature). 
 * The bind transforms are expressed in Local space meaning relatively to the 
 * parent bone.
 * 
 * 2. The Local transforms, that are the transforms of the bone once animation
 * or user transforms has been applied to the bind pose. The local transforms are
 * expressed in Local space meaning relatively to the parent bone.
 * 
 * 3. The Model transforms, that are the transforms of the bone relatives to the 
 * rootBone of the skeleton. Those transforms are what is needed to apply skinning 
 * to the mesh the skeleton controls.
 * Note that there can be several rootBones in a skeleton. The one considered for 
 * these transforms is the one that is an ancestor of this bone.
 *
 * @author Kirill Vainer
 * @author Rémy Bouquet
 */
public final class Bone implements Savable, JmeCloneable {

    public static final int SAVABLE_VERSION = 3;

    private String name;
    private Bone parent;
    private ArrayList<Bone> children = new ArrayList<Bone>();
    
    private Transform restTrLS=new Transform();   
    private Transform restTrMS=new Transform();    
    
    // just restTrMS inverted, we store it a matrix4f just because it's more convenient later in the code   
    private Matrix4f inversedRestTrMS=new Matrix4f(); 
    

    private Transform boneTrLS=new Transform(); 
    private Transform boneTrMS=new Transform();

    private Node attachNode;
    private Geometry targetGeometry = null;

    public Bone(String name) {
        if (name == null)
            throw new IllegalArgumentException("Name cannot be null");        
        this.name = name;       
    }

    
    public Bone() {
    }
    
    @Override   
    public Object jmeClone() {
        try {
            Bone clone = (Bone)super.clone();
            return clone;
        } catch (CloneNotSupportedException ex) {
            throw new AssertionError();
        }
    }     

    @Override   
    public void cloneFields( Cloner cloner, Object original ) {
        this.parent = cloner.clone(parent);
        this.children = cloner.clone(children);    
        
        this.inversedRestTrMS=cloner.clone(inversedRestTrMS);
        
        this.boneTrLS=cloner.clone(boneTrLS);        
        this.boneTrMS=cloner.clone(boneTrMS);     

        this.restTrLS= cloner.clone(restTrLS);       
        this.restTrMS= cloner.clone(restTrMS);       
    }

    
    public String getName() {
        return name;
    }

   
    public Bone getParent() {
        return parent;
    }

   
    public ArrayList<Bone> getChildren() {
        return children;
    }

    
    public Transform getLocalTransform(){
        return boneTrLS;
    }

   
   
   


    public Transform getRestTransform(){
        return restTrLS;
    }

    public Transform getRestTransformModelSpace(){
        return restTrMS;
    }
    public void addChild(Bone bone) {
        children.add(bone);
        bone.parent = this;
    }

    private void updateAttachNode() {
        Node attachParent = attachNode.getParent();
        if (attachParent == null || targetGeometry == null
                || targetGeometry.getParent() == attachParent
                && targetGeometry.getLocalTransform().isIdentity()) {
            /*
             * The animated meshes are in the same coordinate system as the
             * attachments node: no further transforms are needed.
             */
            attachNode.setLocalTransform(boneTrMS);

        } else if (targetGeometry.isIgnoreTransform()) {
            /*
             * The animated meshes ignore transforms: match the world transform
             * of the attachments node to the bone's transform.
             */
            attachNode.setLocalTransform(boneTrMS);

            attachNode.getLocalTransform().combineWithParent(attachNode.getParent().getWorldTransform().invert());

        } else {
            Spatial loopSpatial = targetGeometry;
            Transform combined = new Transform().set(boneTrMS);
            /*
             * Climb the scene graph applying local transforms until the
             * attachments node's parent is reached.
             */
            while (loopSpatial != attachParent && loopSpatial != null) {
                Transform localTransform = loopSpatial.getLocalTransform();
                combined.combineWithParent(localTransform);
                loopSpatial = loopSpatial.getParent();
            }
            attachNode.setLocalTransform(combined);
        }
    }
       

 

    public void setRestTransform(Transform tr) {
        restTrLS.set(tr);
    }

    /**
     * Reset the bone and its children to bind pose.
     */
    public void reset() {
        boneTrLS.set(restTrLS);

        for (int i = children.size() - 1; i >= 0; i--) {
            children.get(i).reset();
        }
    }

     /**
     * Stores the skinning transform in the specified Matrix4f.
     * The skinning transform applies the animation of the bone to a vertex.
     * 
     * This assumes that the world transforms for the entire bone hierarchy
     * have already been computed, otherwise this method will return undefined
     * results.
     * 
     * @param outTransform
     */
    public void getOffsetTransform(Matrix4f outTransform) {
        boneTrMS.toTransformMatrix(outTransform);
        outTransform.multLocal(inversedRestTrMS);
    }


    public void updateRestPose() {

        if(parent != null){
            restTrMS.set(restTrLS);
            restTrMS.combineWithParent(parent.restTrMS);
        }else{
            restTrMS.set(restTrLS);
        }

        restTrMS.toTransformMatrix(inversedRestTrMS);
        inversedRestTrMS.invertLocal();

        for(int i=children.size() - 1;i >= 0;i--){
            children.get(i).updateRestPose();
        }

    }
    // public final void update() {
    //     update(true);
    // }
    public final void update(){//boolean recomputeMs) {
        // recomputeMs=true;
        // userSet=false;
        // if(recomputeMs&&!userSet){
            if (parent != null) {
                boneTrMS.set(boneTrLS);
                boneTrMS.combineWithParent(parent.boneTrMS);
            } else {
                boneTrMS.set(boneTrLS);
            }
        // }

        // boneTrMS.setRotation(new Quaternion());

        if (attachNode != null) {
            updateAttachNode();
        }

        for (int i = children.size() - 1; i >= 0; i--) {
            children.get(i).update();
        }

    }

    public Node getAttachmentsNode(int boneIndex, SafeArrayList<Geometry> targets) {
        targetGeometry = null;
        /*
         * Search for a geometry animated by this particular bone.
         */
        for (Geometry geometry : targets) {
            Mesh mesh = geometry.getMesh();
            if (mesh != null && mesh.isAnimatedByBone(boneIndex)) {
                targetGeometry = geometry;
                break;
            }
        }

        if (attachNode == null) {
            attachNode = new Node(name + "_attachnode");
            attachNode.setUserData("AttachedBone", this);
            //We don't want the node to have a numBone set by a parent node so we force it to null
            attachNode.addMatParamOverride(new MatParamOverride(VarType.Int, "NumberOfBones", null));
        }

        return attachNode;
    }

    public void setAttachmentsNode(int boneIndex, SafeArrayList<Geometry> targets,Node n) {
        targetGeometry = null;
        /*
         * Search for a geometry animated by this particular bone.
         */
        for (Geometry geometry : targets) {
            Mesh mesh = geometry.getMesh();
            if (mesh != null && mesh.isAnimatedByBone(boneIndex)) {
                targetGeometry = geometry;
                break;
            }
        }


         attachNode=n;
    }
    public void setLocalTransform(Transform tr){
        boneTrLS.set(tr);
    }


    // Transform tmp_boneTrLs=new Transform();

    public void blendLocalTransform(Transform tr,float strength) {
        boneTrLS.interpolateTransforms(boneTrLS, tr, strength);

        // Quaternion r1=restTrLS.getRotation().inverse().mult(tr.getRotation());
        // tmp_boneTrLs.getRotation().set(tr.getRotation());
        // boneTrLS.getRotation().set(restTrLS.getRotation());//.mult(tr.getRotation()));

        // boneTrLS.getRotation().set(r1.mult(boneTrLS.getRotation()));

        // boneTrLS.getRotation().set(restTrLS.getRotation());//tr.getRotation()); 
        // boneTrLS.getRotation().multLocal();
        // boneTrLS.getRotation().multLocal(restTrLS.getRotation());
        // tmp_boneTrLs.combineWithParent(boneTrLS);
        // boneTrLS.interpolateTransforms(boneTrLS, tmp_boneTrLs, strength);
        //  boneTrLS.interpolateTransforms(boneTrLS, tr, strength);
    }

    @Override
    public void write(JmeExporter ex) throws IOException {

    }

    @Override
    public void read(JmeImporter im) throws IOException {

    }


	public Transform getModelSpaceTransform() {
		return boneTrMS.clone();
	}


    @Deprecated
	public void setLocalRotation(Quaternion v) {
        Transform tr=getLocalTransform();
        tr.setRotation(v);
        setLocalTransform(tr);
    }
    
    
    @Deprecated
	public void setLocalTranslation(Vector3f v) {
        Transform tr=getLocalTransform();
        tr.setTranslation(v);
        setLocalTransform(tr);
    }
    
    @Deprecated
	public Quaternion getLocalRotation() {
        Transform tr=getLocalTransform();
        return tr.getRotation();
    }
    
    
    @Deprecated
	public Vector3f getLocalTranslation( ) {
        Transform tr=getLocalTransform();
        return tr.getTranslation();
	}


    @Deprecated
	public Quaternion getModelSpaceRotation() {
		return getModelSpaceTransform().getRotation();
	}
    


    @Deprecated
	public Vector3f getModelSpacePosition() {
		return getModelSpaceTransform().getTranslation();
	}


    @Deprecated
	public Vector3f getBindPosition() {
		return getRestTransform().getTranslation();
    }
    

    @Deprecated
	public Quaternion getBindRotation() {
		return getRestTransform().getRotation();
    }
    

    @Deprecated
	public Vector3f getBindScale() {
		return getRestTransform().getScale();
    }
    final Transform _tmpTr1=new Transform();
    final Transform _tmpTr2=new Transform();
    final Matrix4f _tmpMt1=new Matrix4f();
    // boolean userSet=false;
    @Deprecated
    public Transform modelToLocalSpaceTransform(Vector3f translation, Quaternion rotation) {  
        // this.boneTrMS.setTranslation(translation);
        // this.boneTrMS.setRotation(rotation);
        // userSet=true; // TODO remove
        _tmpTr1.setTranslation(translation);
        _tmpTr1.setRotation(rotation);
        _tmpTr1.getScale().set(1,1,1);//new Vector3f(1,1,1));
        if(parent!=null){
            _tmpTr2.fromTransformMatrix(parent.getModelSpaceTransform().toTransformMatrix(_tmpMt1).invertLocal());
            _tmpTr1.combineWithParent(_tmpTr2);
        }
        return _tmpTr1;
        // Transform l=getLocalTransform();
        // l.setTranslation(_tmpTr1.getTranslation());
        // l.setRotation(_tmpTr1.getRotation());
        // // l.setScale(new Vector3f(1,1,1));
        // // if(l.getTranslation().distance(getLocalTranslation())>0.1)return;
        // setLocalTransform(l);
        // update();

    }

    @Deprecated
	public Transform getCombinedTransform(Vector3f position, Quaternion rotation) {
        Transform l=getLocalTransform();
        Vector3f localPos=l.getTranslation();
        Quaternion localRot=l.getRotation();
        rotation.mult(localPos, _tmpTr1.getTranslation()).addLocal(position);
        _tmpTr1.setRotation(rotation).getRotation().multLocal(localRot);
        return _tmpTr1;
	}

    @Deprecated

	public Quaternion getModelBindInverseRotation() {
		return restTrMS.getRotation();
	}
}
