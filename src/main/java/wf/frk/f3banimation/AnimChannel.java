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

import java.util.BitSet;

import wf.frk.f3banimation.blending.BlendingFunction;
import wf.frk.f3banimation.blending.FixedBlendingFunction;
import wf.frk.f3banimation.utils.TriFunction;
import com.jme3.util.TempVars;


/**
 * <code>AnimChannel</code> provides controls, such as play, pause,
 * fast forward, etc, for an animation. The animation
 * channel may influence the entire model or specific bones of the model's
 * skeleton. A single model may have multiple animation channels influencing
 * various parts of its body. For example, a character model may have an
 * animation channel for its feet, and another for its torso, and
 * the animations for each channel are controlled independently.
 * 
 * @author Kirill Vainer
 */
public final class AnimChannel {

    
    private AnimControl control;
    private BitSet affectedBones;
    private Animation animation;

    private TriFunction<AnimChannel,Animation,Float,Float> actionBlendFun;
    private TriFunction<AnimChannel,Animation,Float,Float> channelBlendFun=
    BlendingFunction.newSimple(()->1f);
    private TriFunction<AnimChannel,Animation,Float,Float> animTimeFun=null;

    private TriFunction<AnimChannel,Animation,Float,Float> oldAnimTimeFun=null;
    private TriFunction<AnimChannel,Animation,Float,Float> oldActionBlendFun=null;
    // private TriFunction<AnimChannel,Animation,Float,Float> oldActionBlendOutFun=null;
    
    
    private Animation oldAnim;
    // private boolean notified=false;

 
    
    public AnimChannel(){
        
    }
    
    public AnimChannel(AnimControl control){
        this.control = control;
    }

   
    
    public void setChannelBlendFun(TriFunction<AnimChannel,Animation,Float,Float> fun){
        channelBlendFun=fun;
    }

    public AnimControl getControl() {
        return control;
    }
    

    public String getAnimationName() {
        return animation != null ? animation.getName() : null;
    }

    public Animation getAnimation() {
        return animation;
    }


    public float getAnimMaxTime(){
        return animation != null ? animation.getLength() : 0f;
    }

    public float getAnimationLength(String name){
        Animation anim= control.animationMap.get(name);
        return anim!=null?anim.getLength():0;
    }
    
    public void clearAction(
        TriFunction<AnimChannel,Animation,Float,Float> timeFunction,
        TriFunction<AnimChannel,Animation,Float,Float> newAnimInFunction       
    ){
        setAction(null,timeFunction,newAnimInFunction);
    }

    public void setAction(
        String name,
        TriFunction<AnimChannel,Animation,Float,Float> timeFunction,
        TriFunction<AnimChannel,Animation,Float,Float> newAnimInFunction
        // TriFunction<AnimChannel,Animation,Float,Float> oldAnimOutFunction
        

    ){
        if (name == null){
            this.animation=null;
            return;
        }

        Animation anim = control.animationMap.get(name);
        if (anim == null)
            return;

        control.notifyAnimChange(this, name);
 
        if (animation != null){
            oldAnim = animation;
            oldAnimTimeFun = animTimeFun;   
            oldActionBlendFun=actionBlendFun;
            // oldActionBlendOutFun=oldAnimOutFunction;
        }else{
            oldAnim = null;
            oldAnimTimeFun=null;
        }
       if(timeFunction!=null) animTimeFun=timeFunction;
        if(newAnimInFunction!=null)actionBlendFun=newAnimInFunction;
        animation = anim;
        // notified = false;
    }


   


    public void addAllBones() {
        affectedBones = null;
    }


    public void addBone(String name) {
        addBone(control.getSkeleton().getBone(name));
    }

 
    public void addBone(Bone bone) {
        int boneIndex = control.getSkeleton().getBoneIndex(bone);
        if(affectedBones == null) {
            affectedBones = new BitSet(control.getSkeleton().getBoneCount());
        }
        affectedBones.set(boneIndex);
    }


    public void addToRootBone(String name) {
        addToRootBone(control.getSkeleton().getBone(name));
    }

   
    public void addToRootBone(Bone bone) {
        addBone(bone);
        while (bone.getParent() != null) {
            bone = bone.getParent();
            addBone(bone);
        }
    }


    public void addFromRootBone(String name) {
        addFromRootBone(control.getSkeleton().getBone(name));
    }


    public void addFromRootBone(Bone bone) {
        addBone(bone);
        if (bone.getChildren() == null)
            return;
        for (Bone childBone : bone.getChildren()) {
            addBone(childBone);
            addFromRootBone(childBone);
        }
    }

    public BitSet getAffectedBones(){
        return affectedBones;
    }
    
    public void reset(){
     
            if(control.getSkeleton()!=null){
                control.getSkeleton().resetAndUpdate();
            }else{
                TempVars vars = TempVars.get();
                update(0, vars);
                vars.release();    
            }
        animation = null;
    }


    
    void update(float tpf, TempVars vars) {
        if (
                animation == null
                ||actionBlendFun==null
                ||channelBlendFun==null
                ||animTimeFun==null

        )   return;
        
         float time= animTimeFun.apply(this,animation, tpf);
        
         float blendAmount=this.actionBlendFun.apply(this,animation,tpf);

        // float invBlendAmount=1.f-blendAmount;

        
  
        if (oldAnim != null && blendAmount < 1.0f){
            float oldBlendAmount=oldActionBlendFun.apply(this,oldAnim,tpf);
            // if(oldActionBlendOutFun!=null){
            //     oldBlendAmount*=oldActionBlendOutFun.apply(this,oldAnim,tpf);
            // }
            oldBlendAmount-=blendAmount;
            if(oldBlendAmount<0)oldBlendAmount=0;
            else if(oldBlendAmount>1)oldBlendAmount=1;

            float oldChanBlendAmount=this.channelBlendFun.apply(this,oldAnim,tpf);
            float oldAnimTime=oldAnimTimeFun.apply(this,oldAnim,tpf);
            oldAnim.setTime(oldAnimTime,oldBlendAmount, control, this, vars);
        }else{
            // blendAmount = 1f;
            oldAnim = null;
        }

        if(animation!=null){
            float chanBlendAmount=this.channelBlendFun.apply(this,animation,tpf);

            animation.setTime(time, blendAmount*chanBlendAmount, control, this, vars);
        }
    }
}
