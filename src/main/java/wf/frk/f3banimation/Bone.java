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


public final class Bone implements Savable, JmeCloneable {

    public static final int SAVABLE_VERSION = 3;
    private float length=0;
    private String name;
    private Bone parent;
    private ArrayList<Bone> children = new ArrayList<Bone>();
    
    private Transform restTrLS=new Transform();   
    private Transform restTrMS=new Transform();    
    
    private Matrix4f inversedRestTrMS=new Matrix4f(); 
    

    private Transform boneTrLS=new Transform(); 
    private Transform boneTrMS=new Transform();


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


    public void setLength(float v){
        length=v;
    }

    public float getLength(){
        return length;
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


    public void getTransform(Matrix4f outTransform) {
        boneTrMS.toTransformMatrix(outTransform);
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


    public final void update() {
        if(parent != null){
            boneTrMS.set(boneTrLS);
            boneTrMS.combineWithParent(parent.boneTrMS);
        }else{
            boneTrMS.set(boneTrLS);
        }


        for(int i=children.size() - 1;i >= 0;i--){
            children.get(i).update();
        }
    }


    public void setLocalTransform(Transform tr){
        boneTrLS.set(tr);
    }

    public void blendLocalTransform(Transform tr,float strength) {
        boneTrLS.interpolateTransforms(boneTrLS, tr, strength);
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

}
