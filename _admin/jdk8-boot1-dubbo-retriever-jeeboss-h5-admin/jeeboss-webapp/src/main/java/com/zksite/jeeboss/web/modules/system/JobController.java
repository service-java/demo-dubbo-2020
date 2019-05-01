package com.zksite.jeeboss.web.modules.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.zksite.common.constant.ErrorCode;
import com.zksite.common.job.JobManager;
import com.zksite.common.job.model.JobInfo;
import com.zksite.common.mybatis.Page;
import com.zksite.web.common.model.ResponseModel;

@RestController
public class JobController {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobController.class);

    @Autowired
    private JobManager JobManager;

    @RequestMapping(value = "jobs", method = RequestMethod.GET)
    public ResponseModel list(Page<JobInfo> page) {
        try {
            Page<JobInfo> list = JobManager.list(page);
            return new ResponseModel(list);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseModel(ErrorCode.SYSTEM_ERROR, null);
        }
    }

    @RequestMapping(value = "updateJob", method = RequestMethod.POST)
    public ResponseModel update(JobInfo jobInfo) {
        try {
            JobManager.updateStatus(jobInfo);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseModel(ErrorCode.SYSTEM_ERROR);
        }
        return new ResponseModel();
    }

}
