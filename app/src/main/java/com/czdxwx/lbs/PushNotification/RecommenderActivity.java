package com.czdxwx.lbs.PushNotification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.czdxwx.lbs.PushNews.NewsActivity;
import com.czdxwx.lbs.R;
import com.czdxwx.lbs.adapter.DataAdapter;
import com.czdxwx.lbs.adapter.NewsAdapter;
import com.czdxwx.lbs.entity.Data;
import com.czdxwx.lbs.entity.News;
import com.kyleduo.switchbutton.SwitchButton;
import com.xiaomi.mipush.sdk.MiPushClient;

import net.tsz.afinal.FinalDb;

import java.util.ArrayList;
import java.util.List;

import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService;

public class RecommenderActivity extends AppCompatActivity {
    public static FinalDb db;
    private List<Data> dataList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private DataAdapter dataAdapter;
    private String Aisas = "韦欣";
    private boolean isSubscribe = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.color_primary_dark));
        setContentView(R.layout.activity_news);
        //连接SQLite Studio
        SQLiteStudioService.instance().start(this);
        //建库
        db = FinalDb.create(this, "LBS");
        mRecyclerView = findViewById(R.id.rv_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //查询数据
        dataList = db.findAll(Data.class);
        if (isSubscribe) {
            initAdapter();
        } else {
            dataAdapter.setList(null);
            dataAdapter.setEmptyView(R.layout.empty_view);
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
        dataAdapter = new DataAdapter(dataList);
        dataAdapter.setAnimationEnable(true);
        dataAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {

            }
        });
        //动画效果
        //AlphaIn:
        //ScaleIn:
        //SlideInBottom:
        //SlideInLeft
        dataAdapter.setAnimationWithDefault(BaseQuickAdapter.AnimationType.SlideInLeft);
        mRecyclerView.setAdapter(dataAdapter);
        //取消动画只执行一次
        dataAdapter.setAnimationFirstOnly(false);
    }

    private void initMenu() {
        //是否订阅
        SwitchButton switchButton = findViewById(R.id.switch_button);
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                if (isChecked) {
                    isSubscribe = true;
                    MiPushClient.subscribe(RecommenderActivity.this, Aisas, null);
                    Toast.makeText(RecommenderActivity.this, "订阅成功", Toast.LENGTH_LONG).show();
                    refreshListView();
                    dataAdapter.setList(dataList);
                } else {
                    isSubscribe = false;
                    MiPushClient.unsubscribe(RecommenderActivity.this, Aisas, null);
                    dataAdapter.setList(null);
                    Toast.makeText(RecommenderActivity.this, "退订成功", Toast.LENGTH_LONG).show();
                    dataAdapter.setEmptyView(R.layout.empty_view);
                }
            }
        });

    }

    //刷新适配器
    public void refreshListView() {
        dataList = db.findAll(Data.class);
        if (dataList.isEmpty()) {
            Toast.makeText(this, "查询结果为零", Toast.LENGTH_LONG).show();
        }
        if (dataList.isEmpty()) {
            dataAdapter.setEmptyView(R.layout.empty_data);
        }
        dataAdapter.setList(dataList);
    }

    @Override
    protected void onResume() {
        refreshListView();
        super.onResume();
    }
}