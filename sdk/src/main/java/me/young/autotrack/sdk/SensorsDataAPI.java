package me.young.autotrack.sdk;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据收集API
 *
 * @author O.z Young
 * @version 2020-03-30
 */
public class SensorsDataAPI {

    private static SensorsDataAPI INSTANCE;
    private static Map<String, Object> mDeviceInfo;
    private static String mDeviceId;

    private static final Object mLock = new Object();
    private final String TAG = getClass().getName();

    static {
        mDeviceInfo = new HashMap<>();
        mDeviceInfo.put("device_name", "test_device_name");
        mDeviceInfo.put("device_version", "test_device_version");

        mDeviceId = "test_device_id";
    }

    public static SensorsDataAPI init(Application app) {
        synchronized (mLock) {
            if (INSTANCE == null) {
                INSTANCE = new SensorsDataAPI(app);
            }
            return INSTANCE;
        }
    }

    private SensorsDataAPI(Application app) {
        // 注册Lifecycle
        SensorsDataPrivate.registerActivityLifecycleCallback(app);
        // 注册ContentProviderObserver
        SensorsDataPrivate.registerActivityStateObserver(app);
    }

    public static SensorsDataAPI getInstance() {
        return INSTANCE;
    }

    public void track(@NonNull String eventName, @Nullable JSONObject properties) {
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("event", eventName);
            jsonObject.put("device_id", mDeviceId);

            JSONObject sendProperties = new JSONObject(mDeviceInfo);

            if (properties != null) {
                SensorsDataPrivate.mergeJsonObject(properties, sendProperties);
            }

            jsonObject.put("properties", sendProperties);
            jsonObject.put("time", System.currentTimeMillis());

            Log.i(TAG, "track: " + jsonObject.toString());
        } catch (Exception e) {
            Log.e(TAG, "track: ", e);
        }
    }

}
