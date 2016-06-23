package com.myweather.app.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;


import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.myweather.app.R;
import com.myweather.app.db.MyWeatherDB;
import com.myweather.app.model.City;
import com.myweather.app.model.County;
import com.myweather.app.model.Province;
import com.myweather.app.util.HttpCallbackListener;
import com.myweather.app.util.HttpUtil;
import com.myweather.app.util.Utility;

import java.util.ArrayList;
import java.util.List;

public class ChooseAreaActivity extends BaseActivity {

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private List<String> dataList = new ArrayList<String>();
    private MyWeatherDB myWeatherDB;
    private List<Province> provincesList;
    private List<City> citiesList;
    private List<County> countiesList;
    private Province selectedProvince;
    private City selectedCity;
    private County selectedCounty;
    private int currentLevel;
    private HttpUtil httpUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_area);
        titleText = (TextView) findViewById(R.id.title_text);
        listView = (ListView) findViewById(R.id.list_view);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        getSupportActionBar().hide();
        listView.setAdapter(arrayAdapter);
        myWeatherDB = MyWeatherDB.getInstance(this);
        httpUtil=HttpUtil.getHttpUtil(this);
        //listView点击事件，判断状态层，查询相应数据
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentLevel==LEVEL_PROVINCE){
                    //赋值当前点击的省实体类
                    selectedProvince=provincesList.get(position);
                    queryCities();
                }else if(currentLevel==LEVEL_CITY){
                    //赋值当前点击的城市实体类
                    selectedCity=citiesList.get(position);
                    queryCounties();
                }else if(currentLevel==LEVEL_COUNTY){
                    selectedCounty=countiesList.get(position);
                    Intent intent=new Intent(ChooseAreaActivity.this,WeatherActivity.class);
                    intent.putExtra("countyCode",selectedCounty.getCountyCode());
                    startActivity(intent);
                }
            }
        });

        queryProvinces();

    }

    /**
     * 从数据库中查询出所有省的数据，并且填充到listView中显示出来
     */
    private void queryProvinces() {
        provincesList = myWeatherDB.loadProvince();
        dataList.clear();
        //如果数据库中有数据，则直接读取
        if (provincesList.size() > 0) {
            for (Province p : provincesList) {
                dataList.add(p.getProvinceName());
            }
            listView.setSelection(0);
            titleText.setText("中国");
            arrayAdapter.notifyDataSetChanged();
            currentLevel = LEVEL_PROVINCE;
        }
        //如果没有，从服务器上读取储存到本地数据库
        else {
            queryFromServer(null,"province");
        }
    }

    /**
     * 查询选中的省下的城市数据，并且填充到listView中显示出来
     */
    private void queryCities() {
        citiesList = myWeatherDB.loadCities(selectedProvince.getId());
        dataList.clear();
        //如果数据库中有数据，则直接读取
        if (citiesList.size() > 0) {
            for (City c : citiesList) {
                dataList.add(c.getCityName());
            }
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            arrayAdapter.notifyDataSetChanged();
            currentLevel = LEVEL_CITY;
        }
        //如果没有，从服务器上读取储存到本地数据库
        else {
            queryFromServer(selectedProvince.getProvinceCode(), "city");

        }
    }

    /**
     * 查询选中的城市下的县数据，并且填充到listView中显示出来
     */
    private void queryCounties() {
        countiesList = myWeatherDB.loadCountice(selectedCity.getId());
        dataList.clear();
        //如果数据库中有数据，则直接读取
        if (countiesList.size() > 0) {
            for (County c : countiesList) {
                dataList.add(c.getCountyName());
            }
            listView.setSelection(0);
            titleText.setText(selectedCity.getCityName());
            arrayAdapter.notifyDataSetChanged();
            currentLevel = LEVEL_COUNTY;
        }
        //如果没有，从服务器上读取储存到本地数据库
        else {
            queryFromServer(selectedCity.getCityCode(),"county");
        }
    }

    /**
     * 获取服务器省市县数据，并且解析存储到本地数据库
     * @param code 上级code，查询省则传入null
     * @param type 查询类型 “province：查询省数据，city：查询城市数据，county：查询县城数据”
     */
    private void queryFromServer(final String code, final String type) {

        String address;
        if (TextUtils.isEmpty(code)) {
            address = "http://www.weather.com.cn/data/list3/city.xml";
        } else {
            address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
        }
        showProgressDialog();
        httpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if ("province".equals(type)) {
                    //解析并存储省数据
                    result = Utility.handleProvincesResponse(myWeatherDB, response);
                } else if ("city".equals(type)) {
                    //解析并存储城市数据
                    result = Utility.handleCitiesResponse(myWeatherDB, response, selectedProvince.getId());
                } else if ("county".equals(type)) {
                    //解析并存储县城数据
                    result = Utility.handleCountiesResponse(myWeatherDB, response, selectedCity.getId());
                }
                if (result) {
                    //如果解析并且储存成功，关闭进度条，查询数据显示到listView
                    closeProgressDialog();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if ("province".equals(type)) {
                                //显示省数据
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                //显示城市数据
                                queryCities();
                            } else if ("county".equals(type)) {
                                //显示县城数据
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(VolleyError e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this, "加载失败，请稍后重试...", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }


    /**
     * 显示进度条对话框
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度条对话框
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /**
     * 返回键事件，判断当前层做相应的操作
     */
    @Override
    public void onBackPressed() {

        if(currentLevel==LEVEL_COUNTY){
            queryCities();
        }else if(currentLevel==LEVEL_CITY){
            queryProvinces();
        }else {
            finish();
        }
    }

}
