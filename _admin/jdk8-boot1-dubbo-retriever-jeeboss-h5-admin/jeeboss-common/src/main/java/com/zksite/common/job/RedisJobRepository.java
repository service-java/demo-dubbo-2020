package com.zksite.common.job;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.zksite.common.job.model.JobInfo;
import com.zksite.common.mybatis.Page;
import com.zksite.common.utils.JedisClient;


/**
 * 使用redis存储job信息
 * 
 * @author hanjieHu
 *
 */
@Component
public class RedisJobRepository implements JobRepository {

    private static final String JOB_BASIC_PATH = "jeeboss:job";

    @Autowired
    private JedisClient jedisClient;

    @Override
    public void add(JobInfo job) {
        jedisClient.hset(JOB_BASIC_PATH, job.getId(), JSON.toJSONString(job));
    }

    @Override
    public Page<JobInfo> list(Page<JobInfo> page) {
        if (page == null) {
            List<JobInfo> jobs = new ArrayList<>();
            Map<String, String> map = jedisClient.hgetAll(JOB_BASIC_PATH);
            for (Map.Entry<String, String> me : map.entrySet()) {
                jobs.add(JSON.parseObject(me.getValue(), JobInfo.class));
            }
            page = new Page<JobInfo>();
            page.setList(jobs);
            return page;
        } else {
            return listByPage(page);
        }

    }

    private Page<JobInfo> listByPage(Page<JobInfo> page) {
        List<JobInfo> jobs = new ArrayList<>();
        Set<String> keys = jedisClient.hkeys(JOB_BASIC_PATH);
        if (keys.size() < page.getPageSize()) {
            if (page.getPageNo() == 1) {
                for (String string : keys) {
                    String json = jedisClient.hget(JOB_BASIC_PATH, string);
                    if (StringUtils.isNotBlank(json)) {
                        jobs.add(JSON.parseObject(json, JobInfo.class));
                    }
                }
                page.setCount(keys.size());
            } else {
                page.setCount(keys.size());
            }
        } else {
            int start = page.getPageNo() - 1 * page.getPageSize();// 包含
            int end = page.getPageNo() * page.getPageSize();// 不包含
            if (keys.size() < start) {
                page.setCount(keys.size());
            } else {
                if (keys.size() < end) {
                    String[] strs = new String[keys.size()];
                    keys.toArray(strs);
                    for (int i = start; i < strs.length; i++) {
                        String json = jedisClient.get(strs[i]);
                        jobs.add(JSON.parseObject(json, JobInfo.class));
                    }
                    page.setCount(keys.size());
                } else {
                    String[] strs = new String[keys.size()];
                    keys.toArray(strs);
                    for (int i = start; i < end; i++) {
                        String json = jedisClient.get(strs[i]);
                        jobs.add(JSON.parseObject(json, JobInfo.class));
                    }
                    page.setCount(keys.size());
                }
            }
        }
        page.setList(jobs);
        return page;
    }


    @Override
    public void delete(JobInfo jobInfo) {
        jedisClient.hdel(JOB_BASIC_PATH, jobInfo.getId());
    }

    public String getKey(String name, String group) {
        if (StringUtils.isBlank(group)) {
            return JOB_BASIC_PATH + name;
        }
        return JOB_BASIC_PATH + group + ":" + name;
    }

    @Override
    public void update(JobInfo jobInfo) {
        jedisClient.hset(JOB_BASIC_PATH, jobInfo.getId(), JSON.toJSONString(jobInfo));
    }

}
