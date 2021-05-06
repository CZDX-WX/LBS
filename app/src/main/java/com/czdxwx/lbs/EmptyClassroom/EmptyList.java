package com.czdxwx.lbs.EmptyClassroom;

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
import com.czdxwx.lbs.adapter.NewsAdapter;
import com.czdxwx.lbs.adapter.RoomAdapter;
import com.czdxwx.lbs.entity.News;
import com.czdxwx.lbs.entity.emptyRoom;
import com.kyleduo.switchbutton.SwitchButton;
import com.xiaomi.mipush.sdk.MiPushClient;

import net.tsz.afinal.FinalDb;

import java.util.ArrayList;
import java.util.List;

public class EmptyList extends AppCompatActivity {
    private FinalDb db;
    private List<emptyRoom> roomList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private RoomAdapter roomAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.color_primary_dark));
        setContentView(R.layout.activity_empty_list);

        //获得数据库对象
        db = FinalDb.create(this, "LBS");
        //换标题栏
        setSupportActionBar(findViewById(R.id.toolbar));
        Bundle bundle = getIntent().getExtras();

        roomList = db.findAllByWhere(emptyRoom.class, String.format("school='%s'and building='%s'and week='%s'and weekday='%s'and number='%s';", bundle.get("school"), bundle.get("building"), bundle.get("week"), bundle.get("weekday"), bundle.get("number")));
        mRecyclerView = findViewById(R.id.rv_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        initAdapter();


    }


    private void initAdapter() {
        roomAdapter = new RoomAdapter(roomList);
            roomAdapter.setAnimationEnable(true);
            //动画效果
            //AlphaIn:
            //ScaleIn:
            //SlideInBottom:
            //SlideInLeft
            roomAdapter.setAnimationWithDefault(BaseQuickAdapter.AnimationType.SlideInLeft);
            mRecyclerView.setAdapter(roomAdapter);
            //取消动画只执行一次
            roomAdapter.setAnimationFirstOnly(false);
        if (roomList.isEmpty()) {
            roomAdapter.setList(null);
            roomAdapter.setEmptyView(R.layout.empty_data);
        }else{
            roomAdapter.setList(roomList);
        }
    }

}