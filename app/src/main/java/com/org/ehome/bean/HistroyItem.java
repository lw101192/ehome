package com.org.ehome.bean;

/**
 * Created by liuwei on 2016/8/8.
 */
public class HistroyItem {
    private String id;      //设备ID
    private String nickname;      //设备名称
    private String lasttime;      //最新一条消息时间
    private String content;      //消息内容
    private String isread;      //是否已读
    private String type;      //消息类型（默认为machinenote）
    private String show;      //是否显示（默认为1）
    private int unreadnum;      //未读消息数目

    public HistroyItem() {
    }

    public HistroyItem(String id, String nickname, String lasttime, String content, String isread, String type, String show, int unreadnum) {
        this.id = id;
        this.nickname = nickname;
        this.lasttime = lasttime;
        this.content = content;
        this.isread = isread;
        this.type = type;
        this.show = show;
        this.unreadnum = unreadnum;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getLasttime() {
        return lasttime;
    }

    public void setLasttime(String lasttime) {
        this.lasttime = lasttime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIsread() {
        return isread;
    }

    public void setIsread(String isread) {
        this.isread = isread;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getShow() {
        return show;
    }

    public void setShow(String show) {
        this.show = show;
    }

    public int getUnreadnum() {
        return unreadnum;
    }

    public void setUnreadnum(int unreadnum) {
        this.unreadnum = unreadnum;
    }
}
