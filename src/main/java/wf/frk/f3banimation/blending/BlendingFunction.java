package wf.frk.f3banimation.blending;

import java.util.ArrayList;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import wf.frk.f3banimation.AnimChannel;
import wf.frk.f3banimation.Animation;
import wf.frk.f3banimation.utils.TriFunction;
import wf.frk.f3banimation.utils.mutables.MutableFloat;


/**
 * FadeBlendingFunction
 */
public class BlendingFunction {
    public static TriFunction<AnimChannel,Animation,Float,Float> newSimple(Supplier<Float> blend){
        return (chan,anim,tpf)->{
            return blend.get();
        };
    }
   

    public static TriFunction<AnimChannel,Animation,Float,Float> newConditionalStep(
        TriFunction<AnimChannel,Animation,Float,Boolean> condition){
        return (chan,anim,tpf)->condition.apply(chan,anim,tpf)?1f:0f;
    }


    @Deprecated
    public static TriFunction<AnimChannel,Animation,Float,Float> newFade(
        float blendTime,
        Supplier<Float> speed
    ){
        final MutableFloat time=new MutableFloat();
        return (chan,anim,tpf)->{
            float t=time.get();
            t+=tpf*speed.get();
            time.set(t);
            float blendRange=1/blendTime;      
            float blendAmount=t*blendRange;
            if(blendAmount>1)blendAmount=1;
            return blendAmount;
        };
    }



    public static TriFunction<AnimChannel,Animation,Float,Float> newFadeIn(
        float blendTime,
        Supplier<Float> speed
    ){
        final MutableFloat time=new MutableFloat();
        return (chan,anim,tpf)->{
            float t=time.get();
            t+=tpf*speed.get();
            time.set(t);
            float blendRange=1/blendTime;      
            float blendAmount=t*blendRange;
            if(blendAmount>1)blendAmount=1;
            return blendAmount;
        };
    }

    public static TriFunction<AnimChannel,Animation,Float,Float> newFadeOut(
        float blendTime,
        Supplier<Float> speed
    ){
        final MutableFloat time=new MutableFloat();
        return (chan,anim,tpf)->{
            float t=time.get();
            t+=tpf*speed.get();
            time.set(t);
            float blendRange=1/blendTime;      
            float blendAmount=t*blendRange;
            blendAmount=1.f-blendAmount;
            if(blendAmount<0)blendAmount=0;
            return blendAmount;
        };
    }




    static class ConditionalToggleFadeFunction_Store{
        float blend=0;
        byte action=0; // 0 -> nothing, 1-> fadein, 2-> fadeout
        ArrayList<Byte> actionQueue=new ArrayList<Byte>();
        float lastTick=-1;        
        boolean lastValue=false;
        boolean fadeInValue=true;
    }

    public static TriFunction<AnimChannel,Animation,Float,Float> newToggleFade(
        float fadeInTime,
        float fadeOutTime,
        boolean initialValue,
        Supplier<Float> speed,
                TriFunction<AnimChannel,Animation,Float,Boolean> condition){
        
        final ConditionalToggleFadeFunction_Store data=new ConditionalToggleFadeFunction_Store();
        data.lastValue=initialValue;
        data.fadeInValue=!initialValue;

        final MutableFloat time=new MutableFloat();

        return (chan,anim,tpf)->{
            float t=time.get();
            t+=tpf*speed.get();
            time.set(t);

            if(data.lastTick==-1){
                data.lastTick=t;
            }

            boolean conditionValue=condition.apply(chan,anim,tpf);
            boolean lastConditionValue=data.lastValue;
            

            if(lastConditionValue!=conditionValue){
                byte action=0;
                if(conditionValue==data.fadeInValue){
                    action=1;
                }else{
                    action=2;
                }
                data.lastValue=conditionValue;
                data.actionQueue.add(action);
            }   
            float tdelta=t-data.lastTick;
            float fadeInSpeed=tdelta*(1.f/fadeInTime);
            float fadeOutSpeed=tdelta*(1.f/fadeOutTime);

            if(data.action==0&&data.actionQueue.size()>0)data.action=data.actionQueue.remove(0);
            
            if(data.action==1) {
                data.blend+=fadeInSpeed;
                if(data.blend>=1){
                    data.action=0;
                    data.blend=1;
                }
            }else if(data.action==2){
                data.blend-=fadeOutSpeed;
                if(data.blend<=0){
                    data.blend=0;
                    data.action=0;
                }
            }
            data.lastTick=t;

            return data.blend;
        };
    }


}