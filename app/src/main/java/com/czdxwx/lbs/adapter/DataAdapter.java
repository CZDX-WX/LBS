package com.czdxwx.lbs.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.czdxwx.lbs.R;
import com.czdxwx.lbs.entity.Data;
import com.czdxwx.lbs.PushNews.NewsActivity;

import java.util.ArrayList;
import java.util.List;


public class DataAdapter extends BaseQuickAdapter<Data, BaseViewHolder> {
private List<Data> dataList=new ArrayList<>();
    public DataAdapter(List<Data> dataList) {
        super(R.layout.layout_animation,dataList);
        this.dataList=dataList;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, Data item) {
        //每三行循环一次壁纸
        switch (helper.getLayoutPosition() % 3) {
            case 0:
                helper.setImageResource(R.id.img, R.mipmap.animation_img1);
                break;
            case 1:
                helper.setImageResource(R.id.img, R.mipmap.animation_img2);
                break;
            case 2:
                helper.setImageResource(R.id.img, R.mipmap.animation_img3);
                break;
            default:
                break;
        }

        helper.setText(R.id.tweetName, item.getTweetName());
        ((TextView) helper.getView(R.id.tweetText)).setText(item.getTweetText());
        ((TextView) helper.getView(R.id.tweetText)).setFocusable(false);
        ((TextView) helper.getView(R.id.tweetText)).setClickable(false);
        ((TextView) helper.getView(R.id.tweetText)).setLongClickable(false);
        helper.getView(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewsActivity.db.delete(item);
                dataList.remove(item);
                setList(dataList);
            }
        });
    }

}
