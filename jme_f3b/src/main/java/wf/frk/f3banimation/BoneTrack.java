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
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.util.TempVars;
import com.jme3.util.clone.Cloner;
import com.jme3.util.clone.JmeCloneable;

import wf.frk.f3banimation.utils.F3bMath;

import java.io.IOException;
import java.util.BitSet;

/**
 * Contains a list of transforms and times for each keyframe.
 * 
 * @author Riccardo Balbo, Kirill Vainer
 */
public class BoneTrack implements JmeCloneable, Track {

    /**
     * Bone index in the skeleton which this track affects.
     */
    private int targetBoneIndex;
    
    /**
     * Transforms and times for track.
     */
    private Transform frames[];
    private float[] times;
    


    /**
     * Serialization-only. Do not use.
     */
    public BoneTrack() {
    }

    /**
     * Creates a bone track for the given bone index
     * @param targetBoneIndex the bone index
     * @param times a float array with the time of each frame
     * @param frames the Transform of the bone for each frame
     */
    public BoneTrack(int targetBoneIndex, float[] times, Transform frames[]) {
        this.targetBoneIndex = targetBoneIndex;
        this.setKeyframes(times, frames);
    }


    /**
     * Creates a bone track for the given bone index
     * @param targetBoneIndex the bone's index
     */
    public BoneTrack(int targetBoneIndex) {
        this.targetBoneIndex = targetBoneIndex;
    }

    /**
     * @return the bone index of this bone track.
     */
    public int getTargetBoneIndex() {
        return targetBoneIndex;
    }

  
    /**
     * returns the arrays of time for this track
     * @return 
     */
    public float[] getTimes() {
        return times;
    }

    public Transform[] getFrames(){
        return frames;
    }

    public Transform getFrame(int i){
        return frames[i];
    }


    /**
     * Set the translations and rotations for this bone track
     *
     * @param times the time of each frame, measured from the start of the track
     * (not null, length&gt;0)
     * @param frames the Transform of the bone for each frame (not null,
     * same length as times)
     */
    public void setKeyframes(float[] times, Transform[] frames) {
        if (times.length == 0) {
            throw new RuntimeException("BoneTrack with no keyframes!");
        }

        assert frames != null;
        assert times.length == frames.length;

        this.times = times;

        this.frames=frames;
    }


    /**
     * 
     * Modify the bone which this track modifies in the skeleton to contain
     * the correct animation transforms for a given time.
     * The transforms can be interpolated in some method from the keyframes.
     *
     * @param time the current time of the animation
     * @param weight the weight of the animation
     * @param control
     * @param channel
     * @param vars
     */
    public void setTime(float time, float weight, AnimControl control, AnimChannel channel, boolean loop,TempVars vars) {
        BitSet affectedBones = channel.getAffectedBones();
        if (affectedBones != null && !affectedBones.get(targetBoneIndex)) {
            return;
        }
        
        Bone target = control.getSkeleton().getBone(targetBoneIndex);

        // Vector3f tempV = vars.vect1;
        // Vector3f tempS = vars.vect2;
        // Quaternion tempQ = vars.quat1;
        // Vector3f tempV2 = vars.vect3;
        // Vector3f tempS2 = vars.vect4;
        // Quaternion tempQ2 = vars.quat2;
        

        
        int lastFrame = times.length - 1 ;
        if(lastFrame<0)lastFrame=0;

        float fractTime;
        if(loop){
            fractTime=F3bMath.mod(time,times[0],times[lastFrame]);
        }else{
            fractTime=time;
            if(fractTime>times[lastFrame])fractTime=times[lastFrame];
            if(fractTime<times[0])fractTime=times[0];
        }
        
        float startTime=times[0];
        float endTime=times[0];
        int startFrame = 0;
        int endFrame = 0;
        // System.out.println("Frames "+times.length);
        assert times.length!=0;
        if(times.length>1){    
            int i=1;
            while(true){
                
                if(i>=times.length){
                    assert true;
                    break;
                }

                if(times[i]>=fractTime){
                    endFrame=i;
                    endTime=times[endFrame];
                    
                    startFrame=i-1;
                    startTime=times[startFrame];

                    if(time>fractTime&&startFrame==0){
                        startFrame=lastFrame;
                    }
                    
                    break;
                }

                i++;
            }
        }

       


        float blend = (fractTime- startTime) / (endTime - startTime);

        // If looping -> last frame= first frame
        // if(time>fractTime&&startFrame==0){           
        //     startFrame=lastFrame;            
        // }

        Transform frameA=getFrame(startFrame);        
        Transform frameB=getFrame(endFrame);

        tmp_Tr.set(frameA);
        tmp_Tr.interpolateTransforms(tmp_Tr,frameB,blend);
           

//        if (weight != 1f) {
        target.blendLocalTransform(tmp_Tr, weight);
    //        } else {
    //            target.setAnimTransforms(tempV, tempQ, scales != null ? tempS : null);
    //        }
    // }
    }

    Transform tmp_Tr=new Transform();
    
    
    /**
     * @return the length of the track
     */
    public float getLength() {
        return times == null ? 0 : times[times.length - 1] - times[0];
    }

    @Override
    public float[] getKeyFrameTimes() {
        return times;
    }

    /**
     * Create a deep clone of this track.
     *
     * @return a new track
     */
    @Override
    public BoneTrack clone() {
        return Cloner.deepClone(this);
    }

    /**
     * Create a shallow clone for the JME cloner.
     *
     * @return a new track
     */
    @Override
    public BoneTrack jmeClone() {
        try {
            return (BoneTrack) super.clone();
        } catch (CloneNotSupportedException exception) {
            throw new RuntimeException("Can't clone track", exception);
        }
    }

    /**
     * Callback from {@link com.jme3.util.clone.Cloner} to convert this
     * shallow-cloned track into a deep-cloned one, using the specified cloner
     * to resolve copied fields.
     *
     * @param cloner the cloner currently cloning this control (not null)
     * @param original the track from which this track was shallow-cloned
     * (unused)
     */
    @Override
    public void cloneFields(Cloner cloner, Object original) {
        frames = cloner.clone(frames);
        times = cloner.clone(times);
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(targetBoneIndex, "boneIndex", 0);
        oc.write(frames, "frames", null);
        oc.write(times, "times", null);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule ic = im.getCapsule(this);
        targetBoneIndex = ic.readInt("boneIndex", 0);
        times = ic.readFloatArray("times", null);
        if(times!=null){
            frames=new Transform[times.length];
            frames = (Transform[])ic.readSavableArray("frames",frames);
        }else{
            frames=null;
        }
    }

    public void setTime(float time, float weight, AnimControl control, AnimChannel channel) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


}
