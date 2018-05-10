package com.example.android.openGate;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;
import java.util.UUID;

import static org.altbeacon.beacon.AltBeaconParser.TAG;

// DEVO CAPIRE COME LANCIARE QUESTA CLASSE e FARLA GIRARE COME SERVIZIO UNA VOLTA CHE IL POSTO è STATO AGGIUNTO.

public class MonitoringActivity extends Activity implements BeaconConsumer {
    private static final String UNIQUE_ID = "Package_Name_With_Identifier";
    private static final String PROXIMITY_UUID = "5FF56717-97FE-4B88-8DFA-9B201000673C";
    //Beacon Variables
    private BeaconManager beaconManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Creating the Beacon Identifier and a Region
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));//Descrive il formato dei bite che arrivano.
        beaconManager.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                Log.i(TAG, "I Just Saw The Beacon!! :)");
                try {
                    beaconManager.startRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    Log.e(TAG, "Exception Found:", e);
                }
            }

            @Override
            public void didExitRegion(Region region) {
                Log.i(TAG, "The Beacon is gone :(");
                try {
                    beaconManager.stopRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    Log.e(TAG, "Exception Found:", e);
                }
            }

            @Override
            public void didDetermineStateForRegion(int i, Region region) {
                Log.i(TAG, "The Status has Changed:");
            }
        });
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if(beacons.size() > 0){
                    Log.i(TAG,"The Beacon is about" + beacons.iterator().next().getDistance()+" meters away");
                }
            }
        });
        try {
            Identifier identifier = Identifier.fromUuid(UUID.fromString(PROXIMITY_UUID));
            beaconManager.startMonitoringBeaconsInRegion(new Region("myBeacon", identifier, null, null));
        } catch (RemoteException e) {
        }
    }
}
