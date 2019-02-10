package com.weatherforecast.android.gson;

/**
 * Created by hasee on 2018/12/18.
 */

public class Hourly {
    public String time;//预报时间

    public String tmp;//温度

    public String cond_code;//天气状况代码

    public String cond_txt;//天气状况描述

    public String wind_deg;//风向360角度
    public String wind_dir;//风向

    public String wind_sc;//风力

    public String wind_spd;//风速，公里/小时

    public String hum;//	相对湿度
    public String pres;//	大气压强

    public String dew;//露点温度

    public String cloud;//云量

    public String getTime(){
        return time;
    }


}
