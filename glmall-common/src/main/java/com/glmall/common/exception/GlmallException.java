/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.glmall.common.exception;

import lombok.Data;

/**
 * 自定义异常
 *
 * @author Mark sunlightcs@gmail.com
 */
@Data
public class GlmallException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
    private String msg;
    private int code = 500;
    
    public GlmallException(String msg) {
		super(msg);
		this.msg = msg;
	}
	
	public GlmallException(String msg, Throwable e) {
		super(msg, e);
		this.msg = msg;
	}
	
	public GlmallException(String msg, int code) {
		super(msg);
		this.msg = msg;
		this.code = code;
	}
	
	public GlmallException(String msg, int code, Throwable e) {
		super(msg, e);
		this.msg = msg;
		this.code = code;
	}
	
	
}
