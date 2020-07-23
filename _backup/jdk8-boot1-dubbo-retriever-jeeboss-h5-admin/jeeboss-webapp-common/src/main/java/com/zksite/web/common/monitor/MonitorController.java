package com.zksite.web.common.monitor;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.zksite.web.common.model.ResponseModel;
import com.zksite.web.common.monitor.entity.HistogramEntity;
import com.zksite.web.common.monitor.entity.MeterEntity;

@RestController
@RequestMapping("/monitor")
public class MonitorController {

    @Autowired(required = false)
    private MetricsRepository metricsRepository;

    @RequestMapping(value = "/meters/{date}/applications")
    public ResponseModel listMeterApplications(@PathVariable("date") Date date) {
        List<MeterEntity> list = metricsRepository.listMeterApplications(date, null);
        return new ResponseModel(list);
    }

    @RequestMapping(value = "/meters/{date}/{application}/applications")
    public ResponseModel listMeterApplications(@PathVariable("date") Date date,
            @PathVariable("application") String application) {
        List<MeterEntity> list = metricsRepository.listMeterApplications(date, application);
        return new ResponseModel(list);
    }

    @RequestMapping(value = "/meters", method = RequestMethod.GET)
    public ResponseModel findMeters(MeterEntity meter) {
        if (meter.getStatDay() == null) {
            try {
                meter.setStatDay(DateUtils
                        .parseDate(DateFormatUtils.format(new Date(), "yyyy-MM-dd"), "yyyy-MM-dd"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        List<MeterEntity> list = metricsRepository.findMeter(meter);
        return new ResponseModel(list);
    }

    @RequestMapping(value = "/historgrams/{date}/applications", method = RequestMethod.GET)
    public ResponseModel listHistogramApplications(@PathVariable("date") Date date) {
        List<HistogramEntity> list = metricsRepository.listHistogramApplications(date, null);
        return new ResponseModel(list);
    }

    @RequestMapping(value = "/historgrams/{date}/{application}/applications", method = RequestMethod.GET)
    public ResponseModel listHistogramApplications(@PathVariable("date") Date date,
            @PathVariable("application") String application) {
        List<HistogramEntity> list = metricsRepository.listHistogramApplications(date, application);
        return new ResponseModel(list);
    }


    @RequestMapping(value = "/historgrams", method = RequestMethod.GET)
    public ResponseModel findHistorgrams(HistogramEntity histogram) {
        if (histogram.getStatDay() == null) {
            try {
                histogram.setStatDay(DateUtils
                        .parseDate(DateFormatUtils.format(new Date(), "yyyy-MM-dd"), "yyyy-MM-dd"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        List<HistogramEntity> list = metricsRepository.findHistogram(histogram);
        return new ResponseModel(list);
    }

}
