package com.weatherforecast.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hasee on 2018/12/18.
 */

public class Basic {

    @SerializedName("location")
    public String cityName;

    @SerializedName("cid")
    public String cityId;



}
