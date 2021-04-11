package com.czdxwx.lbs;


import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;



public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();

    }

    private static Context context;

    public static Context getContext(){
        return context;
    }
}
