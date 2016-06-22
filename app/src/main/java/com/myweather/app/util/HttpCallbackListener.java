package com.myweather.app.util;

import com.android.volley.VolleyError;

/**
 * Created by Administrator on 2016/6/20.
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(VolleyError e);
}
