package com.example.carrental.models;

public class Brand {
    private String name;
    private int iconRes;
    private boolean isSelected;

    public Brand(String name, int iconRes, boolean isSelected) {
        this.name = name;
        this.isSelected = isSelected;
    }

    public String getName() { return name; }
    public int getIconRes() { return iconRes; }
    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }
}