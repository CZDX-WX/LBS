package com.czdxwx.lbs;


import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.heinrichreimersoftware.materialdrawer.DrawerView;
import com.heinrichreimersoftware.materialdrawer.structure.DrawerHeaderItem;
import com.heinrichreimersoftware.materialdrawer.structure.DrawerItem;
import com.heinrichreimersoftware.materialdrawer.structure.DrawerProfile;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;
import static com.czdxwx.lbs.MyApplication.getContext;

public class MainActivity extends AppCompatActivity implements AMap.OnMyLocationChangeListener, AMap.OnMapClickListener,
        AMap.OnMarkerClickListener, AMap.OnInfoWindowClickListener, AMap.InfoWindowAdapter, RouteSearch.OnRouteSearchListener {
    //Logcat TAG
    private String TAG = "test";
    //悬浮框按钮
    private FloatingActionMenu menuGrey;
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    private FloatingActionButton fab3;
    private FloatingActionButton fab4;
    //动画处理器
    private final Handler mUiHandler = new Handler();
    //toolbar
    private Toolbar toolbar;
    //抽屉布局
    private DrawerView drawer;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private MapView mapView;
    //地图
    private AMap aMap;
    private Context mContext;
    //定位
    private MyLocationStyle myLocationStyle;
    //路径规划
    private RouteSearch mRouteSearch;
    private WalkRouteResult mWalkRouteResult;
    //起点
    private LatLonPoint mStartPoint = new LatLonPoint(39.942295, 116.335891);//起点，116.335891,39.942295
    //终点
    private LatLonPoint mEndPoint = new LatLonPoint(39.995576, 116.481288);//终点，116.481288,39.995576
    private final int ROUTE_TYPE_WALK = 3;
    // 西南坐标(纬度，经度)
    private static final LatLng southwestLatLng = new LatLng(31.681177, 119.95012);
    // 东北坐标
    private static final LatLng northeastLatLng = new LatLng(31.687828, 119.965239);



    @RequiresApi(api = Build.VERSION_CODES.M)
    void Request() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, String.valueOf(requestCode));
        switch(requestCode) {
//            case INTERNET:
//                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // TODO request success
//                }
//                break;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapView = findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);

        toolbar = findViewById(R.id.toolbar);
        //换标题栏
        setSupportActionBar(toolbar);

        //初始化抽屉布局
        initDrawer();
        //初始化地图
        initMap();
        //初始化悬浮框
        initFab();
    }

    //初始化抽屉
    private void initDrawer() {
        drawerLayout = findViewById(R.id.drawerLayout);
        drawer = findViewById(R.id.drawer);
        //抽屉开关
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu();
            }

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

                        break;
                    case 1:
                        //跳转到空教室查询页面

                        break;
                    case 2:
                        //跳转到校园新闻页面

                        break;
                }
            }
        });

        //底部软件信息
        drawer.addFixedItem(new DrawerItem().setRoundedImage((BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.team), DrawerItem.SMALL_AVATAR).setTextPrimary(getString(R.string.Team)));

        drawer.setOnFixedItemClickListener(new DrawerItem.OnItemClickListener() {
            @Override
            public void onClick(DrawerItem item, long id, int position) {
                drawer.selectFixedItem(position);
                Toast.makeText(MainActivity.this, "没写功能", Toast.LENGTH_SHORT).show();
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

    //fab初始化
    private void initFab() {
        menuGrey = findViewById(R.id.menu_grey);
        fab1 = findViewById(R.id.night);
        fab2 = findViewById(R.id.satelite);
        fab3 = findViewById(R.id.navigation);
        fab4 = findViewById(R.id.base);
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
            //定位
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
//        aMap.setMapStatusLimits(latLngBounds);

        // 定位、但不会移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。
        aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE));
        //设置回调
//        aMap.setOnMyLocationChangeListener(this);
        aMap.setOnMyLocationChangeListener(this);
    }

    @Override
    //回调
    public void onMyLocationChange(Location location) {
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
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {

    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }


}
