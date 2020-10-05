package wf.frk.f3banimation.blending;

import java.util.function.Supplier;

import wf.frk.f3banimation.AnimChannel;
import wf.frk.f3banimation.Animation;
import wf.frk.f3banimation.utils.TriFunction;
import wf.frk.f3banimation.utils.mutables.MutableBoolean;
import wf.frk.f3banimation.utils.mutables.MutableFloat;



/**
 * TimeFunction
 */
public class TimeFunction {
    public static TriFunction<AnimChannel,Animation,Float,Float> newClamped(Supplier<Float> speed){
        final MutableFloat time=new MutableFloat();
        return (chan,anim,tpf)->{
            float t=time.get()+(tpf*speed.get());
            // if(t>=chan.getAnimMaxTime())t=chan.getAnimMaxTime();
            time.set(t);
            anim.setLoop(false);
            return time.get();
        };
    }
    public static TriFunction<AnimChannel,Animation,Float,Float> newLooping(Supplier<Float> speed){
        final MutableFloat time=new MutableFloat();
        return (chan,anim,tpf)->{
            float t=time.get()+(tpf*speed.get());
            // if(t>=chan.getAnimMaxTime())t%=chan.getAnimMaxTime();
            time.set(t);
            anim.setLoop(true);
            return t;
        };
    }


    

    public static TriFunction<AnimChannel,Animation,Float,Float> newSteppingFunction(
        TriFunction<AnimChannel,Animation,Float,Float> stepSupplier
    ){        
        return (chan,anim,tpf)->{
            float step=stepSupplier.apply(chan,anim,tpf);
            if(step<0)step=0;
            else if(step>chan.getAnimMaxTime())step=chan.getAnimMaxTime();
            anim.setLoop(false);
            return step;
        };
    }
   
    public static TriFunction<AnimChannel,Animation,Float,Float> newSteppingRangeFunction(
        TriFunction<AnimChannel,Animation,Float,Float> stepRangeSupplier
    ){        
        return (chan,anim,tpf)->{
            float step=stepRangeSupplier.apply(chan,anim,tpf);
            if(step<0)step=0;
            else if(step>1)step=1;
            float maxTime=chan.getAnimMaxTime();
            anim.setLoop(false);
            return step*maxTime;
        };
    }
   





    public static TriFunction<AnimChannel,Animation,Float,Float> newPlayAndBack(Supplier<Float> speed){
        final MutableFloat time=new MutableFloat();
        final MutableBoolean direction=new MutableBoolean(true);
        return (chan,anim,tpf)->{
            float t;
            if(direction.get()){
                 t=time.get()+(tpf*speed.get());
                if(t>=chan.getAnimMaxTime()){
                    direction.set(false);
                    t=chan.getAnimMaxTime();
                }
            }else{
                t=time.get()-(tpf*speed.get());
                if(t<=0){
                    t=0;
                }
            }
            time.set(t);

            anim.setLoop(false);
            return time.get();
        };
    }



    // public static enum TogglePlayAndBackStatus{
    //     FORWARD,
    //     BACKWARD,
    //     NOPLAY
    // }
    // public static TriFunction<AnimChannel,Animation,Float,Float> newTogglePlayAndBack(
        
    //     TriFunction<AnimChannel,Animation,Float,TogglePlayAndBackStatus> toggler,
    //     Supplier<Float> speed
    // ){
    //     final MutableFloat time=new MutableFloat();
    //     // final Pointer<TogglePlayAndBackStatus> direction=new Pointer<TogglePlayAndBackStatus>(TogglePlayAndBackStatus.NOPLAY);
    //     // final Pointer<TogglePlayAndBackStatus> lastValue=new Pointer<TogglePlayAndBackStatus>(null);
        
    //     return (chan,anim,tpf)->{
        
    //         TogglePlayAndBackStatus currentValue=toggler.apply(chan,anim,tpf);
    //         if(currentValue==TogglePlayAndBackStatus.NOPLAY)return time.get();

    //         float t;
    //         if(currentValue==TogglePlayAndBackStatus.FORWARD){
    //              t=time.get()+(tpf*speed.get());
    //             if(t>=chan.getAnimMaxTime()){
    //                 t=chan.getAnimMaxTime();
    //             }
    //         }else{
    //             t=time.get()-(tpf*speed.get());
    //             if(t<=0){
    //                 t=0;
    //             }
    //         }
    //         time.set(t);

    //         anim.setLoop(false);
    //         return time.get();
    //     };
    // }
}