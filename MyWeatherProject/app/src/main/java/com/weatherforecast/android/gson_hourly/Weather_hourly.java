package com.weatherforecast.android.gson_hourly;


import com.google.gson.annotations.SerializedName;


import java.util.List;

/**
 * Created by hasee on 2019/1/1.
 */

public class Weather_hourly {
    public String status;//接口状态,返回ok为成功

    public Basic basic;

    @SerializedName("hourly")
    public List<Hourly> hourlyList;
}
