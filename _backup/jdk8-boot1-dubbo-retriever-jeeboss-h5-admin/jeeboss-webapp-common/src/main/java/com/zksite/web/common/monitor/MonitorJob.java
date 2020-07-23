package com.zksite.web.common.monitor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.SortedMap;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.common.utils.NetUtils;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.zksite.common.job.AbstractJob;
import com.zksite.common.job.model.JobInfo;
import com.zksite.web.common.monitor.entity.HistogramEntity;
import com.zksite.web.common.monitor.entity.MeterEntity;

public class MonitorJob extends AbstractJob {

    private static final String LOCAL_IP = NetUtils.getLocalHost(); // 本地IP

    @Autowired
    private MetricRegistry metricRegistry;

    @Autowired
    private MonitorConfigurer monitorConfigurer;

    @Autowired
    private MetricsRepository metricsRepository;

    private static final String METER_SUFFIX = "[meter]";

    private static final String HISTOGRAM_SUFFIX = "[histogram]";

    @Override
    protected JobInfo generateJob() {
        JobInfo job = new JobInfo();
        job.setGroup("monitor");
        job.setName(monitorConfigurer.getApplication());
        job.setInterval(monitorConfigurer.getInterval());
        job.setTimeUnit(monitorConfigurer.getTimeUnit());
        job.setRepeat(-1);
        job.setIsHAStandby(false);
        return job;
    }

    @Override
    protected void action() {
        saveData();
    }

    @Override
    protected void onStop() {
        saveData();
    }


    private void saveData() {
        SortedMap<String, Meter> meters = metricRegistry.getMeters();
        for (Map.Entry<String, Meter> me : meters.entrySet()) {
            String key = me.getKey();
            String name = key.substring(0, key.indexOf(METER_SUFFIX));
            Meter meter = me.getValue();
            MeterEntity meterEntity = new MeterEntity();
            meterEntity.setApplication(monitorConfigurer.getApplication());
            meterEntity.setCount(meter.getCount());
            meterEntity.setFifteenMinuteRate(new BigDecimal(meter.getFifteenMinuteRate())
                    .setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());

            meterEntity.setFiveMinuteRate(new BigDecimal(meter.getFiveMinuteRate())
                    .setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());
            meterEntity.setMean(new BigDecimal(meter.getMeanRate())
                    .setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());
            meterEntity.setName(name);
            meterEntity.setIp(LOCAL_IP);

            meterEntity.setOneMinuteRate(new BigDecimal(meter.getOneMinuteRate())
                    .setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());
            meterEntity.setStatDay(new Date());
            metricsRepository.saveMeter(meterEntity);
        }
        SortedMap<String, Histogram> histograms = metricRegistry.getHistograms();
        for (Map.Entry<String, Histogram> me : histograms.entrySet()) {
            String key = me.getKey();
            String name = key.substring(0, key.indexOf(HISTOGRAM_SUFFIX));
            Histogram histogram = me.getValue();
            HistogramEntity histogramEntity = new HistogramEntity();
            histogramEntity.setApplication(monitorConfigurer.getApplication());
            histogramEntity.setIp(LOCAL_IP);
            histogramEntity.setMax(histogram.getSnapshot().getMax());
            histogramEntity.setMin(histogram.getSnapshot().getMin());

            histogramEntity.setMean(new BigDecimal(histogram.getSnapshot().getMean())
                    .setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());

            histogramEntity.setMedian(new BigDecimal(histogram.getSnapshot().getMedian())
                    .setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());
            histogramEntity.setName(name);
            histogramEntity.setPercentile75(histogram.getSnapshot().get75thPercentile());
            histogramEntity.setPercentile95(histogram.getSnapshot().get95thPercentile());
            histogramEntity.setPercentile98(histogram.getSnapshot().get98thPercentile());
            histogramEntity.setPercentile99(histogram.getSnapshot().get99thPercentile());
            histogramEntity.setPercentile999(histogram.getSnapshot().get999thPercentile());

            histogramEntity.setStdDev(new BigDecimal(histogram.getSnapshot().getStdDev())
                    .setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());
            histogramEntity.setStatDay(new Date());
            metricsRepository.saveHistogram(histogramEntity);
        }
    }


}
