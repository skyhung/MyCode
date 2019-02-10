package com.weatherforecast.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hasee on 2018/12/18.
 */

public class Now {

    @SerializedName("tmp")
    public String temperature;//温度

    @SerializedName("cond")
    public String sTemperature;//体感温度


    public String cond_code;//实况天气代码


    public String cond_txt;//实况天气状况描述


    public String wind_deg;//风向角度


    public String wind_dir;//风向

    public String wind_sc;//风力

    public String wind_spd;//风速

    public String hum;//相对湿度

    public String pcpn;//降水量

    public String pres;//大气压强

    public String vis;//能见度

    public String cloud;//云量



}
