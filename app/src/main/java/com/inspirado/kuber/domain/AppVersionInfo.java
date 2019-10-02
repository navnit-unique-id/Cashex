package com.inspirado.kuber.domain;

import java.util.Date;

public class AppVersionInfo {
    Long id;
    int version;
    int updateCompulsion; //0-> can, 1-> should, 2-> must
    Date updateTime;
    String url;
    String url1;
    String updateInfo;


    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public int getVersion() {
        return version;
    }
    public void setVersion(int version) {
        this.version = version;
    }
    public Date getUpdateTime() {
        return updateTime;
    }
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getUpdateInfo() {
        return updateInfo;
    }
    public void setUpdateInfo(String updateInfo) {
        this.updateInfo = updateInfo;
    }
    public int getUpdateCompulsion() {
        return updateCompulsion;
    }
    public void setUpdateCompulsion(int updateCompulsion) {
        this.updateCompulsion = updateCompulsion;
    }

    public String getUrl1() {
        return url1;
    }

    public void setUrl1(String url1) {
        this.url1 = url1;
    }
}
