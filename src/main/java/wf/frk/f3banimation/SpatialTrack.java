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
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.util.TempVars;
import com.jme3.util.clone.Cloner;
import com.jme3.util.clone.JmeCloneable;

import wf.frk.f3banimation.utils.F3bMath;

import java.io.IOException;

/**
 * This class represents the track for spatial animation.
 * 
 * @author Riccardo Balbo, Marcin Roguski (Kaelthas)
 */
@Deprecated
public class SpatialTrack implements JmeCloneable, Track {

    private Transform frames[];


    /**
     * The spatial to which this track applies.
     * Note that this is optional, if no spatial is defined, the AnimControl's Spatial will be used.
     */
    private Spatial trackSpatial;

    /** 
     * The times of the animations frames. 
     */
    private float[] times;

    public SpatialTrack() {
    }

    /**
     * Creates a spatial track for the given track data.
     * 
     * @param times
     *            a float array with the time of each frame
     * @param translations
     *            the translation of the bone for each frame
     * @param rotations
     *            the rotation of the bone for each frame
     * @param scales
     *            the scale of the bone for each frame
     */
    public SpatialTrack(float[] times, Transform frames[]) {
        setKeyframes(times, frames);
    }
    public Transform[] getFrames(){
        return frames;
    }

    public Transform getFrame(int i){
        return frames[i];
    }
    /**
     * 
     * Modify the spatial which this track modifies.
     * 
     * @param time
     *            the current time of the animation
     */
    public void setTime(float time, float weight, AnimControl control, AnimChannel channel, boolean loop,TempVars vars) {
        Spatial spatial = trackSpatial;
        if (spatial == null) {
            spatial = control.getSpatial();
        }

        Vector3f tempV = vars.vect1;
        Vector3f tempS = vars.vect2;
        Quaternion tempQ = vars.quat1;
        Vector3f tempV2 = vars.vect3;
        Vector3f tempS2 = vars.vect4;
        Quaternion tempQ2 = vars.quat2;
        
        int lastFrame = times.length - 1;
       
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

        Transform frameA=getFrame(startFrame);        
        Transform frameB=getFrame(endFrame);

        tmp_Tr.set(frameA);
        tmp_Tr.interpolateTransforms(tmp_Tr,frameB,blend);

        spatial.getLocalTransform().interpolateTransforms( 
            spatial.getLocalTransform(),tmp_Tr,  weight);

        spatial.setLocalTransform(
            spatial.getLocalTransform()
            );
     
   
    }
    Transform tmp_Tr=new Transform();

    /**
     * Set the translations, rotations and scales for this track.
     * 
     * @param times
     *            a float array with the time of each frame
     * @param translations
     *            the translation of the bone for each frame
     * @param rotations
     *            the rotation of the bone for each frame
     * @param scales
     *            the scale of the bone for each frame
     */
    public void setKeyframes(float[] times, Transform[] frames) {
        if (times.length == 0) {
            throw new RuntimeException("BoneTrack with no keyframes!");
        }

        this.times = times;
      this.frames=frames;
    }


    /**
     * @return the arrays of time for this track
     */
    public float[] getTimes() {
            return times;
    }

  

    /**
     * @return the length of the track
     */
    public float getLength() {
            return times == null ? 0 : times[times.length - 1] - times[0];
    }

    /**
     * Create a clone with the same track spatial.
     *
     * @return a new track
     */
    @Override
    public SpatialTrack clone() {
        Cloner cloner = new Cloner();
        cloner.setClonedValue(trackSpatial, trackSpatial);
        return cloner.clone(this);
    }

    @Override
    public float[] getKeyFrameTimes() {
        return times;
    }

    public void setTrackSpatial(Spatial trackSpatial) {
        this.trackSpatial = trackSpatial;
    }

    public Spatial getTrackSpatial() {
        return trackSpatial;
    }

    /**
     * Create a shallow clone for the JME cloner.
     *
     * @return a new track
     */
    @Override
    public SpatialTrack jmeClone() {
        try {
            return (SpatialTrack) super.clone();
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
      frames=cloner.clone(frames);
        trackSpatial = cloner.clone(trackSpatial);
        times = cloner.clone(times);
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(frames, "frames", null);
        oc.write(times, "times", null);
        oc.write(trackSpatial, "trackSpatial", null);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule ic = im.getCapsule(this);
        times = ic.readFloatArray("times", null);
        trackSpatial = (Spatial) ic.readSavable("trackSpatial", null);
        if(times!=null){
            frames=new Transform[times.length];
            frames = (Transform[])ic.readSavableArray("frames",frames);
        }else{
            frames=null;
        }
    }

   
}
