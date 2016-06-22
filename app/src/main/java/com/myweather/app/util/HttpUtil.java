package com.myweather.app.util;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.myweather.app.MyApplication;

/**
 * Created by Administrator on 2016/6/20.
 */
public class HttpUtil {
    private static RequestQueue requestQueue;
    private static HttpUtil httpUtil;

    private HttpUtil(Context context){
        requestQueue=Volley.newRequestQueue(context);
    }
    public  static void sendHttpRequest(final String address,final HttpCallbackListener listener){
        httpUtil= new HttpUtil(MyApplication.getContext());
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
}
