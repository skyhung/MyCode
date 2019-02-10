package com.weatherforecast.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hasee on 2018/12/18.
 */

public class Lifestyle {


    @SerializedName("brf")
    public String life_index;//生活指数简介

    public String txt;//生活指数详细描述

    public String type;//生活指数类型
}
