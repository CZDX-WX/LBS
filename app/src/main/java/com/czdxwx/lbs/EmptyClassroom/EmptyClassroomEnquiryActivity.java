package com.czdxwx.lbs.EmptyClassroom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.czdxwx.lbs.R;
import com.czdxwx.lbs.entity.buildingbean;
import com.czdxwx.lbs.entity.numberbean;
import com.czdxwx.lbs.entity.schoolbean;
import com.czdxwx.lbs.entity.weekbean;
import com.czdxwx.lbs.entity.weekdaybean;

import net.tsz.afinal.FinalDb;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class EmptyClassroomEnquiryActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView school;
    private TextView building;
    private TextView week;
    private TextView weekday;
    private TextView number;
    private TextView temp = null;
    private Button btn_search;

    /*
    选择器
     */
    private OptionsPickerView pvCustomOptions;

    //选择器填充数据
    private ArrayList<schoolbean> schoolItem = new ArrayList<>();
    private ArrayList<buildingbean> buildingItem = new ArrayList<>();
    private ArrayList<weekbean> weekItem = new ArrayList<>();
    private ArrayList<weekdaybean> weekdayItem = new ArrayList<>();
    private ArrayList<numberbean> numberItem = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置状态栏颜色
        getWindow().setStatusBarColor(getResources().getColor(R.color.color_primary_dark));
        setContentView(R.layout.activity_empty_classroom_enquiry);


        //换标题栏
        setSupportActionBar(findViewById(R.id.toolbar));

        school = findViewById(R.id.school);
        school.setOnClickListener(this);
        building = findViewById(R.id.building);
        building.setOnClickListener(this);
        week = findViewById(R.id.week);
        week.setOnClickListener(this);
        weekday = findViewById(R.id.weekday);
        weekday.setOnClickListener(this);
        number = findViewById(R.id.number);
        number.setOnClickListener(this);
        btn_search = findViewById(R.id.btn_search);
        btn_search.setOnClickListener(this);

        schoolItem.add(new schoolbean(0, "科教城校区"));
        schoolItem.add(new schoolbean(1, "西太湖校区"));
        schoolItem.add(new schoolbean(2, "白云校区"));

        buildingItem.add(new buildingbean(0, "主楼"));
        buildingItem.add(new buildingbean(1, "副楼"));

        weekdayItem.add(new weekdaybean(0, "周日"));
        weekdayItem.add(new weekdaybean(1, "周一"));
        weekdayItem.add(new weekdaybean(2, "周二"));
        weekdayItem.add(new weekdaybean(3, "周三"));
        weekdayItem.add(new weekdaybean(4, "周四"));
        weekdayItem.add(new weekdaybean(5, "周五"));
        weekdayItem.add(new weekdaybean(6, "周六"));

        weekItem.add(new weekbean(0, "第一周"));
        weekItem.add(new weekbean(1, "第二周"));
        weekItem.add(new weekbean(2, "第三周"));
        weekItem.add(new weekbean(3, "第四周"));
        weekItem.add(new weekbean(4, "第五周"));
        weekItem.add(new weekbean(5, "第六周"));
        weekItem.add(new weekbean(6, "第七周"));
        weekItem.add(new weekbean(7, "第八周"));
        weekItem.add(new weekbean(8, "第九周"));
        weekItem.add(new weekbean(9, "第十周"));
        weekItem.add(new weekbean(10, "第十一周"));
        weekItem.add(new weekbean(11, "第十二周"));

        numberItem.add(new numberbean(0, "1-2节"));
        numberItem.add(new numberbean(1, "3-4节"));
        numberItem.add(new numberbean(2, "5-6节"));
        numberItem.add(new numberbean(3, "7-8节"));
        numberItem.add(new numberbean(4, "9-10节"));

    }

    @Override
    public void onClick(View v) {
        //判断：不是点击按钮全是选择器
        if (v.getId() == R.id.btn_search) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("school", school.getText().toString());
            bundle.putString("building", building.getText().toString());
            bundle.putString("week", week.getText().toString());
            bundle.putString("weekday", weekday.getText().toString());
            bundle.putString("number", number.getText().toString());
            //带个捆绑走
            intent.putExtras(bundle);
            //隐式跳转
            intent.setAction("android.intent.action.roomList");
            startActivity(intent);
        } else {
            //初始化选项选择器
            initCustomOptionPicker(v);
            pvCustomOptions.show(); //弹出自定义条件选择器
        }
    }

    /**
     * view:触发点击事件的视图
    初始化选项选择器
     */
    private void initCustomOptionPicker(View view) {
        temp = (TextView) view;
        pvCustomOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String tx = "";
                //判断一下是哪个文本视图触发了，从而转变相应的数据
                if (view.getId() == R.id.school) {
                    tx = schoolItem.get(options1).getPickerViewText();
                } else if (view.getId() == R.id.building) {
                    tx = buildingItem.get(options1).getPickerViewText();
                } else if (view.getId() == R.id.week) {
                    tx = weekItem.get(options1).getPickerViewText();
                } else if (view.getId() == R.id.weekday) {
                    tx = weekdayItem.get(options1).getPickerViewText();
                } else if (view.getId() == R.id.number) {
                    tx = numberItem.get(options1).getPickerViewText();
                }
                temp.setText(tx);
            }
        }).setLayoutRes(R.layout.pickerview_custom_options, new CustomListener() {
            @Override
            public void customLayout(View v) {
                final TextView tvSubmit = v.findViewById(R.id.tv_finish);
                ImageView ivCancel = v.findViewById(R.id.iv_cancel);
                //完成按钮
                tvSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pvCustomOptions.returnData();
                        //选择器消失
                        pvCustomOptions.dismiss();
                    }
                });
                //取消按钮
                ivCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //选择器消失
                        pvCustomOptions.dismiss();
                    }
                });

            }
        }).isDialog(true).setOutSideCancelable(false).build();

        if (view.getId() == R.id.school) {
            pvCustomOptions.setPicker(schoolItem);//添加校区
        } else if (view.getId() == R.id.building) {
            pvCustomOptions.setPicker(buildingItem);//添加教学楼
        } else if (view.getId() == R.id.week) {
            pvCustomOptions.setPicker(weekItem);//添加周数
        } else if (view.getId() == R.id.weekday) {
            pvCustomOptions.setPicker(weekdayItem);//添加周几
        } else if (view.getId() == R.id.number) {
            pvCustomOptions.setPicker(numberItem);//添加节数
        }

    }

}
