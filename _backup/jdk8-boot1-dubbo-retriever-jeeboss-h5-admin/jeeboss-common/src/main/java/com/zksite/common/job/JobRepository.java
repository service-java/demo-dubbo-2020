package com.zksite.common.job;

import com.zksite.common.job.model.JobInfo;
import com.zksite.common.mybatis.Page;

/**
 * job仓库
 * 
 * @author hanjieHu
 *
 */
public interface JobRepository {


    /**
     * 添加一个job
     * 
     * @param job
     */
    void add(JobInfo job);


    /**
     * 列出所有job
     * 
     * @return
     */
    Page<JobInfo> list(Page<JobInfo> page);


    /**
     * 删除一个job
     * 
     * @param job
     */
    void delete(JobInfo jobInfo);


    void update(JobInfo jobInfo);

}
