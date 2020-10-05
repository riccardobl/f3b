package wf.frk.f3banimation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import wf.frk.f3banimation.blending.BlendingFunction;
import wf.frk.f3banimation.utils.TriFunction;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;


/**
 * AnimationGroupControl
 */
public class AnimationGroupControl extends AbstractControl{
    private static final ArrayList<AnimChannel> emptyList=new ArrayList<AnimChannel>();
    private static final Logger LOGGER = Logger.getLogger(AnimationGroupControl.class.getName());

    protected final Map<String,ArrayList<AnimChannel>> _CACHED_ANIM_CHANNELS=new HashMap<String,ArrayList<AnimChannel>>(); 

    public static AnimationGroupControl of(Spatial sp){
        AnimationGroupControl c=sp.getControl(AnimationGroupControl.class);
        if(c==null){
            c=new AnimationGroupControl();
            sp.addControl(c);
        }
        return c;
    }



    @Override
    protected void controlUpdate(float tpf) {

    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {

    }

    public List<String> getAnimationNames(){
        ArrayList<String> names=new ArrayList<String>();
        this.spatial.depthFirstTraversal(s->{
            for(int i=0;i<s.getNumControls();i++){
                Control c=s.getControl(i);
                if(c instanceof AnimControl){
                    AnimControl ac=(AnimControl)c;
                    for(String n:ac.getAnimationNames()){
                        if(!names.contains(n)){
                            names.add(n);
                        }
                    }
                }
            }
        });
        return names;
    }

    public ArrayList<AnimChannel> getOrCreateAnimChannel(String tag){
        return getOrCreateAnimChannel(tag,null);
    }


    private String getAutoAnimChannelId(String anim){
        int priority[]=new int[]{-1};
        this.spatial.depthFirstTraversal(s->{
            for(int i=0;i<s.getNumControls();i++){
                Control c=s.getControl(i);
                if(c instanceof AnimControl){
                    AnimControl ac=(AnimControl)c;
                    Animation aa=ac.getAnim(anim);
                    if(aa!=null)priority[0]=aa.getPriority();
                }
            }
        });

        if(priority[0]==-1){
            LOGGER.log(Level.FINE,"Anim not found {0}",anim);
        }else{
            for(int i=0;i<priority[0];i++)    {
                LOGGER.log(Level.FINE,"Create channel index#{0} if it doesn't exist",i);
                getOrCreateAnimChannel("index#"+i,(chan)->{
                    // chan.setActionBlendFun(AnimationGroupControl.newFixedBlendFun(1f));
                    // chan.setChannelBlendFun(AnimationGroupControl.newFixedBlendFun(1f));
                });        
            }
            return "index#"+priority[0];
        }
        return null;
    }

    public ArrayList<AnimChannel> getOrCreateAnimChannelForAction(String tag,Consumer<AnimChannel> constructor){
        tag=getAutoAnimChannelId(tag);
        return getOrCreateAnimChannel(tag,constructor);
    }

    public ArrayList<AnimChannel> getOrCreateAnimChannel(String tag,Consumer<AnimChannel> constructor){
        ArrayList<AnimChannel> cc=_CACHED_ANIM_CHANNELS.get(tag);
        if(cc!=null)return cc;
        
        ArrayList chans=new ArrayList<AnimChannel>();
        this.spatial.depthFirstTraversal(s->{
            for(int i=0;i<s.getNumControls();i++){
                Control c=s.getControl(i);
                if(c instanceof AnimControl){
                    AnimControl ctr=(AnimControl)c;
                    AnimChannel chan=ctr.createChannel();
                    LOGGER.log(Level.FINE,"Create new anim channel {0} in {1}",new Object[]{tag,c});
                    if(constructor!=null)constructor.accept(chan);
                    chans.add(chan);
                }
            }
        });
        chans.trimToSize();
        _CACHED_ANIM_CHANNELS.put(tag,chans);
        return chans;
    }

    public void clearAnimChannel(String tag){
        clearAction(
            tag,
            null,
            BlendingFunction.newFadeOut(.5f, ()->1f)
        );
    
        _CACHED_ANIM_CHANNELS.remove(tag);
    }




    // public void createAnimChannel(String tag,BiFunction<AnimChannel,Animation,Float> channelBlendFun,BiFunction<AnimChannel,Animation,Float> actionBlendFun){
    //     for(AnimChannel c:getAnimChannel(tag)){
    //         c.setActionBlendFun(actionBlendFun);
    //         c.setChannelBlendFun(channelBlendFun);
    //     }
    // }
    


    // public ArrayList<AnimChannel> setAction(String anim,LoopMode mode){
    //     return setAction(null,anim,mode);
    // }

    public ArrayList<AnimChannel> setAction(
        String anim,
        TriFunction<AnimChannel,Animation,Float,Float> timeFun,
        TriFunction<AnimChannel,Animation,Float,Float> newActionBlendInFun
    ){
        return setAction(null,anim,false,timeFun,newActionBlendInFun);
    }

    public ArrayList<AnimChannel> clearAction(
        String anim,
        TriFunction<AnimChannel,Animation,Float,Float> timeFun,
        TriFunction<AnimChannel,Animation,Float,Float> newActionBlendInFun
    ){
        return setAction(null,anim,true,timeFun,newActionBlendInFun);
    }


    public ArrayList<AnimChannel> setAction(
        String chan_name,
        String anim,
        TriFunction<AnimChannel,Animation,Float,Float> timeFun,
        TriFunction<AnimChannel,Animation,Float,Float> newActionBlendInFun
    ){
        return setAction(chan_name,anim,false,timeFun,newActionBlendInFun);
    }

    public ArrayList<AnimChannel> clearChannel(
        String chan_name,
        TriFunction<AnimChannel,Animation,Float,Float> timeFun,
        TriFunction<AnimChannel,Animation,Float,Float> newActionBlendInFun
    ){
        return setAction(chan_name,null,true,timeFun,newActionBlendInFun);
    }


    protected ArrayList<AnimChannel> setAction(
        String chan_name,
        String anim,
        boolean unset,
        TriFunction<AnimChannel,Animation,Float,Float> timeFun,
        TriFunction<AnimChannel,Animation,Float,Float> newActionBlendInFun
    ){
        if(chan_name==null){
            String id=getAutoAnimChannelId(anim);
            if(id==null){
                LOGGER.log(Level.FINE,"Anim not found {0}",anim);
                return emptyList;
            }
            chan_name=id;
        }
        ArrayList<AnimChannel> cc=getOrCreateAnimChannel(chan_name);

        for(AnimChannel chan:cc){
            
            if(unset){
                LOGGER.log(Level.FINE,"Disable anim in chan {0}",chan_name);
                chan.clearAction(timeFun,newActionBlendInFun);
                continue;
            }
            // if(chan.isPaused()) chan.resume();
            
            // if(chan.getAnimationName()==null
            //     ||(
            //         (!chan.getAnimationName().equals(anim))
            //     )
            // ){
                LOGGER.log(Level.FINE,"Set anim {0} in chan {1}",new Object[]{anim,chan_name});
                chan.setAction(anim,timeFun,newActionBlendInFun);
                // chan.setLoopMode(mode);
                // if(blendFun!=null){
                    // chan.setChannelBlendFun(blendFun);
                // }
            // }

            
        }
        return cc;
    }



    // public ArrayList<AnimChannel> setAction(String chan_name,String anim,LoopMode mode){
    //     if(chan_name==null){
    //         String id=getAutoAnimChannelId(anim);
    //         if(id==null){
    //             LOGGER.debug("Anim not found {}",anim);
    //             return emptyList;
    //         }
    //         chan_name=id;
    //     }
    //     ArrayList<AnimChannel> cc=getOrCreateAnimChannel(chan_name);

    //     for(AnimChannel chan:cc){
    //         if(anim==null||mode==null){
    //             LOGGER.debug("Disable anim in chan {}",chan_name);
    //             chan.setAction(null);
    //             continue;
    //         }
    //         if(chan.isPaused()) chan.resume();
            
    //         if(chan.getAnimationName()==null
    //             ||(
    //                 (!chan.getAnimationName().equals(anim)||chan.getTime()>=chan.getAnimMaxTime())
    //             )
    //         ){
    //             LOGGER.debug("Set anim {} in chan {}",anim,chan_name);
    //             chan.setAction(anim);
    //             chan.setLoopMode(mode);
    //         }
    //     }
    //     return cc;
    // }




    public boolean hasAction(String anim){
        ArrayList<AnimControl> cc=new ArrayList<AnimControl>();

        this.spatial.depthFirstTraversal(s->{
            for(int i=0;i<s.getNumControls();i++){
                Control c=s.getControl(i);
                if(c instanceof AnimControl){
                    AnimControl ctr=(AnimControl)c;
                    cc.add(ctr);
                }
            }
        });

        for(AnimControl c:cc){
            if(c.getAnim(anim)!=null)return true;
        }
        return false;
    }
    
    

    // public ArrayList<AnimChannel>  stepAction(String chan_name,String anim,Float time){
    //     if(chan_name==null){
    //         String id=getAutoAnimChannelId(anim);
    //         if(id==null){
    //             LOGGER.debug("Anim not found {}",anim);
    //             return emptyList;
    //         }
    //         chan_name=id;
    //     }

    //     ArrayList<AnimChannel> cc=getOrCreateAnimChannel(chan_name);
    //     for(AnimChannel chan:cc){
    //         if(time==null||time<0){
    //             chan.setAction(null);
    //         }else       chan.stepAnimation(anim, time);
    //     }
    //     return cc;

    // }

    // public ArrayList<AnimChannel>  stepAction(String anim,float i){
    //     return stepAction(null,anim,i);
    // }

    // public ArrayList<AnimChannel>  stepActionRange(String chan_name,String anim,float i){
    //     if(chan_name==null){
    //         String id=getAutoAnimChannelId(anim);
    //         if(id==null){
    //             LOGGER.debug("Anim not found {}",anim);
    //             return emptyList;
    //         }
    //         chan_name=id;
    //     }

    //     ArrayList<AnimChannel> cc=getOrCreateAnimChannel(chan_name);
    //     for(AnimChannel chan:cc){
    //         float time=chan.getAnimationLength(anim)*i;
    //         chan.stepAnimation(anim, time);
    //     }
    //     return cc;
    // }

    // public ArrayList<AnimChannel>  stepActionRange(String anim,float i){
    //     return stepActionRange(null,anim,i);
    // }

    // public ArrayList<AnimChannel>  pauseChannel(String chan_name) {
    //     ArrayList<AnimChannel> cc=getOrCreateAnimChannel(chan_name);
    //     for(AnimChannel chan:cc){
    //         chan.pause();
    //     }
    //     return cc;
    // }
    
    // public ArrayList<AnimChannel>  resumeChannel(String chan_name) {
    //     ArrayList<AnimChannel> cc=getOrCreateAnimChannel(chan_name);
    //     for(AnimChannel chan:cc){
    //         chan.resume();
    //     }
    //     return cc;
	// }

	// public ArrayList<AnimChannel>  pauseAction(String action) {
    //     String chan_name=getAutoAnimChannelId(action);
    //     if(chan_name==null)return emptyList;
    //     ArrayList<AnimChannel> cc=getOrCreateAnimChannel(chan_name);
    //     for(AnimChannel chan:cc){
    //         chan.pause();
    //     }
    //     return cc;
    // }
    
    // public ArrayList<AnimChannel>  resumeAction(String action) {
    //     String chan_name=getAutoAnimChannelId(action);
    //     if(chan_name==null)return emptyList;
    //     ArrayList<AnimChannel> cc=getOrCreateAnimChannel(chan_name);
    //     for(AnimChannel chan:cc){
    //         chan.resume();
    //     }
    //     return cc;
	// }

}