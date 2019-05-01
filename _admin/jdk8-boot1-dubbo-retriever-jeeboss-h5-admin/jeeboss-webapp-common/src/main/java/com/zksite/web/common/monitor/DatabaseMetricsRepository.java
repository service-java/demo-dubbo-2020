package com.zksite.web.common.monitor;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.zksite.web.common.monitor.entity.HistogramEntity;
import com.zksite.web.common.monitor.entity.MeterEntity;
import com.zksite.web.common.monitor.mapper.MetricsDao;

public class DatabaseMetricsRepository implements MetricsRepository {

    @Autowired
    private MetricsDao metricDao;

    @Override
    public MeterEntity getLastMeter(String application, String ip, String name) {
        return metricDao.getLastMeter(application, ip, name);
    }

    @Override
    public HistogramEntity getLastHistogram(String applicationName, String ip, String name) {
        return metricDao.getLastHistogram(applicationName, ip, name);
    }

    @Override
    public void saveMeter(MeterEntity meter) {
        metricDao.saveMeter(meter);
    }

    @Override
    public void saveHistogram(HistogramEntity histogram) {
        metricDao.saveHistogram(histogram);
    }

    @Override
    public List<HistogramEntity> findHistogram(HistogramEntity histogram) {
        return metricDao.findHistogram(histogram);
    }

    @Override
    public List<MeterEntity> findMeter(MeterEntity meter) {
        return metricDao.findMeter(meter);
    }

    @Override
    public List<MeterEntity> listMeterApplications(Date date, String application) {
        return metricDao.listMeterApplications(date, application);
    }

    @Override
    public List<HistogramEntity> listHistogramApplications(Date date, String application) {
        return metricDao.listHistogramApplications(date, application);
    }
}
