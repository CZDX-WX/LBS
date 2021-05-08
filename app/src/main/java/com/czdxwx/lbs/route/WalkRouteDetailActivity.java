package com.czdxwx.lbs.route;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.services.route.WalkPath;
import com.czdxwx.lbs.R;
import com.czdxwx.lbs.adapter.WalkSegmentListAdapter;
import com.czdxwx.lbs.utils.AMapUtil;


public class WalkRouteDetailActivity extends Activity {

	//路径对象
	private WalkPath mWalkPath;
	//路径规划对象
	private TextView mTitle,mTitleWalkRoute;

	//步行细节列表
	private ListView mWalkSegmentList;

	//列表视图适配器
	private WalkSegmentListAdapter mWalkSegmentListAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route_detail);
		//获取传入数据
		getIntentData();

		mTitle = (TextView) findViewById(R.id.title_center);
		mTitle.setText("步行路线详情");
		mTitleWalkRoute = (TextView) findViewById(R.id.firstline);

		//所需时间，路径对象的方法直接get
		String dur = AMapUtil.getFriendlyTime((int) mWalkPath.getDuration());
		//所需距离，同上
		String dis = AMapUtil.getFriendlyLength((int) mWalkPath.getDistance());

		mTitleWalkRoute.setText(dur + "(" + dis + ")");
		mWalkSegmentList = (ListView) findViewById(R.id.bus_segment_list);

		//getsteps方法返回list
		mWalkSegmentListAdapter = new WalkSegmentListAdapter(this.getApplicationContext(), mWalkPath.getSteps());
		mWalkSegmentList.setAdapter(mWalkSegmentListAdapter);

	}

	//获取传入数据
	private void getIntentData() {
		Intent intent = getIntent();
		if (intent == null) {
			return;
		}
		mWalkPath = intent.getParcelableExtra("walk_path");
	}

	public void onBackClick(View view) {
		this.finish();
	}

}
