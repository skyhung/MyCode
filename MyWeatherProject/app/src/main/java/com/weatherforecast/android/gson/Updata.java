package com.weatherforecast.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hasee on 2018/12/18.
 */

public class Updata {
    @SerializedName("loc")
    public String loc;//当地时间

    @SerializedName("utc")
    public String utctime;//UTC时间

}
