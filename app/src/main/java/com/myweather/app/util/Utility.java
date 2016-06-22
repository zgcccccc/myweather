package com.myweather.app.util;

import android.text.TextUtils;

import com.myweather.app.db.MyWeatherDB;
import com.myweather.app.model.City;
import com.myweather.app.model.County;
import com.myweather.app.model.Province;

/**
 * Created by Administrator on 2016/6/21.
 */
public class Utility {

    /**
     * 解析并且处理服务器返回的省数据，转为实体类存储到数据库
     */
    public synchronized static boolean handleProvincesResponse(final MyWeatherDB myWeatherDB, final String response) {
        if (!TextUtils.isEmpty(response)) {
            String[] allProvinces = response.split(",");
            if (allProvinces != null && allProvinces.length > 0) {
                for (String p : allProvinces) {
                    String[] array = p.split("\\|");
                    String provinceCode = array[0];
                    String provinceName = array[1];
                    Province province = new Province();
                    province.setProvinceName(provinceName);
                    province.setProvinceCode(provinceCode);
                    myWeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }

    /**
     *解析并且处理服务器返回的城市数据，转为实体类储存到数据库
     */
    public static boolean handleCitiesResponse(final MyWeatherDB myWeatherDB, final String response, final int provinceId) {
        if (!TextUtils.isEmpty(response) && provinceId != 0) {
            String[] allCities = response.split(",");
            if (allCities != null && allCities.length > 0) {
                for (String c : allCities) {
                    String[] array = c.split("\\|");
                    String cityCode = array[0];
                    String cityName = array[1];
                    City city=new City();
                    city.setCityName(cityName);
                    city.setCityCode(cityCode);
                    city.setProvinceId(provinceId);
                    myWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;

    }

    /**
     * 解析并且处理服务器返回的县数据，转为实体类存储到数据库
     */
    public static boolean handleCountiesResponse(final MyWeatherDB myWeatherDB,final String response,final int cityId){
        if(!TextUtils.isEmpty(response)&&cityId!=0){
            String[] allCounties=response.split(",");
            if(allCounties!=null&&allCounties.length>0){
                for(String c:allCounties){
                    String[] array=c.split("\\|");
                    String countyCode=array[0];
                    String countyName=array[1];
                    County county=new County();
                    county.setCountyName(countyName);
                    county.setCountyCode(countyCode);
                    county.setCityId(cityId);
                    myWeatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }
}
