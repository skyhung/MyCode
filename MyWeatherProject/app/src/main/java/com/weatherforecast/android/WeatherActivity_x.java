package com.weatherforecast.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.weatherforecast.android.gson.Daily_forecast;
import com.weatherforecast.android.gson.Lifestyle;
import com.weatherforecast.android.gson.Weather_x;
import com.weatherforecast.android.gson_air.Aqi;
import com.weatherforecast.android.service.AutoUpdateService;
import com.weatherforecast.android.util.HttpUtil;
import com.weatherforecast.android.util.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;



public class WeatherActivity_x extends AppCompatActivity {

    public DrawerLayout drawerLayout;
    public SwipeRefreshLayout swipeRefresh;
    private ScrollView weatherLayout;
    private Button switch_city_Button;
    private LinearLayout chooseCity;
    private TextView titleCity;

    private TextView degreeText;
    private TextView weatherInfoText;
    private TextView weatherInfo_windDrection;
    private TextView weatherInfo_windSpeed;
    private TextView weatherInfo_humidity;
    private TextView weatherInfo_rainFall;
    private TextView weatherInfo_pressure;
    private TextView weatherInfo_visibility;
    private LinearLayout forecastLayout;
    private LinearLayout suggestionLayout;

    private TextView aqiText;
    private TextView pm25Text;
    private TextView aqiText2;
    private TextView pm25Text2;
    private TextView mainPollution;
    private TextView pm10Text;
    private TextView no2Text;
    private TextView so2Text;
    private TextView coText;
    private TextView o3Text;
    private TextView airQlty;

    private ImageView bingPicImg;
    private TextView airDetail;
    private LinearLayout aqiMain;
    private LinearLayout aqiX;

    private String mCityName;
    private TextView setting;

    public ImageView hideArrow;
    public TextView life_text;
    public int position;
    public int i=0,j=0;


    boolean visibility_Flag = true;
    private View.OnClickListener itemClick;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //如果系统版本号大于等于21，调用getWindow().getDecorView()方法拿到当前活动的DecorView
        //再调用它的setSystemUiVisibility()方法来改变系统UI的显示
        //View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN和View.SYSTEM_UI_FLAG_LAYOUT_STABLE表示活动的布局会显示在状态栏上面
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        //将状态栏设置为透明色
        setContentView(R.layout.activity_weather);
        // 初始化各控件
        bingPicImg = (ImageView) findViewById(R.id.bing_pic_img);
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleCity = (TextView) findViewById(R.id.title_city);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        weatherInfo_windDrection = (TextView) findViewById(R.id.wind_direction);
        weatherInfo_windSpeed = (TextView) findViewById(R.id.wind_speed);
        weatherInfo_humidity = (TextView) findViewById(R.id.humidity);
        weatherInfo_rainFall = (TextView) findViewById(R.id.rainfall);
        weatherInfo_pressure = (TextView) findViewById(R.id.press);
        weatherInfo_visibility = (TextView) findViewById(R.id.visibility);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        suggestionLayout = (LinearLayout) findViewById(R.id.suggestion_layout);
        //空气质量文本控件
        aqiText = (TextView) findViewById(R.id.aqi_text_x);
        pm25Text = (TextView) findViewById(R.id.pm25_text_x);
        mainPollution = (TextView) findViewById(R.id.air_main);
        pm10Text = (TextView) findViewById(R.id.air_pm10);
        no2Text = (TextView) findViewById(R.id.air_no2);
        so2Text = (TextView) findViewById(R.id.air_so2);
        coText = (TextView) findViewById(R.id.air_co);
        o3Text = (TextView) findViewById(R.id.air_o3);
        airQlty = (TextView) findViewById(R.id.air_qlty);
        aqiText2 = (TextView) findViewById(R.id.air_aqi);
        pm25Text2 = (TextView) findViewById(R.id.air_pm25);

        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);//设置下拉刷新进度条的颜色
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        switch_city_Button = (Button) findViewById(R.id.switch_city);
        chooseCity=(LinearLayout) findViewById(R.id.bottom_weixin);
        setting=(TextView) findViewById(R.id.title_setting);
        //
        airDetail=(TextView) findViewById(R.id.aqi_detail);
        aqiMain = (LinearLayout) findViewById(R.id.aqi_main);
        aqiX = (LinearLayout) findViewById(R.id.aqi_x);





        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather_x", null);
        String aqiString=prefs.getString("aqi_x",null);
        String cityName=prefs.getString("city_name2",null);

        if(cityName!=null){
            requestWeather(cityName);
            prefs.edit().putString("city_name2",null).apply();//重置缓存数据
        }
        else{
            if (weatherString != null) {
                // 有缓存时直接解析天气数据
                Weather_x weather_x = Utility.handleWeather_xResponse(weatherString);
                Aqi aqi=Utility.handleAqiResponse(aqiString);
                mCityName = weather_x.basic.cityName;
                showWeatherInfo(weather_x);
                //读取缓存信息后，再从服务器刷新信息。
                requestWeather(mCityName);
            } else {
                // 无缓存时去服务器查询天气
                mCityName = getIntent().getStringExtra("city_name");
                weatherLayout.setVisibility(View.INVISIBLE);

                requestWeather(mCityName);
            }
        }




        //下拉刷新监听器
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mCityName);
            }
        });

        //选择城市
        switch_city_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        //跳转设置界面
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(WeatherActivity_x.this,SettingActivity.class);
                startActivity(intent);
            }
        });

         ////监听空气质量的详情按钮
        airDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(aqiText.getText().equals("暂无数据")){
                    Toast.makeText(WeatherActivity_x.this, "暂无空气质量相关数据", Toast.LENGTH_SHORT).show();
                }
                else{
                aqiMain.setVisibility(View.GONE);
                aqiX.setVisibility(View.VISIBLE);}
            }
        });


        //监听生活建议中每一项的hideArrow
        itemClick=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (WeatherActivity_x.this != null) {

                    position = (int) view.getTag(); //获取点击相应hideArrow的标签
                    TextView life_text_x=(TextView) findViewById(position);//获得该项hideArrow相应的life_text实例
                    ImageView hideArrow_x=(ImageView)findViewById(position+1000);
                    //ImageView hideArrow_x= (ImageView) suggestionLayout.getChildAt(position);
                    if(visibility_Flag){
                        hideArrow_x.setImageResource(R.drawable.arrow_close);
                        life_text_x.setVisibility(View.GONE);
                        visibility_Flag=false;
                        Toast.makeText(WeatherActivity_x.this, "隐藏成功", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        hideArrow_x.setImageResource(R.drawable.arrow_open);
                        life_text_x.setVisibility(View.VISIBLE);
                        visibility_Flag=true;
                    }
                }
                else{Toast.makeText(WeatherActivity_x.this, "出错了", Toast.LENGTH_SHORT).show();}
            }
        };



        //从SharedPreferences中读取缓存的背景图片，如果有直接使用Glide加载这张图
        String bingPic = prefs.getString("bing_pic", null);
        if (bingPic != null) {
            Glide.with(this).load(bingPic).into(bingPicImg);
        } else {
            loadBingPic();
        }
    }


    //根据城市名请求城市天气与空气信息。
    public void requestWeather(final String cityName) {
        String weatherUrl = "https://free-api.heweather.net/s6/weather?location=" + cityName + "&key=a7197b82cdea44e1bc999cec47936782";
        String aqiUrl = "https://free-api.heweather.net/s6/air/now?location=" + cityName + "&key=a7197b82cdea44e1bc999cec47936782";
        ///访问天气api接口
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather_x weather_x = Utility.handleWeather_xResponse(responseText);
                //将当前线程切换到主线程
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //如果服务器返回的status为ok，说明请求成功
                        if ( weather_x != null &&"ok".equals(weather_x.status)) {
                            //将返回的数据缓存到SharedPreferences中，
                            // SharedPreferences调用edit()方法即可获取它所对应的Editor对象
                            // 并调用showWeatherInfo方法显示内容
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity_x.this).edit();
                            editor.putString("weather_x", responseText);
                            editor.apply();
                            mCityName = weather_x.basic.cityName;
                            showWeatherInfo(weather_x);
                            replaceFragment(new HourlyFragment());
                        } else {
                            Toast.makeText(WeatherActivity_x.this, "获取天气信息失败1", Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);//下拉刷新请求结束，并隐藏刷新进度条
                    }
                });
            }
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity_x.this, "获取天气信息失败2", Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });

///////////////访问空气质量api接口
        HttpUtil.sendOkHttpRequest(aqiUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText2 = response.body().string();
                final Aqi aqi = Utility.handleAqiResponse(responseText2);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if ( aqi != null &&"ok".equals(aqi.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity_x.this).edit();
                            editor.putString("aqi_x", responseText2);
                            editor.apply();
                            mCityName = aqi.basic.cityName;
                            showAqiInfo(aqi);
                        } else {
                            aqiMain.setVisibility(View.VISIBLE);
                            aqiX.setVisibility(View.GONE);
                            aqiText.setText("暂无数据");
                            pm25Text.setText("暂无数据");
                            Toast.makeText(WeatherActivity_x.this, "该城市没有空气质量数据", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity_x.this, "获取空气质量信息失败", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

        loadBingPic();
    }





    //加载必应每日一图
    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                //调用SharedPreferences对象的edit()方法来获取一个SharedPreferences.Editor对象
                //向SharedPreferences.Editor对象中添加数据
                //调用apply()将添加的数据提交
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity_x.this).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity_x.this).load(bingPic).into(bingPicImg);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }




    //处理并展示Weather_x实体类中的数据。
    private void showWeatherInfo(Weather_x weather_x) {
        String cityName = weather_x.basic.cityName;


        String degree = weather_x.now.temperature + "℃";
        String weatherInfo = weather_x.now.cond_txt;
        //气象信息部分
        String windDrection = weather_x.now.wind_dir;//风向
        String windSpeed = weather_x.now.wind_spd+"km/h";//风速
        String humidity = weather_x.now.hum+"%";//湿度
        String rainFall = weather_x.now.pcpn+"mm";//降水
        String pressure = weather_x.now.pres+"hpa";//压强
        String visibility = weather_x.now.vis+"km";//能见度
        weatherInfo_windDrection.setText(windDrection);
        weatherInfo_windSpeed.setText(windSpeed);
        weatherInfo_humidity.setText(humidity);
        weatherInfo_rainFall.setText(rainFall);
        weatherInfo_pressure.setText(pressure);
        weatherInfo_visibility.setText(visibility);

        titleCity.setText(cityName);
        //titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        suggestionLayout.removeAllViews();
        //for循环处理未来几天天气信息,在循环中动态加载forecast_item布局并设置相应数据，然后添加到父布局当中
        for (Daily_forecast daily_forecast : weather_x.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);
            dateText.setText(daily_forecast.date);
            infoText.setText(daily_forecast.cond_txt_d);
            maxText.setText(daily_forecast.tmp_max);
            minText.setText(daily_forecast.tmp_min);
            forecastLayout.addView(view);
        }



        //for循环处理生活指数,在循环中动态加载suggestion_item布局并设置相应数据，然后添加到父布局当中
        for (Lifestyle lifestyle : weather_x.lifeStyleList) {
            View view2 = LayoutInflater.from(this).inflate(R.layout.suggestion_item, suggestionLayout , false);
            TextView life_type = (TextView) view2.findViewById(R.id.type);
            TextView life_brf = (TextView) view2.findViewById(R.id.brf);
            life_text = (TextView) view2.findViewById(R.id.text);
            hideArrow=(ImageView)view2.findViewById(R.id.hide_arrow);
            if(lifestyle.type.equals("comf")){life_type.setText("舒适度指数");}
            if(lifestyle.type.equals("cw")){life_type.setText("洗车指数");}
            if(lifestyle.type.equals("drsg")){life_type.setText("穿衣指数");}
            if(lifestyle.type.equals("flu")){life_type.setText("感冒指数");}
            if(lifestyle.type.equals("sport")){life_type.setText("运动指数");}
            if(lifestyle.type.equals("trav")){life_type.setText("旅游指数");}
            if(lifestyle.type.equals("uv")){life_type.setText("紫外线指数");}
            if(lifestyle.type.equals("air")){life_type.setText("空气污染扩散条件指数");}
            if(lifestyle.type.equals("ac")){life_type.setText("空调开启指数");}
            if(lifestyle.type.equals("ag")){life_type.setText("过敏指数");}
            if(lifestyle.type.equals("gl")){life_type.setText("太阳镜指数");}
            if(lifestyle.type.equals("mu")){life_type.setText("化妆指数");}
            if(lifestyle.type.equals("airc")){life_type.setText("晾晒指数");}
            if(lifestyle.type.equals("ptfc")){life_type.setText("交通指数");}
            if(lifestyle.type.equals("fisin")){life_type.setText("钓鱼指数");}
            if(lifestyle.type.equals("spi")){life_type.setText("防晒指数");}

            //life_type.setText(lifestyle.type);
            life_brf.setText(lifestyle.life_index);
            life_text.setText(lifestyle.txt);
            suggestionLayout.addView(view2);
            //hideArrow隐藏箭头的监听
            hideArrow.setOnClickListener(itemClick);
            //给每个hideArrow设置标签
            hideArrow.setTag(i);
            hideArrow.setId(i+1000);
            //life_text.setTag(j);
            //给每个life_text设置ID，该ID与同一行的hideArrow相等
            life_text.setId(j);
            i++;j++;

        }

        weatherLayout.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }

    //处理并展示Aqi实体类中的数据
    private void showAqiInfo(Aqi aqi){

        aqiText.setText(aqi.air_now_city.aqi);
        pm25Text.setText(aqi.air_now_city.pm25);

        mainPollution.setText(aqi.air_now_city.main);
        pm10Text.setText(aqi.air_now_city.pm10);
        no2Text.setText(aqi.air_now_city.no2);
        so2Text.setText(aqi.air_now_city.so2);
        coText.setText(aqi.air_now_city.co);
        o3Text.setText(aqi.air_now_city.o3);
        airQlty.setText(aqi.air_now_city.qlty);
        aqiText2.setText(aqi.air_now_city.aqi);
        pm25Text2.setText(aqi.air_now_city.pm25);


    }
    //动态加载碎片
    public void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager=getSupportFragmentManager();//获取FragmentManager实例
        FragmentTransaction transaction=fragmentManager.beginTransaction();//开启一个事物
        transaction.replace(R.id.hourly_layout,fragment);//向容器内添加碎片
        transaction.commit();
    }




}
