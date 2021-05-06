package com.czdxwx.lbs.entity;

import com.contrarywind.interfaces.IPickerViewData;

public class weekdaybean implements IPickerViewData {
    private int id;
    private String weekdayName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWeekdayName() {
        return weekdayName;
    }

    public void setWeekdayName(String weekdayName) {
        this.weekdayName = weekdayName;
    }

    public weekdaybean(int id, String weekdayName) {
        this.id = id;
        this.weekdayName = weekdayName;
    }

    @Override
    public String getPickerViewText() {
        return weekdayName;
    }
}
