package com.weatherforecast.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;




public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //从SharedPreferences中读取缓存数据，如果不为null，直接跳转到WeatherActivity_x
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getString("weather_x", null) != null) {
            Intent intent = new Intent(this, WeatherActivity_x.class);
            startActivity(intent);
            finish();
        }
    }




}