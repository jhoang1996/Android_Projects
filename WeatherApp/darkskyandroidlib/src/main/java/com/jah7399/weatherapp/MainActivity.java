package com.jah7399.weatherapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements LocationListener {
    private TextView cityField;
    private TextView windspeed;
    private TextView precipitation;
    private TextView humidity;
    private TextView temperature;
    private TextView avg_48;
    private double longitude;
    private double latitude;
    private String urlText;
    private static final String API = "https://api.darksky.net/forecast/9b084c175bd0d4530f1a8d8fbd73c48c/37.8267,-122.4233";
    LocationManager locationManager;
    Location location;
    String provider;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        cityField = (TextView) findViewById(R.id.city_field);
        windspeed = (TextView) findViewById(R.id.wind_speed);
        precipitation = (TextView) findViewById(R.id.precipitation);
        humidity = (TextView) findViewById(R.id.humidity);
        temperature = (TextView) findViewById(R.id.current_temperature_field);
        avg_48= (TextView) findViewById(R.id.avg_48);


        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(this,
                    new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                    0);
        }
        // Getting LocationManager object
        statusCheck();

        locationManager = (LocationManager) getSystemService(
                Context.LOCATION_SERVICE);

        // Creating an empty criteria object
        Criteria criteria = new Criteria();

        // Getting the name of the provider that meets the criteria
        provider = locationManager.getBestProvider(criteria, false);

        if (provider != null && !provider.equals("")) {
            if (!provider.contains("gps")) { // if gps is disabled
                final Intent poke = new Intent();
                poke.setClassName("com.android.settings",
                        "com.android.settings.widget.SettingsAppWidgetProvider");
                poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
                poke.setData(Uri.parse("3"));
                sendBroadcast(poke);
            }
            // Get the location from the given provider
            location = locationManager
                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 0, (LocationListener) this);

            if (location != null)
                onLocationChanged(location);
            else
                location = locationManager.getLastKnownLocation(provider);
            if (location != null)
                onLocationChanged(location);
            else

                Toast.makeText(getBaseContext(), "Location can't be retrieved",
                        Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(getBaseContext(), "No Provider Found",
                    Toast.LENGTH_SHORT).show();
        }


        //cityField.setText(String.valueOf(location));
        Context myContext = this;
        final JSONObject json = getJSON(myContext, String.valueOf(longitude)+","+String.valueOf(latitude));
        Log.v("JSON:", json.toString());
        renderWeather(json);
    }
    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(
                Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(
                "Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false).setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog,
                                        final int id) {
                        startActivity(new Intent(
                                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog,
                                        final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    @Override
    public void onLocationChanged(Location location) {
        // Setting Current Longitude
        //cityField.setText("Longitude:" + location.getLongitude());
        //humidity.setText("TEST:"+location.getLatitude());
        longitude=location.getLongitude();
        latitude=location.getLatitude();
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(this,
                    new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                    0);
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.five_hours:
                Intent intent = new Intent(this, nextFiveHoursActivity.class);
                intent.putExtra("URL", urlText);
                this.startActivity(intent);
                return true;
            case R.id.day_by_day:
                Intent intent2 = new Intent(this, Day_by_Day.class);
                intent2.putExtra("URL", urlText);
                this.startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void renderWeather(JSONObject json){
        try {
            //detailsField.setText("");
            //cityField.setText("");
            Calendar calendar = Calendar.getInstance();
            int today = calendar.get(Calendar.DAY_OF_WEEK);

            String[] days = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
            JSONArray data_array = json.getJSONObject("daily").getJSONArray("data");
            JSONObject data_currently = json.getJSONObject("currently");
            for (int i=0; i<7;i++){
                JSONObject item = data_array.getJSONObject(i);

                String temperatureMax = item.getString("temperatureMax");
                String temperatureMin = item.getString("temperatureMin");
                String w_summary = item.getString("summary");
                temperatureMax = temperatureMax.substring(0,2);
                temperatureMin = temperatureMin.substring(0,2);

                //detailsField.setText(detailsField.getText()  + days[(today+i)%7] + ": "+temperatureMin+" - "+temperatureMax +" "+w_summary+ "\n");
            }
            JSONArray data_array_hourly = json.getJSONObject("hourly").getJSONArray("data");
            double sum=0;
            for (int i=0; i<48;i++){
                JSONObject item = data_array_hourly.getJSONObject(i);
                sum+=item.getDouble("temperature");
            }
            double avg=sum/48;

            JSONObject item = data_array.getJSONObject(0);
            windspeed.setText("Wind Speed: "+data_currently.getString("windSpeed"));
            temperature.setText("Temperature: "+data_currently.getString("temperature")+ " \u2109");
            humidity.setText("Humidity: "+data_currently.getString("humidity"));
            precipitation.setText("Precipitation: "+data_currently.getString("precipIntensity"));
            //cityField.setText("Timezone: "+json.getString("timezone"));
            avg_48.setText("Average for the next 48 hours: "+(int)avg+ " \u2109");


            //currentTemperatureField.setText(json.getJSONObject("currently").getString("temperature") + " \u00b0 F");
           /* updatedField.setText(

                    // "SUMMARY OF WEEK  : " +
                    json.getJSONObject("daily").getString("summary")
                    // +      "\nTIME ZONE  : " + json.getString("timezone")
            );*/



        }catch(Exception e){
            Log.e("SimpleWeather", "One or more fields not found in the JSON data");
        }
    }
    public JSONObject getJSON(Context context, String coord){

        URL url = null;
        urlText="https://api.darksky.net/forecast/9b084c175bd0d4530f1a8d8fbd73c48c/"+latitude+","+longitude;
        String result = null;
        try {
            url = new URL(urlText);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            result = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        JSONObject jObject = null;
        try {
            jObject = new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }/*
        try {
            String aJsonString = jObject.getString("windSpeed");
            windspeed.setText(aJsonString);
            return jObject;
        } catch (JSONException e) {
            e.printStackTrace();
            windspeed.setText("NULL");
            return  null;
        }*/
        return jObject;
        /*
        try {
            //coord = "40.7127,-74.0059";//debug
            URL url = new URL("https://api.darksky.net/forecast/9b084c175bd0d4530f1a8d8fbd73c48c/37.8267,-122.4233");

            HttpURLConnection connection =
                    (HttpURLConnection)url.openConnection();
            connection.getInputStream();

            System.out.print("CONNECTION:::" + connection.getInputStream());

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            System.out.print("url:::");
            StringBuffer json = new StringBuffer(1024);
            String tmp="";
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();

            JSONObject data = new JSONObject(json.toString());

            return data;
        }catch(Exception e){
            e.printStackTrace();

            return null;
        }
        */
    }

}
