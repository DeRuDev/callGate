package com.example.android.openGate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Marco on 26/03/18.
 */

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    public static final String TAG = GeofenceBroadcastReceiver.class.getSimpleName();

    /**
     * The below handles the Broadcast Message sent when the Geofence Transition is triggered
     * since this is running on the Main Thread I'll have to start an AsyncTask,
     * for anything that takes longer than 5 seconds to run.
     *
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG,"onReceive Called");

    }
}
