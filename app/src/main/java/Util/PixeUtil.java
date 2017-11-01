package Util;

import android.content.Context;
import android.content.res.Resources;

/**
 * Created by Administrator on 2017/10/30.
 */

public class PixeUtil {
    private static Context mContext;

    public static void initContext(Context context){
        mContext =context;
    }
    public  static int dp2px(float value){
        final float scale = mContext.getResources().getDisplayMetrics().densityDpi;
        return (int)(value * (scale/160)*0.5f);
    }

    //dpz
    public  static int dp2px(float value,Context context){
        final float scale = context.getResources().getDisplayMetrics().densityDpi;
        return (int)(value * (scale/160)*0.5f);
    }
    //sp转px
    public static int sp2px(float value){
        Resources r;
        if ( mContext==null ){
            r = Resources.getSystem();
        }else {
            r = mContext.getResources();
        }
        float spvalue = value*r.getDisplayMetrics().scaledDensity;
        return (int)(spvalue+0.5f);
    }
    //px转sp
    public static  int px2sp(float value,Context context){
        final  float scale =context.getResources().getDisplayMetrics().scaledDensity;
        return (int)(value/scale+0.5f);
    }
}
