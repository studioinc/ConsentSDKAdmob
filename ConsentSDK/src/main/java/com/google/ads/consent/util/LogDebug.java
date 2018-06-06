package com.google.ads.consent.util;

import android.util.Log;

import com.google.android.ads.consent.BuildConfig;


/**
 * Created by Develop on 11/21/2017.
 */

public final class LogDebug {

    //TODO Logcat -> VERBOSE
    public static void v(String tag, String msg)
    {
        if (BuildConfig.DEBUG){
            Log.v(tag, msg);
        }
    }

    //TODO Logcat -> DEBUG
    public static void d(String tag, String msg)
    {
        if (BuildConfig.DEBUG){
            Log.d(tag, msg);
        }
    }

    //TODO Logcat -> INFO
    public static void i(String tag, String msg)
    {
        if (BuildConfig.DEBUG){
            Log.i(tag, msg);
        }
    }

    //TODO Logcat -> WARN
    public static void w(String tag, String msg)
    {
        if (BuildConfig.DEBUG){
            Log.w(tag, msg);
        }
    }

    //TODO Logcat -> ERROR
    public static void e(String tag, String msg)
    {
        if (BuildConfig.DEBUG){
            Log.e(tag, msg);
        }
    }

    public static void printStackTrace(Exception e) {
        e.printStackTrace();
    }
}

