package com.zksite.web.common.monitor.entity;

public class MeterEntity extends Metric {

    /**
     * 
     */
    private static final long serialVersionUID = -6234734430780636755L;


    /**
     * 平均值
     */
    private Double mean;

    /**
     * 总数
     */
    private Long count;

    private Double oneMinuteRate;

    private Double fiveMinuteRate;

    private Double fifteenMinuteRate;


    public Double getMean() {
        return mean;
    }

    public void setMean(Double mean) {
        this.mean = mean;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Double getOneMinuteRate() {
        return oneMinuteRate;
    }

    public void setOneMinuteRate(Double oneMinuteRate) {
        this.oneMinuteRate = oneMinuteRate;
    }

    public Double getFiveMinuteRate() {
        return fiveMinuteRate;
    }

    public void setFiveMinuteRate(Double fiveMinuteRate) {
        this.fiveMinuteRate = fiveMinuteRate;
    }

    public Double getFifteenMinuteRate() {
        return fifteenMinuteRate;
    }

    public void setFifteenMinuteRate(Double fifteenMinuteRate) {
        this.fifteenMinuteRate = fifteenMinuteRate;
    }

}
