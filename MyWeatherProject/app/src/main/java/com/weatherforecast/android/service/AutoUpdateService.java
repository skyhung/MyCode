package com.weatherforecast.android.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.weatherforecast.android.gson.Weather_x;
import com.weatherforecast.android.gson_air.Aqi;
import com.weatherforecast.android.util.HttpUtil;
import com.weatherforecast.android.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


//后台自动更新天气信息
public class AutoUpdateService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updateBingPic();
        final int anHour= 60 * 60 * 1000; // 这是1小时的毫秒数
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int updata_time = prefs.getInt("updataTime", 1);
        //创建定时任务，可周期性的执行一个Intent
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);//获取一个AlarmManager实例
        //更新的时间
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour*updata_time;//elapsedRealtime()为系统启动到现在的毫秒数
        Intent i = new Intent(this, AutoUpdateService.class);
        //打开一个服务组件
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);//用PendingIntent指定处理定时任务的服务为AutoUpdateService
        manager.cancel(pi);
        //ELAPSED_REALTIME_WAKEUP表示让定时任务的触发时间从系统开机算起
        //第二个参数表示定时任务触发时间。
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);



        return super.onStartCommand(intent, flags, startId);
    }


     //更新天气信息。
    private void updateWeather(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather_x", null);
        String aqiString=prefs.getString("aqi_x",null);
        if (weatherString != null) {
            // 有缓存时直接解析天气数据
            Weather_x weather_x = Utility.handleWeather_xResponse(weatherString);
            Aqi aqi=Utility.handleAqiResponse(aqiString);
            String cityName = weather_x.basic.cityName;
            String weatherUrl = "https://free-api.heweather.net/s6/weather?location=" + cityName + "&key=a7197b82cdea44e1bc999cec47936782";
            String aqiUrl = "https://free-api.heweather.net/s6/air/now?location=" + cityName + "&key=a7197b82cdea44e1bc999cec47936782";

            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText = response.body().string();
                    Weather_x weather_x1 = Utility.handleWeather_xResponse(responseText);
                    if (weather_x1 != null && "ok".equals(weather_x1.status)) {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("weather_x", responseText);
                        editor.apply();
                    }
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }
            });

            HttpUtil.sendOkHttpRequest(aqiUrl, new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText2 = response.body().string();
                    Aqi aqi1 = Utility.handleAqiResponse(responseText2);
                    if (aqi1 != null && "ok".equals(aqi1.status)) {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("aqi_x", responseText2);
                        editor.apply();
                    }
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }


     //更新必应每日一图
    private void updateBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }

}
