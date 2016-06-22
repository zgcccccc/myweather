package com.myweather.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.myweather.app.model.City;
import com.myweather.app.model.County;
import com.myweather.app.model.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/20.
 */
public class MyWeatherDB {
    private static final String DB_NAME = "my_weather.db";
    public static final int VERSION = 1;
    private static MyWeatherDB myWeatherDB;
    private static SQLiteDatabase db;

    //构建函数私有化
    private MyWeatherDB(Context context) {
        MyWeatherOpenHelper database = new MyWeatherOpenHelper(context, DB_NAME, null, VERSION);
        db = database.getWritableDatabase();
    }

    //将构造对象赋值且返回
    public synchronized static MyWeatherDB getInstance(Context context) {
        if (myWeatherDB == null) {
            myWeatherDB = new MyWeatherDB(context);
        }

        return myWeatherDB;
    }

    /**
     * 插入一条省数据
     *
     * @param 省实体类
     */
    public void saveProvince(Province province) {

     db.execSQL("insert into province (province_name,province_code)values(?,?)",new String[]{province.getProvinceName(),
                province.getProvinceCode()});
    }

    /**
     * 查询所有省数据
     *
     * @return 返回省数据集合 List<Province>
     */
    public List<Province> loadProvince() {
        List<Province> provinceList = new ArrayList<Province>();
        Cursor cursor = db.query("province", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {

                int provinceId = cursor.getInt(cursor.getColumnIndex("id"));
                String provinceName = cursor.getString(cursor.getColumnIndex("province_name"));
                String provinceCode = cursor.getString(cursor.getColumnIndex("province_code"));
                Province province = new Province(provinceId, provinceName, provinceCode);
                provinceList.add(province);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return provinceList;

    }

    /**
     * 插入一条城市数据
     *
     * @param 城市实体类
     */
    public void saveCity(City city) {

        db.execSQL("insert into city (city_name,city_code,province_id) values(?,?,?)",
                new String[]{city.getCityName(), city.getCityCode(), String.valueOf(city.getProvinceId())});
    }

    /**
     * 根据传进的省Id，查询该省下的城市数据
     * @param provinceId 省Id
     * @return 城市数据集合
     */
    public List<City> loadCities(int provinceId) {
        List<City> citiesList = new ArrayList<City>();
        Cursor cursor = db.query("city", null, "province_id=?", new String[]{String.valueOf(provinceId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                int cityId = cursor.getInt(cursor.getColumnIndex("id"));
                String cityName = cursor.getString(cursor.getColumnIndex("city_name"));
                String cityCode = cursor.getString(cursor.getColumnIndex("city_code"));
                int _provinceId = cursor.getInt(cursor.getColumnIndex("province_id"));
                City city = new City(cityId, cityName, cityCode, _provinceId);
                citiesList.add(city);


            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return citiesList;

    }

    /**
     * 插入一条县数据
     *
     * @param 县实体类
     */
    public void saveCounty(County county) {

        db.execSQL("insert into county (county_name,county_code,city_id)values(?,?,?)", new String[]{
                county.getCountyName(), county.getCountyCode(), String.valueOf(county.getCityId())
        });
    }

    /**
     *根据传进的城市Id，查询该城市下的县城数据
     * @param cityId 城市Id
     * @return 县城数据集合
     */
    public List<County> loadCountice(int cityId) {
        List<County> countiesList = new ArrayList<County>();
        Cursor cursor = db.query("county", null, "city_id=?", new String[]{String.valueOf(cityId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String countyName = cursor.getString(cursor.getColumnIndex("county_name"));
                String countyCode = cursor.getString(cursor.getColumnIndex("county_code"));
                int _cityId = cursor.getInt(cursor.getColumnIndex("city_id"));

                County county = new County(id, countyName, countyCode, _cityId);
                countiesList.add(county);
            } while (cursor.moveToNext());

        }
        if (cursor != null) {
            cursor.close();
        }
        return countiesList;
    }


}
