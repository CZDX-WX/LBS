package com.czdxwx.lbs;


import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.czdxwx.lbs.logActivity.InformationActivity;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;


public class MyApplication extends Application {
    //上下文
    private static Context context;

    // 此TAG在adb logcat中检索自己所需要的信息， 只需在命令行终端输入 adb logcat | grep
    // com.xiaomi.mipushdemo
    public static final String TAG = "Test";

    private static MyHandler sHandler = null;
    private static InformationActivity sActivity = null;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        LoggerInterface newLogger = new LoggerInterface() {
            @Override
            public void setTag(String tag) {
            }

            @Override
            public void log(String content, Throwable t) {
                Log.d(TAG, content, t);
            }

            @Override
            public void log(String content) {
                Log.d(TAG, content);
            }
        };
        Logger.setLogger(this, newLogger);
        if (sHandler == null) {
            sHandler = new MyHandler(getApplicationContext());
        }
    }


    public static MyHandler getHandler() {
        return sHandler;
    }

    public static void setMainActivity(InformationActivity activity) {
        sActivity = activity;
    }

    public static class MyHandler extends Handler {

        private Context context;

        public MyHandler(Context context) {
            this.context = context;
        }

        @Override
        public void handleMessage(Message msg) {
            String s = (String) msg.obj;
            if (sActivity != null) {
                //刷新日志
                sActivity.refreshLogInfo();
            }
            if (!TextUtils.isEmpty(s)) {
                Toast.makeText(context, s, Toast.LENGTH_LONG).show();
            }
        }
    }

    //获取全局上下文
    public static Context getContext() {
        return context;
    }
}
