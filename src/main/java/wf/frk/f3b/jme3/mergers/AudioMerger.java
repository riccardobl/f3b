package wf.frk.f3b.jme3.mergers;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;

import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioData.DataType;
import com.jme3.physicsloader.PhysicsShape;
import com.jme3.physicsloader.rigidbody.RigidBody;
import com.jme3.physicsloader.rigidbody.RigidBodyType;
import com.jme3.scene.Node;

import f3b.Datas.Data;
import f3b.Physics.Constraint;
import lombok.experimental.ExtensionMethod;
import lombok.extern.log4j.Log4j2;
import wf.frk.f3b.jme3.F3bContext;

@Log4j2
@ExtensionMethod({wf.frk.f3b.jme3.ext.f3b.TypesExt.class})
public class AudioMerger implements Merger{
    AssetManager AM;

    public AudioMerger(AssetManager assetManager){
        super();
        AM=assetManager;
    }

    private String toAMPath(String rpath, AssetManager am, Node root) {
        String path=root.getName();
        int last_sep_i=path.lastIndexOf("/");
        if(last_sep_i==-1) path="/"+rpath;
        else path=path.substring(0,last_sep_i)+"/"+rpath;
        if(am.locateAsset(new AssetKey(path))!=null){ return path; }
        return rpath;

    }

    @Override
    public void apply(Data src, Node root, F3bContext context) {

        for(f3b.Audio.Speaker data:src.getSpeakersList()){
            AudioNode an=new AudioNode(AM,toAMPath(data.getRpath(),AM,root),DataType.Buffer);
            an.setName(data.getName());
            an.setPositional(true);
            an.setVolume(data.getVolume());
            an.setPitch(data.getPitch());
            an.setReverbEnabled(true);
            an.setLooping(true);
            // an.play();
            // log.debug("Load audio node {}",an);

            an.setMaxDistance(data.getDistanceMax());
            an.setRefDistance(data.getDistanceReference());
            an.setVelocityFromTranslation(true);

            // Reflect to keep compatibility with official jme code.
            float attenuation=data.getAttenuation();
            if(attenuation!=1.0f){
                try{
                    Method attm=AudioNode.class.getDeclaredMethod("setAttenuation",float.class);
                    if(attm!=null){
                        log.debug("Set audio node attenuation {}",attenuation);
                        attm.invoke(an,attenuation);
                        
                    }else   log.debug("Can't set audio node attenuation {} - not available in source",attenuation);
                }catch(Exception e){
                    log.debug("Can't set audio node attenuation {} - not available in source",attenuation);

                }
            }

            // if(inner_angle!=360&&outer_angle!=360){
            //     an.setDirection(true);
            //     an.setInnerAngle(innerAngle);
            //     an.setOuterAngle(outerAngle);
            //     an.setDirection(direction);

            // }
            String id=data.getId();

            context.put(id,an);

        }

    }

}
