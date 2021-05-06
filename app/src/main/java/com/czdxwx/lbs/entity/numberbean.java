package com.czdxwx.lbs.entity;

import com.contrarywind.interfaces.IPickerViewData;

public class numberbean implements IPickerViewData {
    private int id;
    private String numberName;

    public numberbean() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumberName() {
        return numberName;
    }

    public void setNumberName(String numberName) {
        this.numberName = numberName;
    }

    public numberbean(int id, String numberName) {
        this.id = id;
        this.numberName = numberName;
    }

    @Override
    public String getPickerViewText() {
        return numberName;
    }
}
