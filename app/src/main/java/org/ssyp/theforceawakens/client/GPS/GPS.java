package org.ssyp.theforceawakens.client.GPS;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

public class GPS {
    private static final double S_LON = 82.728f, S_LAT = 54.614f;

    private GPSCallback callback;

    public GPS(Activity from, GPSCallback callback) {
        this.callback = callback;

        LocationManager lManager = (LocationManager) from.getSystemService(Context.LOCATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            from.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        boolean ready = false;
        while (!ready) { // FIXME: wtf is going here? does this execute more than once?
            try {
                lManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        GPS.this.callback.signalLocation(location.getLatitude(), location.getLongitude());
                    }

                    @Override
                    public void onStatusChanged(String s, int i, Bundle bundle) { // FIXME: handle
                    }

                    @Override
                    public void onProviderEnabled(String s) { // FIXME: handle
                    }

                    @Override
                    public void onProviderDisabled(String s) { // FIXME: handle
                    }

                });
                ready = true;
            } catch (SecurityException e) {
                Log.e("Tag", e.getMessage()); // FIXME: handle (not like this)
            }
        }
    }

    public static float fromLatitude(double coordinate, double scaling) {
        return (float) ((coordinate - S_LAT) * 300000.0 * scaling);
    }

    public static float fromLongitude(double coordinate, double scaling) {
        return (float) ((coordinate - S_LON) * 300000.0 * scaling);
    }
}
