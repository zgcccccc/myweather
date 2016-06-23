package com.myweather.app.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/6/22.
 */
public class WeatherInfo implements Serializable {

    private static final long serialVersionUID=1L;
    String city;
    String cityid;
    String temp1;
    String temp2;
    String weather;
    String img1;
    String img2;
    String ptime;

    public void setCity(String city) {
        this.city = city;
    }

    public void setCityid(String cityid) {
        this.cityid = cityid;
    }

    public void setTemp1(String temp1) {
        this.temp1 = temp1;
    }

    public void setTemp2(String temp2) {
        this.temp2 = temp2;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public void setImg1(String img1) {
        this.img1 = img1;
    }

    public void setImg2(String img2) {
        this.img2 = img2;
    }

    public void setPtime(String ptime) {
        this.ptime = ptime;
    }

    public String getPtime() {
        return ptime;
    }

    public String getCityid() {
        return cityid;
    }

    public String getTemp1() {
        return temp1;
    }

    public String getTemp2() {
        return temp2;
    }

    public String getWeather() {
        return weather;
    }

    public String getImg1() {
        return img1;
    }

    public String getImg2() {
        return img2;
    }

    public String getCity() {
        return city;
    }
}
