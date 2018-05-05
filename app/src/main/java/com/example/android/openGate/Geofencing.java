package com.example.android.openGate;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.openGate.GeofenceBroadcastReceiver.TAG;
import static com.google.android.gms.location.Geofence.NEVER_EXPIRE;

/**
 * Created by Marco on 26/03/18.
 */

public class Geofencing implements ResultCallback {
    private static final float GEOFENCE_RADIUS = 5000;
   //  private static final long GEOFENCE_TIMEOUT = 24 * 60 * 60 * 1000; // 24 hours

    private GoogleApiClient mGoogleApiClient;
    private Context mContext;
    private PendingIntent mGeofencePendingIntent;
    private List<Geofence> mGeofenceList;


    public Geofencing(Context context, GoogleApiClient googleApiClient ) {
        mGoogleApiClient = googleApiClient;
        mContext = context;
        mGeofencePendingIntent = null;
        mGeofenceList = new ArrayList<>();
    }

    /** The below method registers the list of Geofences specified in mGeofenceList with Google Place Services
     * uses {@code #mGoogleApiClient} to connect to Google Place Services
     * uses {@link #getGeofencingRequest} to get the link of Geofences to be registered
     * uses {@Link #getGeofencePendingIntent} to get the PendingIntent to launch the IntentService,
     *      when the Geofence is triggered
     * triggers {@Link #onResult} when the geofences have been registered succesfully
     */
    public void registerAllGeofences(){
     //Check that the API client is connected and the list contains Geofences
        if(mGoogleApiClient == null || !mGoogleApiClient.isConnected()
                || mGeofenceList == null || mGeofenceList.size() == 0){
            return;
        }

        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    getGeofencingRequest(),
                    getmGeofencePendingIntent()
            ).setResultCallback(this);
        } catch (SecurityException securityException) {
            //Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            Log.e(TAG, securityException.getMessage());
        }
    }

    /**
     * The below method unregisters all the Geofences created by this app from Google Place Services
     * uses {@code #mGoogleApiClient} to connect to Google Place Servics
     * uses {@link #getmGeofencePendingIntent} to get the pending intent passed when registering the Geofences in the first place
     * Triggers {@link #onResult} when the Geofences have been unregistered succesfully
     */
    public void unregisterAllGeofences(){
        if(mGoogleApiClient == null || !mGoogleApiClient.isConnected()){
            return;
        } try {
            LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient,
                    //This is the same PendingIntent that was used in #registerAllGeofences)
                    getmGeofencePendingIntent()).setResultCallback(this);
        } catch (SecurityException securityException) {
            //Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            Log.e(TAG, securityException.getMessage());
        }
    }



    /** The below method updates the local Arraylist of Geofences using data from the passed in list
     *  It uses the PlaceID defined by the API as the Geofence ObjectID.
     *
     * @param places The PlaceBuffer result of the getPlaceById call
     */
    public void updateGeofencesList(PlaceBuffer places){
        mGeofenceList = new ArrayList<>();
        if(places == null || places.getCount() == 0) return;
        for (Place place : places) {

        //Read the place information from the database cursor.
            String placeUID = place.getId();
            Double placeLat = place.getLatLng().latitude;
            Double placeLng = place.getLatLng().longitude;

            // Build a Geofence Object
//TODO (1) Set the duration to 24h then set a job Scheduler to Read Geofences everyday, decide type of transition needed
            Geofence geofence = new Geofence.Builder()
                    .setRequestId(placeUID)
                    .setLoiteringDelay(50)
                    .setExpirationDuration(NEVER_EXPIRE)
                    .setCircularRegion(placeLat, placeLng, GEOFENCE_RADIUS)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER| Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build();

        //Now that I've built the geofence it can be added to the ArrayList
            mGeofenceList.add(geofence);
        }
    }
    /** Below I'll create a GeofencingRequest Object using the mGeofenceList ArrayList of Geofences
     * used by {@code #registerGeofences}
     *
     * @return the GeofencingRequest Object
     */
    private GeofencingRequest getGeofencingRequest(){
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
            builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
            builder.addGeofences(mGeofenceList); //Adding Geofences to my A
                return builder.build();

// The INITIAL_TRIGGER defines what happens if the device was already inside any of the Geofences that I'm about to register
// If the device is already inside the Geofences at the time of registering, then trigger an entry transition event immediately.
// #DWELL triggers the transition event only if the device has been inside for some duration of time.

    }
    /** Creating a PendingIntent Object using the GeofenceTransictionIntentService class
     *  used by {@code #registerGeofences}
     *
     * @return the PendingIntent Object
     */

    private PendingIntent getmGeofencePendingIntent(){
        //Reuse the PendingIntent if I already have it
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent (mContext, GeofenceBroadcastReceiver.class); //Creating Intent for GBR.class
        mGeofencePendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, //Creating the PendingIntent
                                                            PendingIntent.FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }

    @Override
    public void onResult(@NonNull Result result) {
        Log.e(TAG,String.format("Error Adding/Removing Geofences : %s",
                result.getStatus().toString()));

    }
}
