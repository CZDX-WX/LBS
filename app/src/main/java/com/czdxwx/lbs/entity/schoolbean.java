package com.czdxwx.lbs.entity;

import com.contrarywind.interfaces.IPickerViewData;

public class schoolbean implements IPickerViewData{
        int id;
        String schoolName;

    public schoolbean(int id, String schoolName) {
        this.id = id;
        this.schoolName = schoolName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    @Override
    public String getPickerViewText() {
        return schoolName;
    }
}
