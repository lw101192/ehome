package com.xm.Bean;

import java.io.Serializable;
import java.util.List;

public class MessageBean implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -3761115408873639528L;
    private long totallength;
    private String action;
    private UserBean from;
    private UserBean to;
    private String timestamp;
    private boolean receipt;
    private ContentBean content;
    private List<ConfigBean> configs;
    private String useragent;
    private ResultBean result;

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public long getTotallength() {
        return totallength;
    }

    public void setTotallength(long totallength) {
        this.totallength = totallength;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public UserBean getFrom() {
        return from;
    }

    public void setFrom(UserBean from) {
        this.from = from;
    }

    public UserBean getTo() {
        return to;
    }

    public void setTo(UserBean to) {
        this.to = to;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isReceipt() {
        return receipt;
    }

    public void setReceipt(boolean receipt) {
        this.receipt = receipt;
    }

    public ContentBean getContent() {
        return content;
    }

    public void setContent(ContentBean content) {
        this.content = content;
    }

    public List<ConfigBean> getConfigs() {
        return configs;
    }

    public void setConfigs(List<ConfigBean> configs) {
        this.configs = configs;
    }

    public String getUseragent() {
        return useragent;
    }

    public void setUseragent(String useragent) {
        this.useragent = useragent;
    }


}
