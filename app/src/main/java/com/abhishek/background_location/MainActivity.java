package com.abhishek.background_location;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    MyLocationService myLocationService = new MyLocationService();

    static MainActivity instance;
    LocationRequest locationRequest;
    private TextView txtLocation;
    String location = "";

    public Button send;
    public static final String URL ="https://1234image.000webhostapp.com/pictures";


    FusedLocationProviderClient fusedLocationProviderClient;

    public static MainActivity getInstance()
    {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtLocation = findViewById(R.id.txt_Location);

        send = findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 sendData();

            }
        });

        instance = this;

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response)
                    {

                        updateLocation();


                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response)
                    {

                        Toast.makeText(MainActivity.this,"Location Declined",Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

            }
        }, 1000,190000);
    }




    public void updateLocation()
    {
        buildLocationRequest();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {


            return;
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest,getPendingIntent());




    }

    private PendingIntent getPendingIntent()
    {
        Intent intent = new Intent(this,MyLocationService.class);
        intent.setAction(MyLocationService.ACTION_PROCESS_UPDATE);
        return PendingIntent.getBroadcast(this,0,intent, PendingIntent.FLAG_UPDATE_CURRENT);

    }

    private void buildLocationRequest()
    {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(6000);
        locationRequest.setFastestInterval(6000);
        //locationRequest.setSmallestDisplacement(10f);

    }

    public void updateTextView(final String value)
    {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

            txtLocation.setText(value);

            location = txtLocation.getText().toString();



        }
        });
    }




    public void sendData()
    {

        new dataToSever(location).execute();


    }




    private class dataToSever extends AsyncTask<Void, Void, Void>
    {
        ProgressDialog pd = new ProgressDialog(MainActivity.this);


        protected void onPreExecute() {
            super.onPreExecute();
            try {
                Thread.sleep(10000);
                pd.setMessage("sending data");
                pd.show();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }

        String location;

        public dataToSever(String location)
            {
                this.location = location;
                System.out.println("This is in data to server Constructor"+location);
            }


        @Override
        protected Void doInBackground(Void... voids) {


            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("location", location));


            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(URL);
            HttpParams httpParams = new BasicHttpParams();


            httpClient = new DefaultHttpClient(httpParams);

            try
            {
                httpPost.setEntity(new UrlEncodedFormEntity(dataToSend));

                HttpResponse response;
                response = httpClient.execute(httpPost);
                StatusLine statusLine = response.getStatusLine();

                if (statusLine.getStatusCode() == HttpStatus.SC_OK)
                {
                    //Toast.makeText(MainActivity.this,"OK FROM SERVCER",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());

                }

            }
            catch (Exception e)
            {
                e.printStackTrace();

            }



//            HttpParams httpRequestParams = getHttpRequestParams();
//            try
//            {
//                HttpClient client = new DefaultHttpClient(httpRequestParams);
//                HttpPost post = new HttpPost(URL);
//
//                post.setEntity(new UrlEncodedFormEntity(dataToSend));
//
//                client.execute(post);
//
//                System.out.println("Data Sent to server");
//
//            }
//            catch (Exception e)
//            {
//
//            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pd.hide();
            pd.dismiss();

            Toast.makeText(MainActivity.this,"Data Sent to server is "+location,Toast.LENGTH_SHORT).show();

        }
    }


    private HttpParams getHttpRequestParams()
    {
        HttpParams httpRequestParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpRequestParams, 1000 * 60);
        HttpConnectionParams.setConnectionTimeout(httpRequestParams, 1000 * 60);
        return httpRequestParams;


    }

    @Override
    protected void onDestroy() {

        unregisterReceiver(myLocationService);
        finishAffinity();

        super.onDestroy();
    }
}
