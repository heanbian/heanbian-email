package com.heanbian.email;

/**
 * 邮件异常类
 * 
 * @author heanbian@heanbian.com
 * @since 1.0
 * @version 1.0
 */
@SuppressWarnings("serial")
public class HeanbianEmailException extends RuntimeException {

	public HeanbianEmailException() {
		super();
	}

	public HeanbianEmailException(String message) {
		super(message);
	}

	public HeanbianEmailException(String message, Throwable cause) {
		super(message, cause);
	}
}
