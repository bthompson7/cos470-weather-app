package com.example.bthom.weatherapp;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ActionMenuItem;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bthom.weatherapp.Common.Common;
import com.example.bthom.weatherapp.Helper.Helper;
import com.example.bthom.weatherapp.Model.Main;
import com.example.bthom.weatherapp.Model.OpenWeatherMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.List;


public class MainActivity extends AppCompatActivity{

    TextView txtCity, txtLastUpdate, txtDesc,
            txtHumidity, txtTime,txtMinMaxTemp,txtCelsius,legalStart,legalStop;
    ProgressBar progressBar;
    ImageView imageView;
    LocationManager locationManager;
    LocationListener locationListener = new MyLocationListener();
    String provider;
    static double lat, lon;
    double sunRise;
    double sunSet;
    OpenWeatherMap openWeatherMap = new OpenWeatherMap();
    int MY_PERMISSION = 0;
    Location location;
    String loc;
    String temp;
    String desc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //our data
        txtCity = findViewById(R.id.txtCity);
        txtLastUpdate = findViewById(R.id.txtLastUpdate);
        txtDesc = findViewById(R.id.txtDesc);
        txtHumidity = findViewById(R.id.txtHumidity);
        txtTime = findViewById(R.id.txtTime);
        txtCelsius = findViewById(R.id.txtCelsius);
        txtMinMaxTemp = findViewById(R.id.txtMinMaxTemp);
        imageView = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.dataLoading);
        legalStart = findViewById(R.id.legalStart);
        legalStop = findViewById(R.id.legalStop);

        progressBar.setVisibility(View.VISIBLE);

        //permission check
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE


            }, MY_PERMISSION);
        }

        //This is to fix the 1st time run crash that was happening
        //it waits until the user has accepted or denied permission
        while (true) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.i("Info", "Fine location granted");
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.i("Info", "Write external storage permission granted");
                break;
            }
        }

        //get coordinates
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);
        location =  locationManager.getLastKnownLocation(provider);
        System.out.println("Location = " + location);
        if (location == null) {
            Log.i("Info", "No location found, getting current location");
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }




    }



    @Override
    protected void onPause() {
        super.onPause();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, MY_PERMISSION);
        }
        locationManager.removeUpdates(locationListener);


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, MY_PERMISSION);
        }

        if(provider != null) {
           locationManager.requestLocationUpdates(provider, 400, 1, locationListener);
        }
    }

    //gets the data from openweathermap
   public class GetWeather extends AsyncTask<String,Void,String>{
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            Log.i("TAG","Starting...");
        }
        @Override
        protected String doInBackground(String... params) {

            Log.i("TAG","Gather weather for location...");
            String stream;
            String urlString = params[0];

            Helper http = new Helper();
            stream = http.getHTTPData(urlString);
            System.out.println(stream);
            return stream;
        }

        @Override
        public void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.contains("Error: Not found city")){
                progressBar.setVisibility(View.INVISIBLE);
                return;
            }
            Gson gson = new Gson();
            Type mType = new TypeToken<OpenWeatherMap>(){}.getType();
            openWeatherMap = gson.fromJson(s,mType);

            sunRise = openWeatherMap.getSys().getSunrise();
            sunSet = openWeatherMap.getSys().getSunset();

            //display the data in the current location
            System.out.println(txtCity);
            txtCity.setText(String.format("%s,%s",openWeatherMap.getName(),openWeatherMap.getSys().getCountry()));
            txtLastUpdate.setText(String.format("Last Updated: %s", Common.getDateAndTimeNow()));
            txtDesc.setText(String.format("%s",openWeatherMap.getWeather().get(0).getDescription()));
            txtHumidity.setText(String.format("Humidity: %d%%",openWeatherMap.getMain().getHumidity()));
            txtTime.setText(String.format("%s/%s",Common.unixTimeStampToDateTime(openWeatherMap.getSys().getSunrise()),Common.unixTimeStampToDateTime(openWeatherMap.getSys().getSunset())));
            System.out.println("txtCelsius =" + txtCelsius);
            txtCelsius.setText(String.format("%.0f °F",openWeatherMap.getMain().getTemp()));
            txtMinMaxTemp.setText(String.format("%.0f/%.0f °F",openWeatherMap.getMain().getTemp_max(),openWeatherMap.getMain().getTemp_min()));
            double subtractTime = 38*60;
            double addTime = 25*60;
            double start = sunRise;
            double stop = sunSet;
            start = start - subtractTime;
            stop = stop + addTime;
            legalStart.setText(String.format("Legal Start: %s",Common.unixTimeStampToDateTime(start)));
            legalStop.setText(String.format("Legal Stop: %s",Common.unixTimeStampToDateTime(stop)));
            Picasso.with(MainActivity.this)
                    .load(Common.getImage(openWeatherMap.getWeather().get(0).getIcon()))
                    .into(imageView);

            progressBar.setVisibility(View.INVISIBLE);
            Log.i("TAG","Done");

             //display a notification after getting the data
             loc = txtCity.getText().toString();
             temp = txtCelsius.getText().toString();
             desc = txtDesc.getText().toString();
             System.out.println("Loc = " + loc);
             displayNotifcation();
        }



    }

    

    //menu stuff
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_layout,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.aboutButton:
                startActivity(new Intent(this,About_app.class));
                return true;
            case R.id.radarView:
                startActivity(new Intent(this,WeatherRadarActivity.class));
                return true;
            default:
                Toast.makeText(this,"Error, try restarting the application",Toast.LENGTH_SHORT).show();
                return true;
        }
    }

    //location listener
    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            lat = location.getLatitude();
            lon = location.getLongitude();
            new GetWeather().execute(Common.apiRequest(String.valueOf(lat), String.valueOf(lon)));}
        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {}

        @Override
        public void onProviderEnabled(String s) {}

        @Override
        public void onProviderDisabled(String s) {}
    }

    public void displayNotifcation(){

        String uniqueID = "47894";
        int id = 47894;
        NotificationCompat.Builder notifcation = new NotificationCompat.Builder(this,uniqueID);
        notifcation.setAutoCancel(false);
        notifcation.setTicker("This is the ticker");
        notifcation.setContentTitle(loc + " " + temp); //Location -  temp
        notifcation.setContentText(desc); //condition
        notifcation.setOngoing(true); //make sure it can't be cleared by the user!
        notifcation.setSmallIcon(R.drawable.ic_launcher_background);

        Intent intent = new Intent(this,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        notifcation.setContentIntent(pendingIntent);

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(id,notifcation.build());

    }
}







