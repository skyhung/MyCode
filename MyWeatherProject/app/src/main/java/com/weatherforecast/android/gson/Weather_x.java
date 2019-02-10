package com.weatherforecast.android.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by hasee on 2018/12/18.
 */

public class Weather_x {
    public String status;//接口状态,返回ok为成功
    public Basic basic;
    public Now now;
    public Updata updata;
    @SerializedName("hourly")
    public List<Hourly> hourlyList;
   @SerializedName("daily_forecast")
    public List<Daily_forecast> forecastList;
    @SerializedName("lifestyle")
    public List<Lifestyle> lifeStyleList;


}
