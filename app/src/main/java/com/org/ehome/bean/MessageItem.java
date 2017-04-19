package com.org.ehome.bean;

/**
 * Created by liuwei on 2016/8/10.
 */
public class MessageItem {
    private String content;     //消息内容
    private String createtime;      //消息时间

    public MessageItem(String content, String createtime) {
        this.content = content;
        this.createtime = createtime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }
}
