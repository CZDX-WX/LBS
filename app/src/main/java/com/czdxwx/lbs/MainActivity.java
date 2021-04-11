package com.czdxwx.lbs;


import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.heinrichreimersoftware.materialdrawer.DrawerView;
import com.heinrichreimersoftware.materialdrawer.structure.DrawerHeaderItem;
import com.heinrichreimersoftware.materialdrawer.structure.DrawerItem;
import com.heinrichreimersoftware.materialdrawer.structure.DrawerProfile;

public class MainActivity extends AppCompatActivity {
    private DrawerView drawer;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        drawer = findViewById(R.id.drawer);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

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
                switch (position){
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

        drawer.addFixedItem(new DrawerItem().setRoundedImage((BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.team), DrawerItem.SMALL_AVATAR).setTextPrimary(getString(R.string.Team)));


        drawer.setOnFixedItemClickListener(new DrawerItem.OnItemClickListener() {
            @Override
            public void onClick(DrawerItem item, long id, int position) {
                drawer.selectFixedItem(position);
                Toast.makeText(MainActivity.this,"没写功能", Toast.LENGTH_SHORT).show();
            }
        });


        //缩略图
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
                Toast.makeText(MainActivity.this,"暂时没啥功能", Toast.LENGTH_SHORT).show();
            }
        });


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
}
