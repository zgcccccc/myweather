package com.myweather.app.activity;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.myweather.app.R;
import com.myweather.app.model.WeatherInfo;
import com.myweather.app.util.HttpCallbackListener;
import com.myweather.app.util.HttpUtil;
import com.myweather.app.util.Utility;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WeatherActivity extends BaseActivity {

    private TextView city_name;
    private TextView publish_text;
    private LinearLayout weather_info;
    private TextView current_data;
    private TextView weather_desp;
    private TextView temp1;
    private TextView temp2;
    private HttpUtil httpUtil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        //初始化控件
        city_name=(TextView)findViewById(R.id.city_name);
        publish_text=(TextView)findViewById(R.id.publish_text);
        weather_info=(LinearLayout)findViewById(R.id.weather_info);
        current_data=(TextView)findViewById(R.id.current_data);
        weather_desp=(TextView)findViewById(R.id.weather_desp);
        temp1=(TextView)findViewById(R.id.temp1);
        temp2=(TextView)findViewById(R.id.temp2);
        httpUtil=HttpUtil.getHttpUtil(this);


        String countyCode=getIntent().getStringExtra("countyCode");
        if(!TextUtils.isEmpty(countyCode)){
            publish_text.setText("天气信息正在同步...");
            queryWeatherCode(countyCode);

        }else {
            showWeatherInfo();
        }

    }

    /**
     * 查询该地区的天气代码
     * @param countyCode
     */
    private void queryWeatherCode(String countyCode){
        String address="http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
        queryFromServer(address,"WeatherCode");
    }

    private void queryWeatherInfo(String weatherCode){
        String address="http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
        queryFromServer(address,"WeatherInfo");
    }

    private void queryFromServer(String address, final String type){
        httpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                if(type.equals("WeatherCode")){
                    String[] array=response.split("\\|");
                    String weatherCode=array[1];
                    queryWeatherInfo(weatherCode);

                }else if(type.equals("WeatherInfo")){
                    Utility.handleWeatherResponse(response);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showWeatherInfo();
                            }
                        });


                }else {
                    return;
                }
            }

            @Override
            public void onError(VolleyError e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "同步更新失败...", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void showWeatherInfo(){
        Gson gson=new Gson();
        SharedPreferences pref= PreferenceManager.getDefaultSharedPreferences(this);
        DateFormat format=new SimpleDateFormat("yyyy年MM月dd日");
        String weatherJson=pref.getString("weatherJson", "");
        WeatherInfo weatherInfo=gson.fromJson(weatherJson,WeatherInfo.class);
        city_name.setText(weatherInfo.getCity());
        publish_text.setText("今天"+weatherInfo.getPtime()+"发布");
        current_data.setText(format.format(new Date()));
        weather_desp.setText(weatherInfo.getWeather());
        temp1.setText(weatherInfo.getTemp1());
        temp2.setText(weatherInfo.getTemp2());
        weather_info.setVisibility(View.VISIBLE);
    }
}
