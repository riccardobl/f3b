package wf.frk.f3banimation.utils;

public class F3bMath {
    public static float mod(float val,float min,float max){
        float range=max-min;
        float mod=(val-min)%range;
        if(mod<0)mod+=range;
        return min+mod;

    }
}