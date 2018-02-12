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

public class Day_by_Day extends AppCompatActivity {
    private URL url;
    private TextView first;
    private TextView second;
    private TextView third;
    private TextView fourth;
    private TextView fifth;
    private TextView sixth;
    private TextView seventh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_by_day);
        first=(TextView)findViewById(R.id.first_day);
        second=(TextView)findViewById(R.id.second_day);
        third=(TextView)findViewById(R.id.third_day);
        fourth=(TextView)findViewById(R.id.fourth_day);
        fifth=(TextView)findViewById(R.id.fifth_day);
        sixth=(TextView)findViewById(R.id.sixth_day);
        seventh=(TextView)findViewById(R.id.seventh_day);

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
            JSONArray data_array = json.getJSONObject("daily").getJSONArray("data");
            String[] days = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
            Calendar calendar = Calendar.getInstance();
            int today = calendar.get(Calendar.DAY_OF_WEEK);

            for (int i=0; i<7;i++){
                JSONObject item = data_array.getJSONObject(i);
                switch (i)
                {
                    case 0:
                        first.setText(days[(today+i)%7]+" "+item.getString("temperatureLow")+ " \u2109");
                        break;
                    case 1:
                        second.setText(days[(today+i)%7]+" "+item.getString("temperatureLow")+ " \u2109");
                        break;
                    case 2:
                        third.setText(days[(today+i)%7]+" "+item.getString("temperatureLow")+ " \u2109");
                        break;
                    case 3:
                        fourth.setText(days[(today+i)%7]+" "+item.getString("temperatureLow")+ " \u2109");
                        break;
                    case 4:
                        fifth.setText(days[(today+i)%7]+" "+item.getString("temperatureLow")+ " \u2109");
                        break;
                    case 5:
                        sixth.setText(days[(today+i)%7]+" "+item.getString("temperatureLow")+ " \u2109");
                        break;
                    case 6:
                        seventh.setText(days[(today+i)%7]+" "+item.getString("temperatureLow")+ " \u2109");
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
