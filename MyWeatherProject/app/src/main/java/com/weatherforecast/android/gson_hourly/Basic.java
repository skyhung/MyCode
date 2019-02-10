package com.weatherforecast.android.gson_hourly;

import com.google.gson.annotations.SerializedName;



public class Basic {

    @SerializedName("location")
    public String cityName;

    @SerializedName("cid")
    public String cityId;



}
