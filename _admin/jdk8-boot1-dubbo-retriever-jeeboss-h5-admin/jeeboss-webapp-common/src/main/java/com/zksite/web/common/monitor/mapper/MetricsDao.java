package com.zksite.web.common.monitor.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.zksite.web.common.monitor.annotation.MetricDao;
import com.zksite.web.common.monitor.entity.HistogramEntity;
import com.zksite.web.common.monitor.entity.MeterEntity;

@MetricDao
public interface MetricsDao {

    @Select("SELECT * FROM `monitor_meter` m WHERE m.`application`= #{application} AND m.`name` =#{name} and m.ip=#{ip} ORDER BY moment DESC LIMIT 1")
    MeterEntity getLastMeter(@Param("application") String application, @Param("ip") String ip,
            @Param("name") String name);


    @Select("SELECT * FROM `monitor_histogram` m WHERE m.`application`= #{application} AND m.`name` =#{name} and m.ip=#{ip} ORDER BY moment DESC LIMIT 1")
    HistogramEntity getLastHistogram(@Param("application") String applicationName,
            @Param("ip") String ip, @Param("name") String name);

    @Insert("INSERT INTO `monitor_meter`(stat_day,moment,application,`name`,ip,mean,`count`,one_minute_rate,five_minute_rate,fifteen_minute_rate)VALUES(#{statDay},#{moment},#{application},#{name},#{ip},#{mean},#{count},#{oneMinuteRate},#{fiveMinuteRate},#{fifteenMinuteRate})")
    void saveMeter(MeterEntity meter);

    @Insert("INSERT INTO `monitor_histogram`(stat_day,moment,application,`name`,ip,`min`,`max`,mean,std_dev,percentile_999,percentile_99,percentile_98,percentile_95,percentile_75,median)VALUES(#{statDay},#{moment},#{application},#{name},#{ip},#{min},#{max},#{mean},#{stdDev},#{percentile999},#{percentile99},#{percentile98},#{percentile95},#{percentile75},#{median})")
    void saveHistogram(HistogramEntity histogram);


    List<HistogramEntity> findHistogram(HistogramEntity histogram);


    List<MeterEntity> findMeter(MeterEntity meter);


    List<MeterEntity> listMeterApplications(@Param("date") Date date,
            @Param("application") String application);


    List<HistogramEntity> listHistogramApplications(@Param("date") Date date,
            @Param("application") String application);



}
