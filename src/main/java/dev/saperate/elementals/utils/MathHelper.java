package dev.saperate.elementals.utils;

public abstract class MathHelper {



    public static float clamp(float u, float min, float max){
        if(u < min){
            u = min;
        }else if(u > max){
            u = max;
        }
        return u;
    }

    public static float returnGreaterAbs(float u, float v){
        if(Math.abs(u) > Math.abs(v)){
            return u;
        } else {
            return v;
        }
    }

    public static float returnSmallerAbs(float u, float v){
        if(Math.abs(u) < Math.abs(v)){
            return u;
        } else {
            return v;
        }
    }

    public static int returnGreaterAbs(int u, int v){
        if(Math.abs(u) > Math.abs(v)){
            return u;
        } else {
            return v;
        }
    }

    public static int returnSmallerAbs(int u, int v){
        if(Math.abs(u) < Math.abs(v)){
            return u;
        } else {
            return v;
        }
    }

    //Easing functions

    public static float linear(float first, float last, float progress){
        //I tried really hard to use Math.clamp, but for some godforsaken reason that shit didn't work
        return first + (last - first) * MathHelper.clamp(progress, 0f, 1f);
    }

    public static float flip(float value){
        return 1 - value;
    }

    //In
    public static float easeIn(float value){
        return value * value;
    };

    public static float easeInCubed(float value){
        return value * value * value;
    };

    public static float easeInFifth(float value){
        return value * value * value * value * value;
    };

    //Out
    public static float easeOut(float value){
        return flip(easeIn(flip(value)));
    };

    public static float easeOutCubed(float value){
        return flip(easeInCubed(flip(value)));
    };

    public static float easeOutFifth(float value){
        return flip(easeInFifth(flip(value)));
    };

    //In Out
    public static float easeInOut(float value){
        return linear(easeIn(value), easeOut(value), value);
    };

    public static float easeInOutCubed(float value){
        return linear(easeInCubed(value), easeOutCubed(value), value);
    };

    public static float easeInOutFifth(float value){
        return linear(easeInFifth(value), easeOutFifth(value), value);
    };

}
