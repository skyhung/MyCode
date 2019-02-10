package com.weatherforecast.android;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.weatherforecast.android.gson.Hourly;
import com.weatherforecast.android.gson.Now;
import com.weatherforecast.android.gson.Weather_x;
import com.weatherforecast.android.gson_air.Aqi;
import com.weatherforecast.android.gson_hourly.Weather_hourly;
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

/**
 * Created by hasee on 2018/12/31.
 */

public class HourlyFragment extends Fragment {

    private View view;
    private String mCityName;
    private List<Hourly> hourlyList= new ArrayList<>();
    private LineChartView lineChart;
    String[] hourlyTime=new String[24];
    int[] hourlyTemp=new int[24];
    private List<String> hTime;
    private List<String> hTemp;
    String[] date = {"10-22","11-22","12-22","1-22","6-22","5-23","5-22","6-22","5-23","5-22"};//X轴测试数据
    String[] date2 = {"10:22","11:22","12:22","1:22","6:22","5:23","5:22","6:22","5:23","5:22"};
    int[] score= {50,42,90,33,-10,74,-22,-18,79,-20};//数据点测试数据
    private List<PointValue> mPointValues = new ArrayList<PointValue>();
    private List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();
    private List<AxisValue> mAxisYValues = new ArrayList<AxisValue>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.hourly_weather, container, false);

        lineChart = (LineChartView)view.findViewById(R.id.chart);


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String weatherString = prefs.getString("weather_x", null);

        Weather_x weather_x = Utility.handleWeather_xResponse(weatherString);
        mCityName = weather_x.basic.cityName;
        requestHourlyWeather(mCityName);




        return view;
    }


     //设置X 轴的显示
      private void getAxisXLables() {
          for (int i = 0; i<24 ;i++) {
              mAxisXValues.add(new AxisValue(i).setLabel(hourlyTime[i]));

          }
      }
      //设置Y轴
      private void gerAxisYLables(){
          for (int m = -30; m < 60; m+=10) {
              mAxisYValues.add(new AxisValue(m).setValue(m));
          }
      }

     //图表的每个点的显示
    private void getAxisPoints() {
        for (int i = 0; i<24; i++) {
            mPointValues.add(new PointValue(i, hourlyTemp[i]));

        }
    }

    private void initLineChart() {
        Line line = new Line(mPointValues).setColor(Color.parseColor("#FFCD41"));  //折线的颜色（橙色）
        List<Line> lines = new ArrayList<Line>();
        line.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.DIAMOND）
        line.setCubic(false);//曲线是否平滑，即是曲线还是折线
        line.setFilled(false);//是否填充曲线的面积
        line.setHasLabels(true);//曲线的数据坐标是否加上备注
        //      line.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
        line.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
        line.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）
        line.setPointColor(Color.WHITE);// 设置节点颜色
        line.setPointRadius(5);//节点大小
        //line.setColor(Color.parseColor("#FFFFFF"));
        //line.setAreaTransparency(0);//线的透明度
        line.setStrokeWidth(2);//线的粗细
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setValueLabelBackgroundEnabled(false);//是否开启点数据的背景色
        //data.setValueLabelBackgroundColor(Color.BLUE);// 设置数据背景颜色
        data.setValueLabelsTextColor(Color.WHITE);// 设置数据文字颜色
        data.setValueLabelTextSize(15);// 设置数据文字大小
        data.setValueLabelTypeface(Typeface.MONOSPACE);// 设置数据文字样式
        data.setLines(lines);//线的集合放在chart数据中

        //X轴
        Axis axisX = new Axis(); //X轴
        axisX.setHasTiltedLabels(false);  //X坐标轴字体是斜的显示还是直的，true是斜的显示
        axisX.setTextColor(Color.WHITE);  //设置字体颜色
        //axisX.setName("date");  //表格名称
        axisX.setTextSize(10);//设置字体大小
        axisX.setMaxLabelChars(8); //最多几个X轴坐标
        axisX.setValues(mAxisXValues);  //填充X轴的坐标名称
        data.setAxisXBottom(axisX); //x 轴在底部
        //data.setAxisXTop(axisX);  //x 轴在顶部
        axisX.setHasLines(true); //x 轴分割线

        // Y轴
        Axis axisY = new Axis();  //Y轴
        axisY.setName("温度");//y轴标注
        axisY.setTextSize(10);//设置字体大小
        axisY.setTextColor(Color.WHITE);
        axisY.setValues(mAxisYValues);
        data.setAxisYLeft(axisY);//Y轴设置在左边
        //data.setAxisYRight(axisY);  //y轴设置在右边

        //设置行为属性，支持缩放、滑动以及平移
        lineChart.setInteractive(true);//设置图表是否可以与用户互动
        lineChart.setZoomEnabled(true);//设置是否缩放
        lineChart.setZoomType(ZoomType.HORIZONTAL);//设置缩放方式
        lineChart.setMaxZoom((float) 2);//最大方法比例
        lineChart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);//是否允许在父容器中滑动
        lineChart.setLineChartData(data);
        lineChart.setVisibility(View.VISIBLE);

/*
        Viewport v = new Viewport(lineChart.getMaximumViewport());
        v.left = 0;
        v.right = 7;
        lineChart.setCurrentViewport(v);*/


    }
    //设置XY轴的坐标上下限
    private void resetViewport() {
        final Viewport v = new Viewport(lineChart.getMaximumViewport());
        v.bottom = -30;
        v.top = 60;
        v.left = 0;
        v.right = 23;
        lineChart.setMaximumViewport(v);
        lineChart.setCurrentViewport(v);
    }


    //访问每小时数据
    public void requestHourlyWeather(final String cityName) {
        String hourlyUrl = "https://api.heweather.net/s6/weather/hourly?location=" + cityName + "&key=a7197b82cdea44e1bc999cec47936782";
        HttpUtil.sendOkHttpRequest(hourlyUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather_hourly weatherHourly = Utility.handleWeather_hourlyResponse(responseText);
                //将当前线程切换到主线程
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //如果服务器返回的status为ok，说明请求成功
                        if (weatherHourly.hourlyList != null) {
                            int size = weatherHourly.hourlyList.size();
                            int j = 0;

                            for (com.weatherforecast.android.gson_hourly.Hourly hourly_x : weatherHourly.hourlyList) {

                                    hourlyTime[j] = hourly_x.getTime();
                                    hourlyTime[j] = hourlyTime[j].substring(11, 16);

                                    hourlyTemp[j] = Integer.parseInt(hourly_x.tmp);

                                    j++;


                            }

                            getAxisXLables();//获取x轴的标注
                            gerAxisYLables();
                            initLineChart();//初始化
                            resetViewport();
                            getAxisPoints();

                        } else {
                            Toast.makeText(getActivity(), "获取折线图失败1", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "获取折线图失败2", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
    }
}
