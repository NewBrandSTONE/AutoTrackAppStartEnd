package me.young.autotrack.sdk.handler;

import android.app.Activity;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;

import me.young.autotrack.sdk.SensorsDataAPI;

/**
 * <一句话简述功能>
 * <功能详细描述>
 *
 * @author O.z Young
 * @version 2020-04-11
 */
public class TouchEventHandler {

    private static final String TAG = TouchEventHandler.class.getCanonicalName();

    private TouchEventHandler() {

    }

    public static void dispatchTouchEvent(Activity activity, MotionEvent motionEvent) {
        // 如果是按起类型
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            // 通过activity找到被点击的view
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            ArrayList<View> targetViews = getTargetViews(decorView, motionEvent);
            if (targetViews == null) {
                return;
            }
            for (View view : targetViews) {
                if (view == null) {
                    continue;
                }
                if (view instanceof AdapterView) {
                    // todo
                } else {
                    SensorsDataAPI.trackViewOnClick(view);
                }
            }
        }
    }

    /**
     * 要获取的View必须满足三个条件
     * 1.可见
     * 2.在范围内
     * 3.可以被点击的
     *
     * @param parent      根视图
     * @param motionEvent 点击事件
     * @return 获取的Views
     */
    private static ArrayList<View> getTargetViews(View parent, MotionEvent motionEvent) {
        ArrayList<View> targetViews = new ArrayList<>();
        try {
            // 判断这个View是否处于可见状态，判断这个View是否处于被点击的范围
            if (isVisible(parent) && isContain(parent, motionEvent)) {
                // 判断是否为AdapterView的子类
                if (parent instanceof AdapterView) {
                    targetViews.add(parent);
                    getTargetViewsInGroup((ViewGroup) parent, motionEvent, targetViews);
                } else if (parent.isClickable()) {
                    targetViews.add(parent);
                } else if (parent instanceof ViewGroup) {
                    getTargetViewsInGroup((ViewGroup) parent, motionEvent, targetViews);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "getTargetViews: ", e);
        }
        return targetViews;
    }

    private static void getTargetViewsInGroup(ViewGroup parent
            , MotionEvent motionEvent, ArrayList<View> hitViews) {
        try {
            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);
                ArrayList<View> hitChild = getTargetViews(child, motionEvent);
                if (!hitChild.isEmpty()) {
                    hitViews.addAll(hitChild);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "getTargetViewsInGroup: ", e);
        }
    }

    private static boolean isVisible(View parent) {
        return parent.getVisibility() == View.VISIBLE;
    }

    private static boolean isContain(View parent, MotionEvent motionEvent) {
        float rawX = motionEvent.getRawX();
        float rawY = motionEvent.getRawY();
        Rect outRect = new Rect();
        parent.getGlobalVisibleRect(outRect);
        return outRect.contains((int) rawX, (int) rawY);
    }

}
