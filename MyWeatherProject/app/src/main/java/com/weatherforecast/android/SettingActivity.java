package com.weatherforecast.android;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.util.ArrayList;
import java.util.List;


public class SettingActivity extends AppCompatActivity {
    private TextView updateTime;
    private TextView searchCity;
    private TextView location;
    public String[] data={"一小时","两小时","四小时","八小时"};
    public LocationClient mLocationClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        updateTime=(TextView)findViewById(R.id.update_time);
        searchCity=(TextView) findViewById(R.id.search_city);
        location=(TextView) findViewById(R.id.location);

        mLocationClient = new LocationClient(getApplicationContext());//创建一个LocationClient实例
        mLocationClient.registerLocationListener(new MyLocationListener());//注册一个定位监听器

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SettingActivity.this);


        updateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog=new AlertDialog.Builder(SettingActivity.this);
                dialog.setTitle("选择时间");
                dialog.setSingleChoiceItems(data, prefs.getInt("index", 0), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        prefs.edit().putInt("index",which).apply();//SharedPreferences保存选择的index
                        switch (which){
                            case 0:prefs.edit().putInt("updataTime",1).apply();
                            case 1:prefs.edit().putInt("updataTime",2).apply();
                            case 2:prefs.edit().putInt("updataTime",4).apply();
                            case 3:prefs.edit().putInt("updataTime",8).apply();
                        }
                        Toast.makeText(SettingActivity.this, "更改成功", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        searchCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editText = new EditText(SettingActivity.this);
                AlertDialog.Builder dialog2=new AlertDialog.Builder(SettingActivity.this);
                dialog2.setTitle("输入城市").setView(editText);
                dialog2.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                prefs.edit().putString("city_name2",editText.getText().toString()).apply();
                                Intent intent=new Intent(SettingActivity.this,WeatherActivity_x.class);
                                startActivity(intent);
                                Toast.makeText(SettingActivity.this, "搜索成功", Toast.LENGTH_SHORT).show();
                            }
                        }).show();
            }
        });



        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //一次性申请3个权限，创建一个list集合，依次判断这3个权限有没被授权，如果没有就添加到list集合中
                //将list转换成数组，再调用ActivityCompat.requestPermissions一次性申请
                List<String> permissionList = new ArrayList<>();
                if (ContextCompat.checkSelfPermission(SettingActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
                }
                if (ContextCompat.checkSelfPermission(SettingActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(Manifest.permission.READ_PHONE_STATE);
                }
                if (ContextCompat.checkSelfPermission(SettingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
                if (!permissionList.isEmpty()) {
                    String [] permissions = permissionList.toArray(new String[permissionList.size()]);
                    ActivityCompat.requestPermissions(SettingActivity.this, permissions, 1);
                } else {
                    requestLocation();
                }

                //Toast.makeText(SettingActivity.this, prefs.getString("guojia",null), Toast.LENGTH_SHORT).show();



            }
        });


    }
    //开始定位
    private void requestLocation() {
        mLocationClient.start();
    }



    //通过循环将申请的每个权限进行判断，如果任何一个权限被拒绝，直接调用finish方法关闭当前程序
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有权限才能使用定位", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                } else {
                    Toast.makeText(this, "发生错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }


    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {

            SharedPreferences prefs_x = PreferenceManager.getDefaultSharedPreferences(SettingActivity.this);

            StringBuilder currentPosition = new StringBuilder();
            currentPosition.append(location.getLongitude()).append(",").append(location.getLatitude());
            prefs_x.edit().putString("city_name2", String.valueOf(currentPosition)).apply();//将定位得到的数据缓存
            mLocationClient.stop();//关闭定位
            Intent intent=new Intent(SettingActivity.this,WeatherActivity_x.class);
            startActivity(intent);


        }

    }
}
