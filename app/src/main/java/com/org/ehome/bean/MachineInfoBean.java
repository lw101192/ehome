package com.org.ehome.bean;

public class MachineInfoBean {
    private String MachineID;
    private String MachineNickName;
    private String CreateTime;
    private String Online;

    public String getMachineID() {
        return MachineID;
    }

    public void setMachineID(String machineID) {
        MachineID = machineID;
    }

    public String getMachineNickName() {
        return MachineNickName;
    }

    public void setMachineNickName(String machineNickName) {
        MachineNickName = machineNickName;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public String getOnline() {
        return Online;
    }

    public void setOnline(String online) {
        Online = online;
    }


}
