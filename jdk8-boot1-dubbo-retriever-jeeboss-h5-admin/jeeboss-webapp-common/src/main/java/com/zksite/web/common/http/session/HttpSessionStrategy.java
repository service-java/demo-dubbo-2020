package com.zksite.web.common.http.session;

import javax.servlet.http.HttpServletRequest;

/**
 * session 策略
 * 
 * @author hanjieHu
 *
 */
public interface HttpSessionStrategy {

	/**
	 * 获取sessionid
	 * 
	 * @param request
	 * @return
	 */
	String getRequestedSessionId(HttpServletRequest request);
}
