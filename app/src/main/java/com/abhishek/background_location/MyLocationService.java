package com.abhishek.background_location;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.IBinder;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

public class MyLocationService extends BroadcastReceiver {




    BroadcastReceiver b = new BroadcastReceiver() {


        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent != null)
            {
                final String action = intent.getAction();

                if (ACTION_PROCESS_UPDATE.equals(action))
                {
                    LocationResult result = LocationResult.extractResult(intent);

                    if (result !=null)
                    {
                        Location location = result.getLastLocation();
                        String location_string = new StringBuilder("Latitude is " + location.getLatitude()+"\n")

                                .append("Longitude is "+location.getLongitude())
                                .toString();


                        try {

                            MainActivity.getInstance().updateTextView(location_string);
                            MainActivity.getInstance().sendData();




                            Toast.makeText(context, location_string, Toast.LENGTH_LONG).show();




                        }
                        catch (Exception ex) {
                            Toast.makeText(context, location_string, Toast.LENGTH_LONG).show();


                        }

                    }

                }
            }


        }
    };



    public static final String ACTION_PROCESS_UPDATE = "A";



    @Override
    public void onReceive(Context context, Intent intent)
    {
        b.onReceive(context,intent);

    }










}
