package com.czdxwx.lbs.entity;

import com.contrarywind.interfaces.IPickerViewData;

public class weekbean implements IPickerViewData {
    private int id;
    private String weekName;

    public weekbean(int id, String weekName) {
        this.id = id;
        this.weekName = weekName;
    }

    public String getWeekName() {
        return weekName;
    }

    public void setWeekName(String weekName) {
        this.weekName = weekName;
    }

    public weekbean() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }



    @Override
    public String getPickerViewText() {
        return weekName;
    }
}
