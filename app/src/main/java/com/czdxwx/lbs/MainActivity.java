package com.czdxwx.lbs;


import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.ScaleAnimation;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.czdxwx.lbs.PushNotification.RecommenderActivity;
import com.czdxwx.lbs.overlay.WalkRouteOverlay;
import com.czdxwx.lbs.route.WalkRouteDetailActivity;
import com.czdxwx.lbs.utils.AMapUtil;
import com.czdxwx.lbs.utils.ToastUtil;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.heinrichreimersoftware.materialdrawer.DrawerView;
import com.heinrichreimersoftware.materialdrawer.structure.DrawerHeaderItem;
import com.heinrichreimersoftware.materialdrawer.structure.DrawerItem;
import com.heinrichreimersoftware.materialdrawer.structure.DrawerProfile;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;

import net.tsz.afinal.FinalDb;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;

import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService;

import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;
import static com.czdxwx.lbs.MyApplication.getContext;

public class MainActivity extends AppCompatActivity implements AMap.OnMyLocationChangeListener, AMap.OnMapLongClickListener,
        AMap.OnMarkerClickListener, AMap.OnInfoWindowClickListener, RouteSearch.OnRouteSearchListener {

    //afinal数据库对象
    public static FinalDb db;

    //Logcat TAG
    private String TAG = "test";

    /*
**************小米推送*****************
 */

    //小米推送别名
    private String alias = "韦欣";
    //小米推送订阅主题
    private String topic = "新闻";


    // user your appid the key.
    private static final String APP_ID = "2882303761519894315";
    // user your appid the key.
    private static final String APP_KEY = "5391989490315";

    /*
    ***********悬浮按钮布局***************
     */

    //悬浮框按钮
    private FloatingActionMenu menuGrey;
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    private FloatingActionButton fab3;
    private FloatingActionButton fab4;
    private FloatingActionButton fab5;
    //动画处理器
    private final Handler mUiHandler = new Handler();
    //toolbar
    private Toolbar toolbar;
    //抽屉布局
    private DrawerView drawer;
    private ActionBarDrawerToggle drawerToggle;

    /*
    *********************地图******************
     */

    private MapView mapView;
    //地图
    private AMap aMap;
    //上下文
    private Context mContext;
    //定位
    private MyLocationStyle myLocationStyle;
    // 西南坐标(纬度，经度)
    private static final LatLng southwestLatLng = new LatLng(31.678003, 119.943828);
    // 东北坐标
    private static final LatLng northeastLatLng = new LatLng(31.690411, 119.968637);


    /*
    路径规划
     */

    private RelativeLayout mBottomLayout;
    //底部文字描述
    private TextView mRotueTimeDes, mRouteDetailDes;
    //路径规划
    private RouteSearch mRouteSearch;
    //步行结果回调
    private WalkRouteResult mWalkRouteResult;
    //标记点
    private Marker marker = null;
    //起点
    private LatLonPoint mStartPoint = null;
    //终点
    private LatLonPoint mEndPoint = null;
    //步行代号
    private final int ROUTE_TYPE_WALK = 3;
    // 搜索时进度条
    private ProgressDialog progDialog = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //连接SQLite Studio
        SQLiteStudioService.instance().start(this);
        //建库
        db = FinalDb.create(this, "LBS");

        mContext = this.getApplicationContext();
        mapView = findViewById(R.id.map);
        //重写地图
        mapView.onCreate(savedInstanceState);

        //注册小米推送，订阅新闻topic
        initPush();
        //初始化地图
        initMap();
        //初始化悬浮框
        initFab();
        //初始化抽屉布局
        initDrawer();


    }

    private void initPush() {
        // 注册push服务，注册成功后会向MessageReceiver发送广播
        // 可以从MessageReceiver的onCommandResult方法中MiPushCommandMessage对象参数中获取注册信息
        if (shouldInit()) {
            MiPushClient.registerPush(this, APP_ID, APP_KEY);
        }
        //开发者可以为指定用户设置别名，然后给这个别名推送消息，效果等同于给RegId推送消息。
        //说明：每个App单台设备可设置的alias个数为30个，如果超过了对应上限数，则新设置的alias会覆盖最早设置的alias。
        MiPushClient.setAlias(this, alias, null);
        //为某个用户设置订阅主题（Topic）；根据用户订阅的不同主题，开发者可以根据订阅的主题实现分组群发。
        //说明：每个App单台设备可设置的Topic个数为30个，如果超过了对应上限数，则新设置的Topic会覆盖最早设置的Topic。
        MiPushClient.subscribe(this, topic, null);

    }

    //判断是否应该注册
    private boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    //初始化抽屉
    private void initDrawer() {
        toolbar = findViewById(R.id.toolbar);
        //换标题栏
        setSupportActionBar(toolbar);

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawer = findViewById(R.id.drawer);
        //抽屉开关
        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        ) {
            //抽屉关
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu();
            }
            //抽屉开
            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
            }
        };

        drawerLayout.setStatusBarBackgroundColor(ContextCompat.getColor(this, R.color.color_primary_dark));

        drawerLayout.addDrawerListener(drawerToggle);
        //默认关闭
        drawerLayout.closeDrawer(drawer);

        //第一个item:活动推荐
        drawer.addItem(new DrawerItem().setImage(ContextCompat.getDrawable(this, R.drawable.notice))
                .setTextPrimary(getString(R.string.first_item))
                .setTextSecondary(getString(R.string.first_information)));

        //第二个item:空教室查询
        drawer.addItem(new DrawerItem().setRoundedImage((BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.search))
                .setTextPrimary(getString(R.string.second_item))
                .setTextSecondary(getString(R.string.second_information))
        );


        //第三个item:校园新闻
        drawer.addItem(new DrawerItem().setRoundedImage((BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.news))
                .setTextPrimary(getString(R.string.third_item))
                .setTextSecondary(getString(R.string.third_information))
        );

        //进入相关页面
        drawer.setOnItemClickListener(new DrawerItem.OnItemClickListener() {
            @Override
            public void onClick(DrawerItem item, long id, int position) {
                drawer.selectItem(position);
                switch (position) {
                    case 0:
                        //跳转到活动推荐页面
                        Intent I1 = new Intent();
                        I1.setAction("android.intent.action.notification");
                        startActivity(I1);
                        break;
                    case 1:
                        //跳转到空教室查询页面
                        Intent I2 = new Intent();
                        I2.setAction("android.intent.action.emptyClassroomEnquiry");
                        startActivity(I2);
                        break;
                    case 2:
                        //跳转到校园新闻页面
                        Intent I3 = new Intent();
                        I3.setAction("android.intent.action.news");
                        startActivity(I3);
                        break;
                }
            }
        });

        //底部软件信息，查看推送日志
        drawer.addFixedItem(new DrawerItem().setRoundedImage((BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.team), DrawerItem.SMALL_AVATAR).setTextPrimary(getString(R.string.Team)));

        //点击事件，跳转到信息页面
        drawer.setOnFixedItemClickListener(new DrawerItem.OnItemClickListener() {
            @Override
            public void onClick(DrawerItem item, long id, int position) {
                drawer.selectFixedItem(position);
                //跳转到日志
                Intent tmp = new Intent();
                tmp.setAction("android.intent.action.information");
                startActivity(tmp);
            }
        });


        //个人资料（没设计）
        drawer.addProfile(new DrawerProfile()
                .setId(1)
                .setRoundedAvatar((BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.head))
                .setBackground(ContextCompat.getDrawable(this, R.drawable.bg))
                .setName(getString(R.string.main))
                .setDescription(getString(R.string.person))
        );

        //点击暂无功能
        drawer.setOnProfileClickListener(new DrawerProfile.OnProfileClickListener() {
            @Override
            public void onClick(DrawerProfile profile, long id) {
                Toast.makeText(MainActivity.this, "暂时没啥功能", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //fab悬浮按钮初始化
    private void initFab() {
        menuGrey = findViewById(R.id.menu_grey);
        fab1 = findViewById(R.id.night);
        fab2 = findViewById(R.id.satelite);
        fab3 = findViewById(R.id.navigation);
        fab4 = findViewById(R.id.base);
        fab5=findViewById(R.id.map_inside);
        //点击外面关闭
        menuGrey.setClosedOnTouchOutside(true);
        //默认隐藏
        menuGrey.hideMenuButton(false);
        //夜间模式
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aMap.setMapType(AMap.MAP_TYPE_NIGHT);//夜景地图模式
            }
        });
        //卫星模式
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aMap.setMapType(AMap.MAP_TYPE_SATELLITE);// 卫星地图模式
            }
        });
        //导航模式
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aMap.setMapType(AMap.MAP_TYPE_NAVI);//导航地图模式
            }
        });
        //矢量模式（基本模式）
        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aMap.setMapType(AMap.MAP_TYPE_NORMAL);// 矢量地图模式
            }
        });

        //室内模式:没鸡儿用 这学校没室内地图
        fab5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        //延迟展示fab
        mUiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                menuGrey.showMenuButton(true);
            }
        }, 1000);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //抽屉按钮
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        //跳转到github主页
        if (item.getItemId() == R.id.action_github) {
            String url = "https://github.com/CZDXWX1226566881/LBS";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    //屏幕旋转
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    //activity加载完全后的回调函数
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }


    /**
     * 初始化AMap对象
     */
    private void initMap() {
        if (aMap == null) {
            aMap = mapView.getMap();
            registerListener();
            //定位
            mRouteSearch = new RouteSearch(this);
            mRouteSearch.setRouteSearchListener(this);
            mBottomLayout = findViewById(R.id.bottom_layout);
            //底部详细栏
            mRotueTimeDes = findViewById(R.id.firstline);
            mRouteDetailDes = findViewById(R.id.secondline);

            myLocationStyle = new MyLocationStyle();
            aMap.setMyLocationStyle(myLocationStyle);
            // 设置默认定位按钮是否显示
            aMap.getUiSettings().setMyLocationButtonEnabled(true);
            // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
            aMap.setMyLocationEnabled(true);
        }

        //添加两个标记点
        aMap.addMarker(new MarkerOptions().position(southwestLatLng));
        aMap.addMarker(new MarkerOptions().position(northeastLatLng));
        //缩放
        aMap.moveCamera(CameraUpdateFactory.zoomTo(8f));
        //设置限制区域
        LatLngBounds latLngBounds = new LatLngBounds(southwestLatLng, northeastLatLng);
        aMap.setMapStatusLimits(latLngBounds);

        // 定位、但不会移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。
        aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER));
        //设置回调
        aMap.setOnMyLocationChangeListener(this);
    }

    /**
     * 注册监听
     */
    private void registerListener() {
        aMap.setOnMapLongClickListener(MainActivity.this);
        aMap.setOnMarkerClickListener(MainActivity.this);
        aMap.setOnInfoWindowClickListener(MainActivity.this);
    }

    @Override
    //回调
    public void onMyLocationChange(Location location) {
        mStartPoint = new LatLonPoint(location.getLatitude(), location.getLongitude());
        // 定位回调监听
        if (location != null) {
            Log.e("amap", "onMyLocationChange 定位成功， lat: " + location.getLatitude() + " lon: " + location.getLongitude());
            Bundle bundle = location.getExtras();
            if (bundle != null) {
                int errorCode = bundle.getInt(MyLocationStyle.ERROR_CODE);
                String errorInfo = bundle.getString(MyLocationStyle.ERROR_INFO);
                // 定位类型，可能为GPS WIFI等，具体可以参考官网的定位SDK介绍
                int locationType = bundle.getInt(MyLocationStyle.LOCATION_TYPE);
                /*
                errorCode
                errorInfo
                locationType
                */
                Log.e("amap", "定位信息， code: " + errorCode + " errorInfo: " + errorInfo + " locationType: " + locationType);
            } else {
                Log.e("amap", "定位信息， bundle is null ");
            }
        } else {
            Log.e("amap", "定位失败");
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }



    @Override
    //点击标记小窗口，开始路径规划
    public void onInfoWindowClick(Marker m) {
        mEndPoint = new LatLonPoint(m.getPosition().latitude, m.getPosition().longitude);
        searchRouteResult(ROUTE_TYPE_WALK, RouteSearch.WalkDefault);
        mapView.setVisibility(View.VISIBLE);
    }

    @Override
    //点击后开始导航
    public boolean onMarkerClick(Marker arg0) {
        marker.showInfoWindow();
        return false;
    }


    @Override
    //地图上长按点击事件绘制maker
    public void onMapLongClick(LatLng latLng) {
        addMarker(aMap, latLng.latitude, latLng.longitude, latLng.toString());
    }

    //添加marker事件
    private void addMarker(AMap map, double latitude, double longitude, String title) {
        aMap.clear();
        MarkerOptions markerOptions = new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title("要去这里？")
                .snippet("点击即可导航")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                .draggable(true);
        marker = map.addMarker(markerOptions);
        startGrowAnimation();
    }

    /**
     * 地上生长的Marker动画
     */
    private void startGrowAnimation() {
        if (marker != null) {
            Animation animation = new ScaleAnimation(0, 1, 0, 1);
            animation.setInterpolator(new LinearInterpolator());
            //整个移动所需要的时间
            animation.setDuration(1000);
            //设置动画
            marker.setAnimation(animation);
            //开始动画
            marker.startAnimation();
        }
    }

    /**
     * 开始搜索路径规划方案
     */
    public void searchRouteResult(int routeType, int mode) {
        if (mStartPoint == null) {
            ToastUtil.show(mContext, "起点未设置");
            return;
        }
        if (mEndPoint == null) {
            ToastUtil.show(mContext, "终点未设置");
        }

        showProgressDialog();

        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(mStartPoint, mEndPoint);

        if (routeType == ROUTE_TYPE_WALK) {// 步行路径规划
            RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo);
            mRouteSearch.calculateWalkRouteAsyn(query);// 异步路径规划步行模式查询
        }

    }


    /**
     * 步行路线搜索结果方法回调
     */
    @Override
    public void onWalkRouteSearched(WalkRouteResult result, int errorCode) {
        dissmissProgressDialog();
        aMap.clear();// 清理地图上的所有覆盖物
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mWalkRouteResult = result;
                    final WalkPath walkPath = mWalkRouteResult.getPaths()
                            .get(0);
                    if (walkPath == null) {
                        return;
                    }
                    WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(
                            this, aMap, walkPath,
                            mWalkRouteResult.getStartPos(),
                            mWalkRouteResult.getTargetPos());
                    walkRouteOverlay.removeFromMap();
                    walkRouteOverlay.addToMap();
                    walkRouteOverlay.zoomToSpan();
                    mBottomLayout.setVisibility(View.VISIBLE);
                    int dis = (int) walkPath.getDistance();
                    int dur = (int) walkPath.getDuration();
                    String des = AMapUtil.getFriendlyTime(dur) + "(" + AMapUtil.getFriendlyLength(dis) + ")";
                    mRotueTimeDes.setText(des);
                    mRouteDetailDes.setVisibility(View.GONE);
                    mBottomLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext,
                                    WalkRouteDetailActivity.class);
                            intent.putExtra("walk_path", walkPath);
                            intent.putExtra("walk_result",
                                    mWalkRouteResult);
                            startActivity(intent);
                        }
                    });
                } else if (result != null && result.getPaths() == null) {
                    ToastUtil.show(mContext, R.string.no_result);
                }

            } else {
                ToastUtil.show(mContext, R.string.no_result);
            }
        } else {
            ToastUtil.showerror(this.getApplicationContext(), errorCode);
        }
    }


    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (progDialog == null) {
            progDialog = new ProgressDialog(this);
        }
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在搜索");
        progDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {
    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {
    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {
    }


}
