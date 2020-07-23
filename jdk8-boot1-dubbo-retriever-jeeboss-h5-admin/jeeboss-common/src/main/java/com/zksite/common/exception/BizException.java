package com.zksite.common.exception;

import com.zksite.common.constant.ErrorCode;

public class BizException extends Exception {
	private static final long serialVersionUID = 4745557949097429001L;

	// 错误代码和错误消息间隔符号，可用于从exception.getMessage()中反向解析出代码和消息
	public static final String MESSAGE_SPLIT_CHAR = ":";

	private String errcode;
	private String errm;
	private String errLogMsg;

	public BizException(String errcode) {
		super(errcode);
		this.errcode = errcode;
		this.errm = "";
	}

	public BizException(String errcode, String errm) {
		super(errcode + MESSAGE_SPLIT_CHAR + errm);
		this.errcode = errcode;
		this.errm = errm;
	}

	public BizException(String errcode, String errm, String errLogMsg) {
		super(errcode + MESSAGE_SPLIT_CHAR + errm + MESSAGE_SPLIT_CHAR + errLogMsg);
		this.errcode = errcode;
		this.errm = errm;
		this.errLogMsg = errLogMsg;
	}

	public BizException(ErrorCode errorCode) {
		this(errorCode.getErrcode(), errorCode.getErrm());
	}

	public BizException(ErrorCode errorCode, String errLogMsg) {
		this(errorCode.getErrcode(), errorCode.getErrm(), errLogMsg);
	}

	public String getErrcode() {
		return errcode;
	}

	public void setErrcode(String errcode) {
		this.errcode = errcode;
	}

	public String getErrm() {
		return errm;
	}

	public void setErrm(String errm) {
		this.errm = errm;
	}

	public String getErrLogMsg() {
		return errLogMsg;
	}

	public void setErrLogMsg(String errLogMsg) {
		this.errLogMsg = errLogMsg;
	}
}
