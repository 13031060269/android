package com.hfax.ucard.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.hfax.lib.BaseApplication;

/**
 * 定位服务
 *
 * @author SongGuangYao
 * @date 2018/6/21
 */

public class LocationUtils {

    public double latitude;
    public double longitude;
    public boolean isGPSable = true;//gps是否开启
    private LocationManager locationManager;
    private Context context = BaseApplication.getContext();

    /**
     * 获取经纬度
     */
    public LocationUtils() {
        String serviceString = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) context.getSystemService(serviceString);
        requestLocation();
    }


    /**
     * 请求定位
     */
    public void requestLocation() {
        try {
            Location gpsLocation = getGPSLocation();
            if (gpsLocation != null) {
                getInfo(gpsLocation);
            } else {
                Location netLocation = getNetLocation();
                getInfo(netLocation);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private Location getGPSLocation() {
        String gpsProvider = LocationManager.GPS_PROVIDER;
        isGPSable = locationManager.isProviderEnabled(gpsProvider);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        locationManager.requestLocationUpdates(gpsProvider, 500, 2000, myListener);
        Location location = locationManager.getLastKnownLocation(gpsProvider);
        return location;
    }

    private Location getNetLocation() {
        //网络定位
        String netProvider = LocationManager.NETWORK_PROVIDER;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        locationManager.requestLocationUpdates(netProvider, 500, 2000, myListener);
        Location location = locationManager.getLastKnownLocation(netProvider);
        return location;
    }

    private void getInfo(Location location) {
        if (location != null && location.getLongitude() != 0.0 && location.getLatitude() != 0.0) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            return;
        }
    }

    /**
     * 移除监听
     */
    public void removeListener() {
        if (locationManager != null) {
            locationManager.removeUpdates(myListener);
            locationManager = null;
        }
    }


    private LocationListener myListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            getInfo(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
}
