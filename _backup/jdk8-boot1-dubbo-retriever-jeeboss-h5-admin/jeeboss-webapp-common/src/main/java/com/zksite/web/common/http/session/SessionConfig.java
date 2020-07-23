package com.zksite.web.common.http.session;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import com.zksite.common.utils.SpringContextUtil;

public class SessionConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(SessionConfig.class);

	private static Environment environment = SpringContextUtil.getBean(Environment.class);

	private static final String SESSION_MAX_INACTIVE = "session.max.inactive";

	private static final Integer SESSION_MAX_INACTIVE_V = 3600;// 默认session超时.秒

	public static int getMaxInactive() {
		String property = environment.getProperty(SESSION_MAX_INACTIVE);
		if (StringUtils.isNotBlank(property)) {
			try {
				return Integer.valueOf(property);
			} catch (Exception e) {
				LOGGER.error("invalid session max inactive value to set.return default value:", SESSION_MAX_INACTIVE_V);
			}
		}
		return SESSION_MAX_INACTIVE_V;
	}

}
