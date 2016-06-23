package com.myweather.app.util;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.myweather.app.MyApplication;
import com.myweather.app.request.MyStringRequest;

/**
 * Created by Administrator on 2016/6/20.
 */
public class HttpUtil {
    private static RequestQueue requestQueue;
    private static HttpUtil httpUtil;

    private HttpUtil(Context context){
        requestQueue=Volley.newRequestQueue(context);
    }

    public synchronized static HttpUtil getHttpUtil(Context context){
        if(httpUtil==null){
            httpUtil= new HttpUtil(context);
        }
        return httpUtil;

    }
    public  void sendHttpRequest(final String address,final HttpCallbackListener listener){

        StringRequest stringRequest=new MyStringRequest(address, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                listener.onFinish(s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                listener.onError(volleyError);
            }
        });
        httpUtil.requestQueue.add(stringRequest);
    }

    public static void sendHttpRequestByJsonObject(final  String address,final HttpCallbackListener listener){


    }
}
