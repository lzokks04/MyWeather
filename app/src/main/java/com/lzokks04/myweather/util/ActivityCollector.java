package com.lzokks04.myweather.util;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**Activity控制类
 * Created by Liu on 2016/8/17.
 */
public class ActivityCollector {
    public static List<Activity> activities = new ArrayList<Activity>();

    public static void addActivity(Activity activity){
        activities.add(activity);
    }

    public static void removeActivity(Activity activity){
        activities.remove(activity);
    }

    public static void finishAll(){
        for (Activity a:activities){
            if (!a.isFinishing()){
                a.finish();
                System.exit(0);
            }
        }
    }
}
