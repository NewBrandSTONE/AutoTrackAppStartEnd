package me.young.autotrack.startend;

import android.app.Application;

import me.young.autotrack.sdk.SensorsDataAPI;

/**
 * App
 *
 * @author O.z Young
 * @version 2020-03-30
 */
public class AutoTrackApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SensorsDataAPI.init(this);
    }
}
