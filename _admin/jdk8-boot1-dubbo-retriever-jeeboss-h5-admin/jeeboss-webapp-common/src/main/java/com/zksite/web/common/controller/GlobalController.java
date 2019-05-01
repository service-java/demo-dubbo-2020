package com.zksite.web.common.controller;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

@ControllerAdvice
public class GlobalController {

    private Logger logger = LoggerFactory.getLogger(GlobalController.class);

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // 日期类型绑定
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                if (logger.isDebugEnabled()) {
                    logger.debug("parsing {} to java.util.Date", text);
                }

                if (StringUtils.isEmpty(text)) {
                    setValue(null);
                } else {
                    // 支持long类型参数
                    if (StringUtils.isNumeric(text)) {
                        long time = Long.parseLong(text) * 1000;
                        Date date = new Date();
                        date.setTime(time);
                        setValue(date);
                    }
                    // 支持各种格式字符串
                    else {
                        try {
                            Date date = DateUtils.parseDate(text.trim(), "yyyy-MM-dd", "yyyy/MM/dd", "yyyy-MM-dd HH:mm:ss",
                                    "yyyy-MM-dd HH:mm");
                            setValue(date);
                        } catch (ParseException e) {
                            logger.error("parsing {} to java.util.Date error: {}", text, e.getMessage());
                            setValue(null);
                        }
                    }
                }
            }
        });
    }
}
