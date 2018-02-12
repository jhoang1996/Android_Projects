package com.jah7399.johnhiott.darkskyandroid;

import android.app.Application;

import com.jah7399.weatherapp.ForecastApi;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ForecastApi.create("5f0c0745a2939e44dd79a189c5386962");
    }
}
