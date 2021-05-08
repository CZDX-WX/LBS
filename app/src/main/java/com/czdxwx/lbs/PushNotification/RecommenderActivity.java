package com.czdxwx.lbs.PushNotification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.ajguan.library.EasyRefreshLayout;
import com.ajguan.library.LoadModel;
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

        //获取数据库实例
        db = FinalDb.create(this, "LBS");

        mRecyclerView = findViewById(R.id.rv_list);
        //固定尺寸，防止冲突
        mRecyclerView.setHasFixedSize(true);
        //设置布局管理器
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //查询数据
        dataList = db.findAll(Data.class);
        //判断是否订阅
        if (isSubscribe) {
            //初始化适配器
            initAdapter();
        } else {
            dataAdapter.setList(null);
            dataAdapter.setEmptyView(R.layout.empty_view);
        }
        //初始化菜单
        initMenu();
        //开源框架
        EasyRefreshLayout refreshLayout=findViewById(R.id.easylayout);
        //注册事件
        refreshLayout.addEasyEvent(new EasyRefreshLayout.EasyEvent() {

            @Override
            public void onLoadMore() {

            }

            @Override
            public void onRefreshing() {
                //延迟线程
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //取数据
                        dataList = db.findAll(Data.class);
                        //为空则放入空视图
                        if (dataList.isEmpty()) {
                            dataAdapter.setEmptyView(R.layout.empty_data);
                        }
                        dataAdapter.setList(dataList);
                        //刷新完毕
                        refreshLayout.refreshComplete();
                    }
                }, 1000);
            }
        });
        //取消上拉加载更多
        refreshLayout.setLoadMoreModel(LoadModel.NONE);
    }

    //初始化适配器配置
    private void initAdapter() {
        dataAdapter = new DataAdapter(dataList);
        //设置滑动动画
        dataAdapter.setAnimationEnable(true);
        //动画效果
        //AlphaIn:
        //ScaleIn:
        //SlideInBottom:
        //SlideInLeft
        dataAdapter.setAnimationWithDefault(BaseQuickAdapter.AnimationType.SlideInLeft);
        //放适配器
        mRecyclerView.setAdapter(dataAdapter);
        //取消动画只执行一次
        dataAdapter.setAnimationFirstOnly(false);
    }

    private void initMenu() {
        //返回按钮，贴个图
        ImageView mImgBtn = findViewById(R.id.img_back);
        //返回按钮点击事件
        mImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                finish();
            }
        });

        //是否订阅按钮
        SwitchButton switchButton = findViewById(R.id.switch_button);
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                if (isChecked) {
                    isSubscribe = true;
                    //小米推送 订阅
                    MiPushClient.subscribe(RecommenderActivity.this, Aisas, null);
                    Toast.makeText(RecommenderActivity.this, "订阅成功", Toast.LENGTH_LONG).show();
                    //刷新一下列表
                    refreshListView();
                    dataAdapter.setList(dataList);
                } else {
                    isSubscribe = false;
                    //取消订阅，收不到以标签方式发送的推送
                    MiPushClient.unsubscribe(RecommenderActivity.this, Aisas, null);
                    //放进去空数据
                    dataAdapter.setList(null);
                    Toast.makeText(RecommenderActivity.this, "退订成功", Toast.LENGTH_LONG).show();
                    //放个空视图
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
    //每次进来刷新一下
    protected void onResume() {
        refreshListView();
        super.onResume();
    }
}