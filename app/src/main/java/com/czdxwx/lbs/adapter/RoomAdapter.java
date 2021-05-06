package com.czdxwx.lbs.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.czdxwx.lbs.PushNews.NewsActivity;
import com.czdxwx.lbs.R;
import com.czdxwx.lbs.entity.Data;
import com.czdxwx.lbs.entity.News;
import com.czdxwx.lbs.entity.emptyRoom;

import java.util.ArrayList;
import java.util.List;

public class RoomAdapter extends BaseQuickAdapter<emptyRoom, BaseViewHolder> {
    private List<emptyRoom> roomList =new ArrayList<>();

    public RoomAdapter(List<emptyRoom> roomList) {
        super(R.layout.layout_animation,roomList);
        this.roomList =roomList;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, emptyRoom item) {
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

        helper.setText(R.id.tweetName, item.getName());
        ((TextView) helper.getView(R.id.tweetText)).setText(item.getBuilding());
        ((TextView) helper.getView(R.id.tweetText)).setFocusable(false);
        ((TextView) helper.getView(R.id.tweetText)).setClickable(false);
        ((TextView) helper.getView(R.id.tweetText)).setLongClickable(false);
    }

}

