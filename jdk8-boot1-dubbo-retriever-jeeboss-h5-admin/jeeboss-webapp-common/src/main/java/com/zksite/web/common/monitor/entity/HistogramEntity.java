package com.zksite.web.common.monitor.entity;

public class HistogramEntity extends Metric {

    private static final long serialVersionUID = 2384637226732642412L;

    private Long min;

    private Long max;

    private Double mean;

    private Double stdDev;

    private Double percentile999;

    private Double percentile99;

    private Double percentile98;

    private Double percentile95;

    private Double percentile75;

    private Double median;


    public Long getMin() {
        return min;
    }

    public void setMin(Long min) {
        this.min = min;
    }

    public Long getMax() {
        return max;
    }

    public void setMax(Long max) {
        this.max = max;
    }

    public Double getMean() {
        return mean;
    }

    public void setMean(Double mean) {
        this.mean = mean;
    }

    public Double getStdDev() {
        return stdDev;
    }

    public void setStdDev(Double stdDev) {
        this.stdDev = stdDev;
    }

    public Double getPercentile999() {
        return percentile999;
    }

    public void setPercentile999(Double percentile999) {
        this.percentile999 = percentile999;
    }

    public Double getPercentile99() {
        return percentile99;
    }

    public void setPercentile99(Double percentile99) {
        this.percentile99 = percentile99;
    }

    public Double getPercentile98() {
        return percentile98;
    }

    public void setPercentile98(Double percentile98) {
        this.percentile98 = percentile98;
    }

    public Double getPercentile95() {
        return percentile95;
    }

    public void setPercentile95(Double percentile95) {
        this.percentile95 = percentile95;
    }

    public Double getPercentile75() {
        return percentile75;
    }

    public void setPercentile75(Double percentile75) {
        this.percentile75 = percentile75;
    }

    public Double getMedian() {
        return median;
    }

    public void setMedian(Double median) {
        this.median = median;
    }
    
}
