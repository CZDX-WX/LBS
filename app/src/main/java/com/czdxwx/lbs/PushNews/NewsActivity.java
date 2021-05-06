package com.czdxwx.lbs.PushNews;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.czdxwx.lbs.R;
import com.czdxwx.lbs.adapter.NewsAdapter;
import com.czdxwx.lbs.entity.News;
import com.kyleduo.switchbutton.SwitchButton;
import com.xiaomi.mipush.sdk.MiPushClient;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.db.sqlite.DbModel;

import java.util.ArrayList;
import java.util.List;

import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService;

public class NewsActivity extends AppCompatActivity {
    public static FinalDb db;
    private List<News> newsList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private NewsAdapter newsAdapter;
    private String topic = "新闻";
    private boolean isSubscribe = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.color_primary_dark));
        setContentView(R.layout.activity_news);

        //建库
        db = FinalDb.create(this, "LBS");
        mRecyclerView = findViewById(R.id.rv_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //查询数据
        newsList = db.findAll(News.class);
        if (isSubscribe) {
            initAdapter();
        } else {
            newsAdapter.setList(null);
            newsAdapter.setEmptyView(R.layout.empty_view);
        }
        initMenu();
        initView();
    }

    private void initView() {
        ImageView mImgBtn = findViewById(R.id.img_back);
        mImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                finish();
            }
        });
    }

    private void initAdapter() {
        newsAdapter = new NewsAdapter(newsList);
        newsAdapter.setAnimationEnable(true);
        //动画效果
        //AlphaIn:
        //ScaleIn:
        //SlideInBottom:
        //SlideInLeft
        newsAdapter.setAnimationWithDefault(BaseQuickAdapter.AnimationType.SlideInLeft);
        mRecyclerView.setAdapter(newsAdapter);
        //取消动画只执行一次
        newsAdapter.setAnimationFirstOnly(false);

    }

    private void initMenu() {
        //是否订阅
        SwitchButton switchButton = findViewById(R.id.switch_button);
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                if (isChecked) {
                    isSubscribe = true;
                    MiPushClient.subscribe(NewsActivity.this, topic, null);
                    Toast.makeText(NewsActivity.this, "订阅成功", Toast.LENGTH_LONG).show();
                    refreshListView();
                    newsAdapter.setList(newsList);
                } else {
                    isSubscribe = false;
                    MiPushClient.unsubscribe(NewsActivity.this, topic, null);
                    newsAdapter.setList(null);
                    Toast.makeText(NewsActivity.this, "退订成功", Toast.LENGTH_LONG).show();
                    newsAdapter.setEmptyView(R.layout.empty_view);
                }
            }
        });

    }

    //刷新适配器
    public void refreshListView() {
        newsList = db.findAll(News.class);
        if (newsList.isEmpty()) {
            Toast.makeText(this, "查询结果为零", Toast.LENGTH_LONG).show();
        }
        if (newsList.isEmpty()) {
            newsAdapter.setEmptyView(R.layout.empty_data);
        }
        newsAdapter.setList(newsList);
    }

    @Override
    protected void onResume() {
        refreshListView();
        super.onResume();
    }
}