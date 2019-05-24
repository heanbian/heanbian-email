package com.heanbian.email;

/**
 * 邮件异常类
 * 
 * @author heanbian@heanbian.com
 * @since 1.0
 * @version 1.0
 */
@SuppressWarnings("serial")
public class HEmailException extends RuntimeException {

	public HEmailException() {
		super();
	}

	public HEmailException(String message) {
		super(message);
	}

	public HEmailException(String message, Throwable cause) {
		super(message, cause);
	}
}
