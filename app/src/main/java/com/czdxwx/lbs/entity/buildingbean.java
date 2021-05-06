package com.czdxwx.lbs.entity;

import com.contrarywind.interfaces.IPickerViewData;

public class buildingbean implements IPickerViewData {
    private int id;
    private String buildingName;

    public buildingbean(int id, String buildingName) {
        this.id = id;
        this.buildingName = buildingName;
    }

    public buildingbean() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    @Override
    public String getPickerViewText() {
        return buildingName;
    }
}
