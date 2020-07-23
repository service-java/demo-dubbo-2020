package com.zksite.web.common.monitor.entity;

import java.io.Serializable;
import java.util.Date;

public class Metric implements Serializable {

    private static final long serialVersionUID = -1988410851608480705L;

    private Integer id;

    /**
     * 统计日
     */
    private Date statDay;

    /**
     * 统计时刻
     */
    private Date moment;

    private String application;

    private String ip;

    private String name;
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getStatDay() {
        return statDay;
    }

    public void setStatDay(Date statDay) {
        this.statDay = statDay;
    }

    public Date getMoment() {
        return moment;
    }

    public void setMoment(Date moment) {
        this.moment = moment;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}
