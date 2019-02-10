package com.weatherforecast.android.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

//用于和服务器交互的类，导入okhttp第三方库的方法

public class HttpUtil {

    //传入一个服务器地址，并注册一个回调来处理服务器响应
    public static void sendOkHttpRequest(String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();//创建一个okhttpclient的实例
        Request request = new Request.Builder().url(address).build();//创建一个request对象，发起一条http请求
        client.newCall(request).enqueue(callback);// 使用okhttp的实例发送请求，并执行回调
    }

}
