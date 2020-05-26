package com.zpan.lifecycle;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 维护一个 Activity 堆栈
 *
 * @author zpan
 */
final public class ActivityManager implements Application.ActivityLifecycleCallbacks {

    private static ActivityManager sInstance;

    /**
     * 维护Activity 的list
     */
    private static List<Activity> mActivitys = Collections.synchronizedList(new LinkedList<Activity>());

    private ActivityManager() {
    }

    public static synchronized ActivityManager getInstance() {
        if (sInstance == null) {
            sInstance = new ActivityManager();
        }
        return sInstance;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        //监听到 Activity创建事件 将该 Activity 加入list
        pushActivity(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Log.e("zpan", "==onActivityStarted====" + activity);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Log.e("zpan", "==onActivityResumed====" + activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if (null == mActivitys && mActivitys.isEmpty()) {
            return;
        }
        if (mActivitys.contains(activity)) {
            //监听到 Activity销毁事件 将该Activity 从list中移除
            popActivity(activity);
        }
    }

    /**
     * 目标Activity是否在栈内
     */
    public boolean haveActivity(Class<?> activityClass) {
        for (Activity a : mActivitys) {
            if (a.getClass() == activityClass) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param activity 作用说明 ：添加一个activity到管理里
     */
    private void pushActivity(Activity activity) {
        mActivitys.add(activity);
    }

    /**
     * @param activity 作用说明 ：删除一个activity在管理里
     */
    private void popActivity(Activity activity) {
        mActivitys.remove(activity);
    }

    public int getActivitysSize() {
        return mActivitys.size();
    }

    /**
     * get current Activity 获取当前Activity（栈中最后一个压入的）
     */
    public Activity currentActivity() {
        if (mActivitys == null || mActivitys.isEmpty()) {
            return null;
        }
        return mActivitys.get(mActivitys.size() - 1);
    }

    /**
     * 结束当前Activity（栈中最后一个压入的）
     */
    public void finishCurrentActivity() {
        if (mActivitys == null || mActivitys.isEmpty()) {
            return;
        }
        Activity activity = mActivitys.get(mActivitys.size() - 1);
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity
     */
    private void finishActivity(Activity activity) {
        if (mActivitys == null || mActivitys.isEmpty()) {
            return;
        }
        if (activity != null) {
            mActivitys.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        if (mActivitys == null || mActivitys.isEmpty()) {
            return;
        }
        for (Activity activity : mActivitys) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 结束所有除Login之外的其他Act
     */
    public static void finishAllActivityWithoutTargetAct(Class<?> cls) {
        if (mActivitys == null) {
            return;
        }
        for (Activity activity : mActivitys) {
            if (activity.getClass().equals(cls)) {
                continue;
            }
            if (activity == null || activity.isDestroyed() || activity.isFinishing()) {
                continue;
            }
            activity.finish();
        }
    }

    /**
     * 按照指定类名找到activity
     */
    public Activity findActivity(Class<?> cls) {
        Activity targetActivity = null;
        if (mActivitys != null) {
            for (Activity activity : mActivitys) {
                if (activity.getClass().equals(cls)) {
                    targetActivity = activity;
                    break;
                }
            }
        }
        return targetActivity;
    }

    /**
     * @return 作用说明 ：获取当前最顶部activity的实例
     */
    public Activity getTopActivity() {
        Activity baseActivity;
        synchronized (this) {
            final int size = mActivitys.size() - 1;
            if (size < 0) {
                return null;
            }
            baseActivity = mActivitys.get(size);
        }
        return baseActivity;
    }

    /**
     * @return 作用说明 ：获取当前最顶部的acitivity 名字
     */
    public String getTopActivityName() {
        Activity baseActivity;
        synchronized (this) {
            final int size = mActivitys.size() - 1;
            if (size < 0) {
                return null;
            }
            baseActivity = mActivitys.get(size);
        }
        return baseActivity.getClass().getName();
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        if (mActivitys == null) {
            return;
        }
        for (Activity activity : mActivitys) {
            activity.finish();
        }
        mActivitys.clear();
    }

    /**
     * 退出应用程序
     */
    public void appExit() {
        try {
            finishAllActivity();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
