package com.org.ehome.bean;

/**
 * Created by liuwei on 2016/8/1.
 */
public class Tab {
    private String text;        //标签文字
    private Integer icon_normal;        //标签未被选中时的icon
    private Integer icon_pressed;        //标签被选中时的icon

    public Integer getIcon_pressed() {
        return icon_pressed;
    }

    public void setIcon_pressed(Integer icon_pressed) {
        this.icon_pressed = icon_pressed;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getIcon_normal() {
        return icon_normal;
    }

    public void setIcon_normal(Integer icon_normal) {
        this.icon_normal = icon_normal;
    }
}
