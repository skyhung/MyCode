package com.weatherforecast.android.util;

import android.text.TextUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.weatherforecast.android.db.City;
import com.weatherforecast.android.db.County;
import com.weatherforecast.android.db.Province;

import com.google.gson.Gson;
import com.weatherforecast.android.gson.Hourly;
import com.weatherforecast.android.gson.Now;
import com.weatherforecast.android.gson.Weather_x;
import com.weatherforecast.android.gson_air.Aqi;
import com.weatherforecast.android.gson_hourly.Weather_hourly;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


//用于解析服务器返回的Json格式数据的工具类

public class Utility {


     // 解析和处理服务器返回的省级数据
    public static boolean handleProvinceResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            //返回数据非空，执行try语句
            try {
                //用JSONArray和JSONObject将数据解析出来，然后组装成实体类对象，
                // 再调用save()方法将数据存储到数据库中
                JSONArray allProvinces = new JSONArray(response);
                for (int i = 0; i < allProvinces.length(); i++) {
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


     //解析和处理服务器返回的市级数据
    public static boolean handleCityResponse(String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCities = new JSONArray(response);
                for (int i = 0; i < allCities.length(); i++) {
                    JSONObject cityObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


     //解析和处理服务器返回的县级数据
    public static boolean handleCountyResponse(String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCounties = new JSONArray(response);
                for (int i = 0; i < allCounties.length(); i++) {
                    JSONObject countyObject = allCounties.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));//中途更换了API接口，weather_id未使用
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }




    //将返回的JSON数据解析成Weather_x实体类
    public static Weather_x handleWeather_xResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");

            String weather_xContent = jsonArray.getJSONObject(0).toString();
            //利用GSON开源库的fromJson方法直接将JSON数据转换成Weather_x对象
            return new Gson().fromJson(weather_xContent, Weather_x.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //将返回的JSON数据解析成Aqi实体类
    public static Aqi handleAqiResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");

            String aqiContent = jsonArray.getJSONObject(0).toString();
            //利用GSON开源库的fromJson方法直接将JSON数据转换成Aqi对象
            return new Gson().fromJson(aqiContent, Aqi.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //将返回的JSON数据解析成Weather_hourly实体类
    public static Weather_hourly handleWeather_hourlyResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");


            String hourlyContent = jsonArray.getJSONObject(0).toString();

            return new Gson().fromJson(hourlyContent, Weather_hourly.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
