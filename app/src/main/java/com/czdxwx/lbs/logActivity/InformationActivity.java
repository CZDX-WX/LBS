package com.czdxwx.lbs.logActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.czdxwx.lbs.MyApplication;
import com.czdxwx.lbs.R;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class InformationActivity extends AppCompatActivity {
    //记录列表
    public static List<String> logList = new CopyOnWriteArrayList<String>();
    //显示记录视图
    private TextView mLogView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        mLogView = (TextView) findViewById(R.id.log);
    }
    @Override
    protected void onResume() {
        super.onResume();
        refreshLogInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.setMainActivity(null);
    }

    public void refreshLogInfo() {
        String AllLog = "";
        for (String log : logList) {
            AllLog = AllLog + log + "\n\n";
        }
        mLogView.setText(AllLog);
    }
}