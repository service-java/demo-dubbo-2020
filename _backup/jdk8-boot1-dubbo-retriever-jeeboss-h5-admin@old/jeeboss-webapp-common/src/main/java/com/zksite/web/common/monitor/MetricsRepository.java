package com.zksite.web.common.monitor;

import java.util.Date;
import java.util.List;

import com.zksite.web.common.monitor.entity.HistogramEntity;
import com.zksite.web.common.monitor.entity.MeterEntity;

public interface MetricsRepository {

    /**
     * 获取applicationName ip day最后一个统计meter
     * 
     * @param applicationName
     * @param ip
     * @param day
     * @return
     */
    MeterEntity getLastMeter(String applicationName, String ip, String name);

    HistogramEntity getLastHistogram(String applicationName, String ip, String name);

    void saveMeter(MeterEntity meter);

    void saveHistogram(HistogramEntity histogram);

    List<HistogramEntity> findHistogram(HistogramEntity histogram);

    List<MeterEntity> findMeter(MeterEntity meter);

    List<MeterEntity> listMeterApplications(Date date, String application);

    List<HistogramEntity> listHistogramApplications(Date date, String application);
}
