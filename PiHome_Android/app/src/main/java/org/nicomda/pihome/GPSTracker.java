package org.nicomda.pihome;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by nicomda on 10/06/14.
 */
public class GPSTracker extends Service implements LocationListener {

    private final Context mContext;
    boolean isGPSEnabled=false;
    boolean isNetworkEnabled=false;
    boolean canGetLocation=false;
    Location location;
    double latitude;
    double longitude;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES=10;
    private static final long MIN_TIME_BW_UPDATES=1000*60*1;
    protected LocationManager locationManager;
    public GPSTracker(Context context){
        this.mContext=context;
        getLocation();
    }

    public Location getLocation(){
        try{
            locationManager=(LocationManager) mContext.getSystemService(LOCATION_SERVICE);
            isGPSEnabled=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled=locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if(!isGPSEnabled && !isNetworkEnabled) Toast.makeText(getApplicationContext(),"No hay proveedores de localización activados",Toast.LENGTH_SHORT).show();
            else{
                this.canGetLocation=true;
                if(isNetworkEnabled){
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES,this);
                    Log.d("Network","Network");
                    if(locationManager!=null){
                        location=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if(location!=null){
                            latitude=location.getLatitude();
                            longitude=location.getLongitude();
                        }
                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return location;
    }
    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
    public double getLatitude(){
        if(location!=null) latitude=location.getLatitude();
        return latitude;
    }
    public double getLongitude(){
        if(location!=null) longitude=location.getLongitude();
        return longitude;
    }
    public boolean canGetLocation(){
        return this.canGetLocation;
    }
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Atención");
        alertDialog.setMessage("Sensor GPS no activado. Quieres ir a ajustes para activarlo?");
        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialog.setPositiveButton("Ajustes GPS", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });
        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        alertDialog.show();
    }
    public void stopUsingGPS(){
        if (locationManager!=null)locationManager.removeUpdates(GPSTracker.this);
    }
}
