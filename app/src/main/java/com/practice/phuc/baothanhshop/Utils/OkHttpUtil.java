package com.practice.phuc.baothanhshop.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpUtil {
    public static String SHARED_PRE_SERVER = "server";
    public static String SHARED_PRE_KEY_SERVER_PORT = "server_port";
    public static String SHARED_PRE_KEY_SERVER_ADDRESS = "server_address";

    public static String DEFAULT_SERVER_PORT = "8082";
    public static String DEFAILT_SERVER_ADDRESS = "192.168.1.106";

    public static Response makeRequest(String url, boolean isPostMethod, RequestBody requestBody) {
        final OkHttpClient okHttpClient = new OkHttpClient();
        Request request = null;

        if (requestBody == null)
            requestBody = RequestBody.create(
                    MediaType.parse("application/xml; charset=utf-8"), ""
            );
        try {
            if (isPostMethod) {
                request = new Request.Builder().url(url).post(requestBody).build();
            } else {
                request = new Request.Builder().url(url).get().build();
            }
            return okHttpClient.newCall(request).execute();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("DEBUG", "Đảm bảo tường lửa đã cho phép server mở cổng");
            return null;
        }
    }

    public static String getServerPort(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SHARED_PRE_SERVER, Context.MODE_PRIVATE);
        return sp.getString(SHARED_PRE_KEY_SERVER_PORT, null);
    }

    public static String getServerAddress(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SHARED_PRE_SERVER, Context.MODE_PRIVATE);
        return sp.getString(SHARED_PRE_KEY_SERVER_ADDRESS, null);
    }

    public static String getLoadProductApiUrl(String serverAddr, String port, String productCode) {
        return "http://" + serverAddr + ":" + port + "/api/product/" + productCode;
    }

    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;

    /* Status response code */
    public static final int OK = 200;
    public static final int NOT_FOUND = 404;
    public static final int BAD_REQUEST = 400;

    public static int getConnectivityStatus(Context context) {
        if (context == null) return TYPE_WIFI;

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }
}
