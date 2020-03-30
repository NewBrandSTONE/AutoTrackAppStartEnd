package me.young.autotrack.sdk;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Iterator;

/**
 * 用来操作私有数据
 *
 * @author O.z Young
 * @version 2020-03-30
 */
public class SensorsDataPrivate {

    private static CountDownTimer countDownTimer;
    private static WeakReference<Activity> mCurrentActivity;

    private final static int SESSION_INTERVAL_TIME = 30 * 1000;
    private static final String TAG = SensorsDataPrivate.class.getName();

    @TargetApi(14)
    public static void registerActivityLifecycleCallback(Application app) {

        countDownTimer = new CountDownTimer(SESSION_INTERVAL_TIME, 10 * 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.i(TAG, "onTick: 被调用");
            }

            @Override
            public void onFinish() {
                Log.i(TAG, "onFinish: 被调用");
                // 如果倒计时完成说明APP已经进入后台
                if (mCurrentActivity != null) {
                    trackAppEnd(mCurrentActivity.get());
                }
            }
        };

        // 初始化DataBaseHelper
        app.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                Log.i(TAG, "onActivityCreated: ");
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                Log.i(TAG, "onActivityStarted: ");
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                Log.i(TAG, "onActivityResumed: ");
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
                mCurrentActivity = new WeakReference<>(activity);
                // 开始计时
                countDownTimer.start();
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
                Log.i(TAG, "onActivityStopped: ");
            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
                Log.i(TAG, "onActivitySaveInstanceState: ");
            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                Log.i(TAG, "onActivityDestroyed: ");
            }
        });
    }

    private static void trackAppEnd(Activity activity) {
        try {
            // 保存数据库
            if (activity == null) {
                return;
            }
            JSONObject properties = new JSONObject();
            properties.put("$activity", activity.getClass().getCanonicalName());
            //properties.put("$title", getActivityTitle(activity));
            SensorsDataAPI.getInstance().track("$AppEnd", properties);
            //mDatabaseHelper.commitAppEndEventState(true);
            mCurrentActivity = null;
        } catch (Exception e) {
            Log.e(TAG, "trackAppEnd: ", e);
        }

    }

    public static void registerActivityStateObserver(Application app) {

    }

    public static void mergeJsonObject(JSONObject source, JSONObject dest) throws JSONException {
        Iterator<String> keys = source.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            Object value = source.get(key);
            dest.put(key, value);
        }
    }

}
