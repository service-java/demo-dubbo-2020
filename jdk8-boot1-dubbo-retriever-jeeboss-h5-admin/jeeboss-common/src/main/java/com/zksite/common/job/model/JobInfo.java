package com.zksite.common.job.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class JobInfo {

    private String id;

    private String name;

    private String group;

    private boolean isHAEnable = true; // 是否启用HA模式支持

    private boolean isHAStandby = true;// HA模式下，是否standby模式（是-主备模式，否-全活模式）

    /************** 简单的重复执行 ****************/
    private int interval;// 执行间隔

    private TimeUnit timeUnit;// 执行间隔时间单位

    private int repeat;// 重复执行次数,-1重复执行

    /******************************/
    private String cron;// cron表达式

    private String path;

    private String host;

    private String value;

    private Map<Object, Object> data = new HashMap<>();

    private List<String> standbyList = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public boolean getIsHAEnable() {
        return isHAEnable;
    }

    public void setIsHAEnable(boolean isHAEnable) {
        this.isHAEnable = isHAEnable;
    }

    public boolean getIsHAStandby() {
        return isHAStandby;
    }

    public void setIsHAStandby(boolean isHAStandby) {
        this.isHAStandby = isHAStandby;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public int getRepeat() {
        return repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }


    public Map<Object, Object> getData() {
        return data;
    }

    public void setData(Map<Object, Object> data) {
        this.data = data;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getStandbyList() {
        return standbyList;
    }

    public void setStandbyList(List<String> standbyList) {
        this.standbyList = standbyList;
    }
}
