package com.jah7399.weatherapp;

import android.content.Context;
import android.icu.util.Calendar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

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

public class nextFiveHoursActivity extends AppCompatActivity {
    private URL url;
    private TextView first;
    private TextView second;
    private TextView third;
    private TextView fourth;
    private TextView fifth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_five_hours);

        first=(TextView)findViewById(R.id.first_hour);
        second=(TextView)findViewById(R.id.second_hour);
        third=(TextView)findViewById(R.id.third_hour);
        fourth=(TextView)findViewById(R.id.fourth_hour);
        fifth=(TextView)findViewById(R.id.fifth_hour);

        String urltext = getIntent().getStringExtra("URL");
        try {
            url=new URL(urltext);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        JSONObject jsonObj=getJSON(this);
        renderWeather(jsonObj);
    }


    private void renderWeather(JSONObject json){
        try {
            JSONArray data_array = json.getJSONObject("hourly").getJSONArray("data");
            for (int i=0; i<5;i++){
                JSONObject item = data_array.getJSONObject(i);
                switch (i)
                {
                    case 0:
                        first.setText("1st Hour: "+item.getString("temperature")+ " \u2109");
                        break;
                    case 1:
                        second.setText("2nd Hour: "+item.getString("temperature")+ " \u2109");
                        break;
                    case 2:
                        third.setText("3rd Hour: "+item.getString("temperature")+ " \u2109");
                        break;
                    case 3:
                        fourth.setText("4th Hour: "+item.getString("temperature")+ " \u2109");
                        break;
                    case 4:
                        fifth.setText("5th Hour: "+item.getString("temperature")+ " \u2109");
                        break;
                }
            }


        }catch(Exception e){
            Log.e("AppWeather", "One or more fields not found in the JSON data");
        }
    }
    public JSONObject getJSON(Context context){

        String result = null;
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
        }
        return jObject;

    }


}
